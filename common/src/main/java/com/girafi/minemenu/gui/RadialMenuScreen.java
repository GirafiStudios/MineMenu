package com.girafi.minemenu.gui;

import com.girafi.minemenu.data.menu.MenuItem;
import com.girafi.minemenu.data.menu.RadialMenu;
import com.girafi.minemenu.helper.AngleHelper;
import com.girafi.minemenu.helper.KeyboardHandlerHelper;
import com.girafi.minemenu.menu.MenuItemScreen;
import com.girafi.minemenu.util.Config;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RadialMenuScreen extends Screen {
    public static final RadialMenuScreen INSTANCE = new RadialMenuScreen();
    public static boolean active = false;
    public static final double ANGLE_PER_ITEM = 360F / RadialMenu.MAX_ITEMS;
    private static final float OUTER_RADIUS = 80;
    private static final float INNER_RADIUS = 60;

    public RadialMenuScreen() {
        super(Component.translatable("mine_menu.radialMenu.title"));
    }

    public static void activate() {
        if (Minecraft.getInstance().screen == null) {
            active = true;
            Minecraft.getInstance().setScreen(INSTANCE);
        }
    }

    public static void deactivate() {
        if (Minecraft.getInstance().screen == INSTANCE) {
            active = false;
            Minecraft.getInstance().setScreen(null);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent buttonEvent, boolean isDoubleClick) {
        if (active && RadialMenu.animationTimer == 0) {
            double mouseAngle = AngleHelper.getMouseAngle();
            Minecraft mc = this.minecraft;
            mouseAngle -= ANGLE_PER_ITEM / 2;
            mouseAngle = 360 - mouseAngle;
            mouseAngle = AngleHelper.correctAngle(mouseAngle);

            if (mc != null && !mc.options.hideGui) {
                for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
                    double currAngle = ANGLE_PER_ITEM * i;
                    double nextAngle = currAngle + ANGLE_PER_ITEM;
                    currAngle = AngleHelper.correctAngle(currAngle);
                    nextAngle = AngleHelper.correctAngle(nextAngle);

                    boolean mouseIn = mouseAngle > currAngle && mouseAngle < nextAngle;

                    if (mouseIn) {
                        MenuItem menuItem = RadialMenu.getActiveArray()[i];
                        if (menuItem != null) {
                            if (buttonEvent.hasShiftDown() || (Config.GENERAL.rightClickToEdit.get() && buttonEvent.button() == 1)) {
                                deactivate();
                                ScreenStack.push(new MenuItemScreen(i, menuItem));
                                return true;
                            } else {
                                if (buttonEvent.button() == 0) {
                                    if (menuItem.clickAction.deactivates()) {
                                        deactivate();
                                        menuItem.clickAction.onClicked();
                                        return true;
                                    } else {
                                        menuItem.clickAction.onClicked();
                                        return true;
                                    }
                                }
                            }
                        } else {
                            if (buttonEvent.button() == 0) {
                                deactivate();
                                ScreenStack.push(new MenuItemScreen(i, menuItem));
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void removed() {
        super.removed();
        active = false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && !mc.options.hideGui && !mc.isPaused() && RadialMenuScreen.active) {
            guiGraphics.guiRenderState.submitGuiElement(new RadialMenuBackgroundElement(RenderPipelines.GUI, TextureSetup.noTexture(), guiGraphics.pose(), this.width / 2, this.height / 2, null));
            renderItems(guiGraphics);
            renderText(guiGraphics);
        }
    }

    public void renderItems(GuiGraphics guiGraphics) {
        int x = this.width / 2;
        int y = this.height / 2;

        for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
            MenuItem item = RadialMenu.getActiveArray()[i];
            Item menuButton = BuiltInRegistries.ITEM.getValue(Identifier.parse(Config.GENERAL.menuButtonIcon.get()));
            ItemStack stack = (item != null && !item.icon.isEmpty()) ? item.icon : (menuButton == null ? ItemStack.EMPTY : new ItemStack(menuButton));

            double angle = (ANGLE_PER_ITEM * i) + 90;
            double drawOffset = 1.5;
            double drawX = INNER_RADIUS - RadialMenu.animationTimer + drawOffset;
            double drawY = INNER_RADIUS - RadialMenu.animationTimer + drawOffset;

            double length = Math.sqrt(drawX * drawX + drawY * drawY);

            drawX = -(length * Math.cos(Math.toRadians(angle)));
            drawY = (length * Math.sin(Math.toRadians(angle)));

            if (!stack.isEmpty()) {
                int itemX = (int) (drawX + x);
                int itemY = (int) (drawY + y);
                itemX -= 8;
                itemY -= 8;
                guiGraphics.renderItem(stack, itemX, itemY);
            }
        }
    }

    public void renderText(GuiGraphics guiGraphics) {
        double mouseAngle = AngleHelper.getMouseAngle();
        mouseAngle -= ANGLE_PER_ITEM / 2;
        mouseAngle = 360 - mouseAngle;
        mouseAngle = AngleHelper.correctAngle(mouseAngle);

        for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
            double currAngle = ANGLE_PER_ITEM * i;
            double nextAngle = currAngle + ANGLE_PER_ITEM;
            currAngle = AngleHelper.correctAngle(currAngle);
            nextAngle = AngleHelper.correctAngle(nextAngle);

            boolean mouseIn = mouseAngle > currAngle && mouseAngle < nextAngle;

            if (mouseIn) {
                MenuItem item = RadialMenu.getActiveArray()[i];
                String string = item == null ? "Add Item" : item.title;
                if (KeyboardHandlerHelper.hasShiftDown() && item != null) {
                    string = ChatFormatting.RED + "EDIT: " + ChatFormatting.WHITE + string;
                }

                int drawX = this.width / 2 - this.font.width(string) / 2;
                int drawY = this.height / 2;

                int drawWidth = this.font.width(string);
                int drawHeight = this.font.lineHeight;

                float padding = 5F;

                // Background
                int r = Config.VISUAL.menuRed.get();
                int g = Config.VISUAL.menuGreen.get();
                int b = Config.VISUAL.menuBlue.get();
                int alpha = Config.VISUAL.menuAlpha.get();
                int color = ARGB.color(alpha, r, g, b);

                guiGraphics.fill(RenderPipelines.GUI, (int) (drawX - padding), (int) (drawY - drawHeight + padding), (int) (drawX + drawWidth + padding), (int) (drawY + drawHeight + padding), color);

                guiGraphics.drawString(this.font, string, drawX, drawY, 0xFFFFFFFF, false);
            }
        }
    }

    public record RadialMenuBackgroundElement(RenderPipeline pipeline,
                                        TextureSetup textureSetup,
                                        Matrix3x2f pose,
                                        int x,
                                        int y,
                                        @Nullable ScreenRectangle scissorArea,
                                        @Nullable ScreenRectangle bounds) implements GuiElementRenderState {

        public RadialMenuBackgroundElement(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x, int y, @Nullable ScreenRectangle bounds) {
            this(pipeline, textureSetup, pose, x, y, bounds, getBounds(x, y, pose, bounds));
        }

        @Override
        public void buildVertices(@Nonnull VertexConsumer vertexConsumer) {

            double mouseAngle = AngleHelper.getMouseAngle();
            mouseAngle -= (ANGLE_PER_ITEM / 2);
            mouseAngle = AngleHelper.correctAngle(mouseAngle);
            for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
                double currAngle = (ANGLE_PER_ITEM * i) + 90 + (ANGLE_PER_ITEM / 2);
                double nextAngle = (currAngle + ANGLE_PER_ITEM);
                double truecurrAngle = (ANGLE_PER_ITEM * i);
                double truenextAngle = (truecurrAngle + ANGLE_PER_ITEM);
                boolean mouseIn = (mouseAngle > truecurrAngle && mouseAngle < truenextAngle);

                currAngle = AngleHelper.correctAngle(currAngle);
                nextAngle = AngleHelper.correctAngle(nextAngle);
                currAngle = AngleHelper.correctAngle(currAngle);
                nextAngle = AngleHelper.correctAngle(nextAngle);

                currAngle = Math.toRadians(currAngle);
                nextAngle = Math.toRadians(nextAngle);

                float innerRadius = ((INNER_RADIUS - RadialMenu.animationTimer - (mouseIn ? 2 : 0)) / 100F) * (130F);
                float outerRadius = ((OUTER_RADIUS - RadialMenu.animationTimer + (mouseIn ? 2 : 0)) / 100F) * (130F);

                float pos1InX = x + innerRadius * Mth.cos((float) currAngle);
                float pos1InY = y + innerRadius * Mth.sin((float) currAngle);
                float pos1OutX = x + outerRadius * Mth.cos((float) currAngle);
                float pos1OutY = y + outerRadius * Mth.sin((float) currAngle);
                float pos2OutX = x + outerRadius * Mth.cos((float) nextAngle);
                float pos2OutY = y + outerRadius * Mth.sin((float) nextAngle);
                float pos2InX = x + innerRadius * Mth.cos((float) nextAngle);
                float pos2InY = y + innerRadius * Mth.sin((float) nextAngle);

                int r, g, b, alpha;

                if (mouseIn) {
                    r = Config.VISUAL.selectRed.get();
                    g = Config.VISUAL.selectGreen.get();
                    b = Config.VISUAL.selectBlue.get();
                    alpha = Config.VISUAL.selectAlpha.get();
                } else {
                    r = Config.VISUAL.menuRed.get();
                    g = Config.VISUAL.menuGreen.get();
                    b = Config.VISUAL.menuBlue.get();
                    alpha = Config.VISUAL.menuAlpha.get();
                }

                vertexConsumer.addVertexWith2DPose(this.pose(), pos1OutX, pos1OutY).setColor(r, g, b, alpha);
                vertexConsumer.addVertexWith2DPose(this.pose(), pos1InX, pos1InY).setColor(r, g, b, alpha);
                vertexConsumer.addVertexWith2DPose(this.pose(), pos2InX, pos2InY).setColor(r, g, b, alpha);
                vertexConsumer.addVertexWith2DPose(this.pose(), pos2OutX, pos2OutY).setColor(r, g, b, alpha);
            }
        }

        @Nullable
        private static ScreenRectangle getBounds(int x, int y, Matrix3x2f pose, @Nullable ScreenRectangle rect) {
            ScreenRectangle screenrectangle = new ScreenRectangle(x, y, 0, 0).transformMaxBounds(pose);
            return rect != null ? rect.intersection(screenrectangle) : screenrectangle;
        }
    }
}