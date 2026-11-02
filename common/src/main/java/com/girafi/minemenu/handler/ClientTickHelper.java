package com.girafi.minemenu.handler;

import com.girafi.minemenu.data.menu.Category;
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
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fStack;
import org.lwjgl.opengl.GL11;

public class ClientTickHelper {
    public static final double ANGLE_PER_ITEM = 360F / RadialMenu.MAX_ITEMS;
    private static final float OUTER_RADIUS = 80;
    private static final float INNER_RADIUS = 60;
    private static final double CENTER_EDIT_FRACTION = 0.5D;
    public static boolean isMouseInCenter() {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        double dx = mc.mouseHandler.xpos() - window.getScreenWidth() * 0.5D;
        double dy = mc.mouseHandler.ypos() - window.getScreenHeight() * 0.5D;
        double distance = Math.sqrt(dx * dx + dy * dy) * window.getGuiScaledWidth() / window.getScreenWidth();
        float innerRadius = ((INNER_RADIUS - RadialMenu.animationTimer - 2) / 100F) * (130F);
        double innerEdgeDistance = innerRadius * Math.cos(Math.toRadians(ANGLE_PER_ITEM / 2));
        return distance < innerEdgeDistance * CENTER_EDIT_FRACTION;
    }

    public static void renderButtonBackgrounds() {
        Minecraft mc = Minecraft.getInstance();
        Category category = RadialMenu.getActiveCategory();
        Matrix4fStack matrix = RenderSystem.getModelViewStack();
        matrix.pushMatrix();
        matrix.translate((float) (mc.getWindow().getGuiScaledWidth() * 0.5D), (float) (mc.getWindow().getGuiScaledHeight() * 0.5D), 0);
        RenderSystem.applyModelViewMatrix();

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        int hovered = RadialMenuInput.getHoveredIndex();
        for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
            double currAngle = (ANGLE_PER_ITEM * i) + 90 + (ANGLE_PER_ITEM / 2);
            double nextAngle = (currAngle + ANGLE_PER_ITEM);
            currAngle = AngleHelper.correctAngle(currAngle);
            nextAngle = AngleHelper.correctAngle(nextAngle);

            boolean mouseIn = hovered >= 0 && i == RadialMenu.MAX_ITEMS - 1 - hovered;

            currAngle = Math.toRadians(currAngle);
            nextAngle = Math.toRadians(nextAngle);

            float innerRadius = ((INNER_RADIUS - RadialMenu.animationTimer - (mouseIn ? 2 : 0)) / 100F) * (130F);
            float outerRadius = ((OUTER_RADIUS - RadialMenu.animationTimer + (mouseIn ? 2 : 0)) / 100F) * (130F);

            int color = mouseIn ? category.getSelectColor() : category.getMenuColor();
            float r = FastColor.ARGB32.red(color) / 255F;
            float g = FastColor.ARGB32.green(color) / 255F;
            float b = FastColor.ARGB32.blue(color) / 255F;
            float alpha = FastColor.ARGB32.alpha(color) / 255F;

            float x1 = (float) (Math.cos(currAngle) * innerRadius);
            float x2 = (float) (Math.cos(currAngle) * outerRadius);
            float x3 = (float) Math.cos(nextAngle) * outerRadius;
            float x4 = (float) Math.cos(nextAngle) * innerRadius;

            float y1 = (float) Math.sin(currAngle) * innerRadius;
            float y2 = (float) Math.sin(currAngle) * outerRadius;
            float y3 = (float) Math.sin(nextAngle) * outerRadius;
            float y4 = (float) Math.sin(nextAngle) * innerRadius;

            bufferBuilder.addVertex(x1, y1, 0).setColor(r, g, b, alpha);
            bufferBuilder.addVertex(x2, y2, 0).setColor(r, g, b, alpha);
            bufferBuilder.addVertex(x3, y3, 0).setColor(r, g, b, alpha);
            bufferBuilder.addVertex(x4, y4, 0).setColor(r, g, b, alpha);
        }

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        matrix.popMatrix();
        RenderSystem.applyModelViewMatrix();
    }

    public static void renderItems(GuiGraphics guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(mc.getWindow().getGuiScaledWidth() * 0.5D, mc.getWindow().getGuiScaledHeight() * 0.5D, 0);

        for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
            MenuItem item = RadialMenu.getActiveArray()[i];
            Item menuButton = BuiltInRegistries.ITEM.get(ResourceLocation.parse(Config.GENERAL.menuButtonIcon.get()));
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
        int hovered = RadialMenuInput.getHoveredIndex();
        if (hovered < 0) {
            return;
        }

        MenuItem item = RadialMenu.getActiveArray()[hovered];
        String string = item == null ? "Add Item" : item.title;
        if (RadialMenuScreen.isShiftEditing() && RadialMenuInput.isCenterHovered()) {
            string = ChatFormatting.RED + "EDIT: " + ChatFormatting.WHITE + I18n.get("mine_menu.categoryScreen.edit");
        } else if (RadialMenuScreen.isShiftEditing() && item != null) {
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
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        int color = RadialMenu.getActiveCategory().getMenuColor();
        float r = FastColor.ARGB32.red(color) / 255F;
        float g = FastColor.ARGB32.green(color) / 255F;
        float b = FastColor.ARGB32.blue(color) / 255F;
        float alpha = FastColor.ARGB32.alpha(color) / 255F;

        bufferBuilder.addVertex(drawX - padding, drawY + drawHeight + padding, 0).setColor(r, g, b, alpha);
        bufferBuilder.addVertex(drawX + drawWidth + padding, drawY + drawHeight + padding, 0).setColor(r, g, b, alpha);
        bufferBuilder.addVertex(drawX + drawWidth + padding, drawY - padding, 0).setColor(r, g, b, alpha);
        bufferBuilder.addVertex(drawX - padding, drawY - padding, 0).setColor(r, g, b, alpha);

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        RenderSystem.disableBlend();

        // Text
        guiGraphics.drawString(Minecraft.getInstance().font, string, drawX, drawY, 0xFFFFFF, false);
    }
}
