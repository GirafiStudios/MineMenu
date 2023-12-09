package com.girafi.minemenu.handler;

import com.girafi.minemenu.data.menu.MenuItem;
import com.girafi.minemenu.data.menu.RadialMenu;
import com.girafi.minemenu.gui.RadialMenuScreen;
import com.girafi.minemenu.helper.AngleHelper;
import com.girafi.minemenu.helper.ItemRenderHelper;
import com.girafi.minemenu.util.Config;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class ClientTickHelper {
    public static final double ANGLE_PER_ITEM = 360F / RadialMenu.MAX_ITEMS;
    private static final double OUTER_RADIUS = 80;
    private static final double INNER_RADIUS = 60;

    public static void renderButtonBackgrounds() {
        Minecraft mc = Minecraft.getInstance();
        PoseStack poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        poseStack.translate(mc.getWindow().getGuiScaledWidth() * 0.5D, mc.getWindow().getGuiScaledHeight() * 0.5D, 0);
        RenderSystem.applyModelViewMatrix();

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

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

            double innerRadius = ((INNER_RADIUS - RadialMenu.animationTimer - (mouseIn ? 2 : 0)) / 100F) * (130F);
            double outerRadius = ((OUTER_RADIUS - RadialMenu.animationTimer + (mouseIn ? 2 : 0)) / 100F) * (130F);

            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

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

            double x1 = Math.cos(currAngle) * innerRadius;
            double x2 = Math.cos(currAngle) * outerRadius;
            double x3 = Math.cos(nextAngle) * outerRadius;
            double x4 = Math.cos(nextAngle) * innerRadius;

            double y1 = Math.sin(currAngle) * innerRadius;
            double y2 = Math.sin(currAngle) * outerRadius;
            double y3 = Math.sin(nextAngle) * outerRadius;
            double y4 = Math.sin(nextAngle) * innerRadius;

            bufferBuilder.vertex(x1, y1, 0).color(r, g, b, alpha).endVertex();
            bufferBuilder.vertex(x2, y2, 0).color(r, g, b, alpha).endVertex();
            bufferBuilder.vertex(x3, y3, 0).color(r, g, b, alpha).endVertex();
            bufferBuilder.vertex(x4, y4, 0).color(r, g, b, alpha).endVertex();

            tessellator.end();
        }
        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    public static void renderItems(GuiGraphics guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(mc.getWindow().getGuiScaledWidth() * 0.5D, mc.getWindow().getGuiScaledHeight() * 0.5D, 0);

        for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
            MenuItem item = RadialMenu.getActiveArray()[i];
            Item menuButton = BuiltInRegistries.ITEM.get(new ResourceLocation(Config.GENERAL.menuButtonIcon.get().toString()));
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
        mouseAngle -= ClientTickHelper.ANGLE_PER_ITEM / 2;
        mouseAngle = 360 - mouseAngle;
        mouseAngle = AngleHelper.correctAngle(mouseAngle);

        for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
            double currAngle = ClientTickHelper.ANGLE_PER_ITEM * i;
            double nextAngle = currAngle + ClientTickHelper.ANGLE_PER_ITEM;
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
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                RenderSystem.setShader(GameRenderer::getPositionColorShader);

                Tesselator tessellator = Tesselator.getInstance();
                BufferBuilder bufferBuilder = tessellator.getBuilder();
                bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

                float r = (float) Config.VISUAL.menuRed.get() / (float) 255;
                float g = (float) Config.VISUAL.menuGreen.get() / (float) 255;
                float b = (float) Config.VISUAL.menuBlue.get() / (float) 255;
                float alpha = (float) Config.VISUAL.menuAlpha.get() / (float) 255;

                bufferBuilder.vertex(drawX - padding, drawY + drawHeight + padding, 0).color(r, g, b, alpha).endVertex();
                bufferBuilder.vertex(drawX + drawWidth + padding, drawY + drawHeight + padding, 0).color(r, g, b, alpha).endVertex();
                bufferBuilder.vertex(drawX + drawWidth + padding, drawY - padding, 0).color(r, g, b, alpha).endVertex();
                bufferBuilder.vertex(drawX - padding, drawY - padding, 0).color(r, g, b, alpha).endVertex();

                tessellator.end();
                RenderSystem.disableBlend();

                // Text
                guiGraphics.drawString(Minecraft.getInstance().font, string, drawX, drawY, 0xFFFFFF, false);
            }
        }
    }
}