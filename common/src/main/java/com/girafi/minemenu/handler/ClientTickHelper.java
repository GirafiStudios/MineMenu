package com.girafi.minemenu.handler;

import com.girafi.minemenu.data.menu.MenuItem;
import com.girafi.minemenu.data.menu.RadialMenu;
import com.girafi.minemenu.gui.RadialMenuScreen;
import com.girafi.minemenu.helper.AngleHelper;
import com.girafi.minemenu.helper.ItemRenderHelper;
import com.girafi.minemenu.util.Config;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ClientTickHelper {
    public static final double ANGLE_PER_ITEM = 360F / RadialMenu.MAX_ITEMS;
    private static final float OUTER_RADIUS = 80;
    private static final float INNER_RADIUS = 60;

    public static void renderButtonBackgrounds(GuiGraphics guiGraphics) { //TODO Doesn't render???
        Minecraft mc = Minecraft.getInstance();
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate((float) (mc.getWindow().getGuiScaledWidth() * 0.5D), (float) (mc.getWindow().getGuiScaledHeight() * 0.5D), 0);

        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        RenderType guiOverlay = RenderType.guiOverlay();
        VertexConsumer vertexConsumer = buffer.getBuffer(guiOverlay);
        //RenderSystem.enableBlend();
        //RenderSystem.defaultBlendFunc();
        //RenderSystem.disableCull();
        //RenderSystem.setShader(CoreShaders.RENDERTYPE_GUI_OVERLAY);

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

            float x1 = (float) (Math.cos(currAngle) * innerRadius);
            float x2 = (float) (Math.cos(currAngle) * outerRadius);
            float x3 = (float) Math.cos(nextAngle) * outerRadius;
            float x4 = (float) Math.cos(nextAngle) * innerRadius;

            float y1 = (float) Math.sin(currAngle) * innerRadius;
            float y2 = (float) Math.sin(currAngle) * outerRadius;
            float y3 = (float) Math.sin(nextAngle) * outerRadius;
            float y4 = (float) Math.sin(nextAngle) * innerRadius;

            vertexConsumer.addVertex(x1, y1, 0).setColor(r, g, b, alpha);
            vertexConsumer.addVertex(x2, y2, 0).setColor(r, g, b, alpha);
            vertexConsumer.addVertex(x3, y3, 0).setColor(r, g, b, alpha);
            vertexConsumer.addVertex(x4, y4, 0).setColor(r, g, b, alpha);
        }

        buffer.endBatch(guiOverlay);

        //BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        //RenderSystem.enableCull();
        //RenderSystem.disableBlend();

        poseStack.popPose();
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