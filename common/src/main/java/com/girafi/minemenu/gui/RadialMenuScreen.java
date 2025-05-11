package com.girafi.minemenu.gui;

import com.girafi.minemenu.data.menu.MenuItem;
import com.girafi.minemenu.data.menu.RadialMenu;
import com.girafi.minemenu.helper.AngleHelper;
import com.girafi.minemenu.helper.ItemRenderHelper;
import com.girafi.minemenu.menu.MenuItemScreen;
import com.girafi.minemenu.util.Config;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fStack;

import javax.annotation.Nonnull;

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
        active = false;
        if (Minecraft.getInstance().screen == INSTANCE) {
            Minecraft.getInstance().setScreen(null);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
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
                            if (hasShiftDown() || (Config.GENERAL.rightClickToEdit.get() && button == 1)) {
                                deactivate();
                                ScreenStack.push(new MenuItemScreen(i, menuItem));
                                return true;
                            } else {
                                if (button == 0) {
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
                            if (button == 0) {
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
    public void renderBackground(@Nonnull GuiGraphics guiGraphics, int x, int y, float partialTick) {
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int x, int y, float partialTick) {
        super.render(guiGraphics, x, y, partialTick);

        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && !mc.options.hideGui && !mc.isPaused() && RadialMenuScreen.active) {
            renderButtonBackgrounds(guiGraphics);
            renderItems(guiGraphics);
            renderText(guiGraphics);
        }
    }

    public static void renderButtonBackgrounds(GuiGraphics guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
        Matrix4fStack matrix = RenderSystem.getModelViewStack();
        Camera camera = mc.gameRenderer.getMainCamera();


        matrix.pushMatrix();
        float guiX = guiGraphics.guiWidth() * 0.5F;
        float guiY = guiGraphics.guiHeight() * 0.5F;

        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.gui());

        double mouseAngle = AngleHelper.getMouseAngle();
        mouseAngle -= (ANGLE_PER_ITEM / 2);
        mouseAngle = AngleHelper.correctAngle(mouseAngle);
        for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
            double currAngle = (ANGLE_PER_ITEM * i) + 90 + (ANGLE_PER_ITEM / 2);
            double nextAngle = (currAngle + ANGLE_PER_ITEM);
            currAngle = AngleHelper.correctAngle(currAngle);
            nextAngle = AngleHelper.correctAngle(nextAngle);
            double truecurrAngle = (ANGLE_PER_ITEM * i);
            double truenextAngle = (truecurrAngle + ANGLE_PER_ITEM);
            currAngle = AngleHelper.correctAngle(currAngle);
            nextAngle = AngleHelper.correctAngle(nextAngle);

            boolean mouseIn = (mouseAngle > truecurrAngle && mouseAngle < truenextAngle);

            currAngle = Math.toRadians(currAngle);
            nextAngle = Math.toRadians(nextAngle);

            float innerRadius = ((INNER_RADIUS - RadialMenu.animationTimer - (mouseIn ? 2 : 0)) / 100F) * (130F);
            float outerRadius = ((OUTER_RADIUS - RadialMenu.animationTimer + (mouseIn ? 2 : 0)) / 100F) * (130F);

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

            float x1 = (float) (Math.cos(currAngle) * innerRadius) + guiX;
            float x2 = (float) (Math.cos(currAngle) * outerRadius) + guiX;
            float x3 = (float) (Math.cos(nextAngle) * outerRadius) + guiX;
            float x4 = (float) (Math.cos(nextAngle) * innerRadius) + guiX;

            float y1 = (float) (Math.sin(currAngle) * innerRadius) + guiY;
            float y2 = (float) (Math.sin(currAngle) * outerRadius) + guiY;
            float y3 = (float) (Math.sin(nextAngle) * outerRadius) + guiY;
            float y4 = (float) (Math.sin(nextAngle) * innerRadius) + guiY;

            //guiGraphics.fill(RenderType.gui(), (int) x1, (int) y1, (int) x2, (int) y2, ARGB.color(alpha, r, g, b));
            //guiGraphics.fill(RenderType.gui(), (int) x3, (int) y3, (int) x4, (int) y4, ARGB.color(alpha, r, g, b));
            vertexConsumer.addVertex(x1, y1, 0).setColor(r, g, b, alpha);
            vertexConsumer.addVertex(x2, y2, 0).setColor(r, g, b, alpha);
            vertexConsumer.addVertex(x3, y3, 0).setColor(r, g, b, alpha);
            //vertexConsumer.addVertex(x4, y4, 0).setColor(r, g, b, alpha);

        }
        matrix.popMatrix();
    }

    public static void renderItems(GuiGraphics guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(mc.getWindow().getGuiScaledWidth() * 0.5D, mc.getWindow().getGuiScaledHeight() * 0.5D, 0);

        for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
            MenuItem item = RadialMenu.getActiveArray()[i];
            Item menuButton = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(Config.GENERAL.menuButtonIcon.get()));
            ItemStack stack = (item != null && !item.icon.isEmpty()) ? item.icon : (menuButton == null ? ItemStack.EMPTY : new ItemStack(menuButton));

            double angle = (ANGLE_PER_ITEM * i);
            double drawOffset = 1.5;
            double drawX = INNER_RADIUS - RadialMenu.animationTimer + drawOffset;
            double drawY = INNER_RADIUS - RadialMenu.animationTimer + drawOffset;

            double length = Math.sqrt(drawX * drawX + drawY * drawY);

            drawX = (length * Math.cos(Math.toRadians(angle)));
            drawY = (length * Math.sin(Math.toRadians(angle)));

            ItemRenderHelper.renderItem(guiGraphics, (int) drawY, (int) drawX, stack);
        }
        poseStack.popPose();
    }

    public static void renderText(GuiGraphics guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        Font fontRenderer = mc.font;
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
                if (RadialMenuScreen.hasShiftDown() && item != null) {
                    string = ChatFormatting.RED + "EDIT: " + ChatFormatting.WHITE + string;
                }

                int drawX = window.getGuiScaledWidth() / 2 - fontRenderer.width(string) / 2;
                int drawY = window.getGuiScaledHeight() / 2;

                int drawWidth = mc.font.width(string);
                int drawHeight = mc.font.lineHeight;

                float padding = 5F;

                // Background
                MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
                VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.guiOverlay());

                float r = (float) Config.VISUAL.menuRed.get() / (float) 255;
                float g = (float) Config.VISUAL.menuGreen.get() / (float) 255;
                float b = (float) Config.VISUAL.menuBlue.get() / (float) 255;
                float alpha = (float) Config.VISUAL.menuAlpha.get() / (float) 255;

                vertexConsumer.addVertex(drawX - padding, drawY + drawHeight + padding, 0).setColor(r, g, b, alpha);
                vertexConsumer.addVertex(drawX + drawWidth + padding, drawY + drawHeight + padding, 0).setColor(r, g, b, alpha);
                vertexConsumer.addVertex(drawX + drawWidth + padding, drawY - padding, 0).setColor(r, g, b, alpha);
                vertexConsumer.addVertex(drawX - padding, drawY - padding, 0).setColor(r, g, b, alpha);

                // Text
                guiGraphics.drawString(Minecraft.getInstance().font, string, drawX, drawY, 0xFFFFFF, false);
            }
        }
    }
}