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
        BufferBuilder bufferBuilder = tessellator.getBuilder();

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.disableBlend();

        RenderSystem.setShaderTexture(0, Screen.BACKGROUND_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();

        poseStack.pushPose();
        float f = 32F;

        // HEADER
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        bufferBuilder.vertex(0, headerHeight, 0.0D).uv(0.0F, ((float) headerHeight / f)).color(64, 64, 64, 255).endVertex();
        bufferBuilder.vertex((double) 0 + base.width, headerHeight, 0.0D).uv(((float) base.width / f), ((float) headerHeight / f)).color(64, 64, 64, 255).endVertex();

        bufferBuilder.vertex((double) 0 + base.width, 0, 0.0D).uv(((float) base.width / f), ((float) 0 / f)).color(64, 64, 64, 255).endVertex();
        bufferBuilder.vertex(0, 0, 0.0D).uv(0.0F, ((float) 0 / f)).color(64, 64, 64, 255).endVertex();

        tessellator.end();

        // FOOTER
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        bufferBuilder.vertex(0, base.height, 0.0D).uv(0.0F, ((float) headerHeight / f)).color(64, 64, 64, 255).endVertex();
        bufferBuilder.vertex((double) 0 + base.width, base.height, 0.0D).uv(((float) base.width / f), ((float) headerHeight / f)).color(64, 64, 64, 255).endVertex();

        bufferBuilder.vertex((double) 0 + base.width, (double) base.height - footerHeight, 0.0D).uv(((float) base.width / f), ((float) 0 / f)).color(64, 64, 64, 255).endVertex();
        bufferBuilder.vertex(0, (double) base.height - footerHeight, 0.0D).uv(0.0F, ((float) 0 / f)).color(64, 64, 64, 255).endVertex();

        tessellator.end();

        poseStack.popPose();

        poseStack.pushPose();
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, headerText, base.width / 2, 8, 16777215);
        poseStack.popPose();
    }
}