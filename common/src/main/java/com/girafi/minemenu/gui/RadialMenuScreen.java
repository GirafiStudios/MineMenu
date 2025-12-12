package com.girafi.minemenu.gui;

import com.girafi.minemenu.data.menu.MenuItem;
import com.girafi.minemenu.data.menu.RadialMenu;
import com.girafi.minemenu.helper.AngleHelper;
import com.girafi.minemenu.helper.ItemRenderHelper;
import com.girafi.minemenu.menu.MenuItemScreen;
import com.girafi.minemenu.util.Config;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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
        if (Minecraft.getInstance().screen == INSTANCE) {
            active = false;
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
    public void renderBackground(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && !mc.options.hideGui && !mc.isPaused() && RadialMenuScreen.active) {
            renderItems(guiGraphics);
            renderText(guiGraphics);
            renderButtonBackgrounds(guiGraphics, mouseX, mouseY);
        }
    }

    public void renderButtonBackgrounds(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getInstance();
        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();
        float x = this.width / 2.0F;
        float y = this.height / 2.0F;

        MultiBufferSource.BufferSource buffer = guiGraphics.bufferSource;
        RenderType renderType = RenderType.debugQuads();
        VertexConsumer vertexConsumer = buffer.getBuffer(renderType);

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

            float r, g, b, alpha;

            if (mouseIn) {
                r = (float) Config.VISUAL.selectRed.get() / (float) 255;
                g = (float) Config.VISUAL.selectGreen.get() / (float) 255;
                b = (float) Config.VISUAL.selectBlue.get() / (float) 255;
                alpha = (float) Config.VISUAL.selectAlpha.get() / (float) 255;
            } else {
                r = (float) Config.VISUAL.menuRed.get() / (float) 255;
                g = (float) Config.VISUAL.menuGreen.get() / (float) 255;
                b = (float) Config.VISUAL.menuBlue.get() / (float) 255;
                alpha = (float) Config.VISUAL.menuAlpha.get() / (float) 255;
            }

            float x1 = (float) (Math.cos(currAngle) * innerRadius) + x;
            float x2 = (float) (Math.cos(currAngle) * outerRadius) + x;
            float x3 = (float) (Math.cos(nextAngle) * outerRadius) + x;
            float x4 = (float) (Math.cos(nextAngle) * innerRadius) + x;

            float y1 = (float) (Math.sin(currAngle) * innerRadius) + y;
            float y2 = (float) (Math.sin(currAngle) * outerRadius) + y;
            float y3 = (float) (Math.sin(nextAngle) * outerRadius) + y;
            float y4 = (float) (Math.sin(nextAngle) * innerRadius) + y;

            vertexConsumer.addVertex(x1, y1, 0).setColor(r, g, b, alpha);
            vertexConsumer.addVertex(x2, y2, 0).setColor(r, g, b, alpha);
            vertexConsumer.addVertex(x3, y3, 0).setColor(r, g, b, alpha);
            vertexConsumer.addVertex(x4, y4, 0).setColor(r, g, b, alpha);
        }
        buffer.endBatch(renderType);

        poseStack.popPose();
    }

    public void renderItems(GuiGraphics guiGraphics) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(this.width * 0.5D, this.height * 0.5D, 0);

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

    public void renderText(GuiGraphics guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
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

                int drawX = this.width / 2 - this.font.width(string) / 2;
                int drawY = this.height / 2;

                int drawWidth = this.font.width(string);
                int drawHeight = this.font.lineHeight;

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
                guiGraphics.drawString(this.font, string, drawX, drawY, 0xFFFFFF, false);
            }
        }
    }
}