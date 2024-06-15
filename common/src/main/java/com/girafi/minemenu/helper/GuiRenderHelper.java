package com.girafi.minemenu.helper;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;

public class GuiRenderHelper {

    public static void renderHeaderAndFooter(GuiGraphics guiGraphics, Screen base, int headerHeight, int footerHeight, int shadowDepth, String headerText) {
        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 0, 1);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.disableBlend();

        RenderSystem.setShaderTexture(0, Screen.MENU_BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();

        poseStack.pushPose();
        float f = 32F;

        // HEADER

        bufferBuilder.addVertex(0, headerHeight, 0.0F).setUv(0.0F, ((float) headerHeight / f)).setColor(64, 64, 64, 255);
        bufferBuilder.addVertex(base.width, headerHeight, 0.0F).setUv(((float) base.width / f), ((float) headerHeight / f)).setColor(64, 64, 64, 255);

        bufferBuilder.addVertex(base.width, 0, 0.0F).setUv(((float) base.width / f), ((float) 0 / f)).setColor(64, 64, 64, 255);
        bufferBuilder.addVertex(0, 0, 0.0F).setUv(0.0F, ((float) 0 / f)).setColor(64, 64, 64, 255);

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        // FOOTER
        bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        bufferBuilder.addVertex(0, base.height, 0.0F).setUv(0.0F, ((float) headerHeight / f)).setColor(64, 64, 64, 255);
        bufferBuilder.addVertex(base.width, base.height, 0.0F).setUv(((float) base.width / f), ((float) headerHeight / f)).setColor(64, 64, 64, 255);

        bufferBuilder.addVertex(base.width, base.height - footerHeight, 0.0F).setUv(((float) base.width / f), ((float) 0 / f)).setColor(64, 64, 64, 255);
        bufferBuilder.addVertex(0, base.height - footerHeight, 0.0F).setUv(0.0F, ((float) 0 / f)).setColor(64, 64, 64, 255);

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        poseStack.popPose();

        poseStack.pushPose();
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, headerText, base.width / 2, 8, 16777215);
        poseStack.popPose();
    }
}