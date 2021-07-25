package dmillerw.menu.helper;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;

public class GuiRenderHelper {

    public static void renderHeaderAndFooter(PoseStack matrixStack, Screen base, int headerHeight, int footerHeight, int shadowDepth, String headerText) {
        matrixStack.pushPose();

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 0, 1);
        //RenderSystem.disableAlphaTest(); //TODO
        //RenderSystem.shadeModel(7425); //TODO
        RenderSystem.disableTexture();

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();

        // HEADER - SHADOW
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        bufferBuilder.vertex(0, headerHeight + shadowDepth, 0.0D).uv(0.0F, 1.0F).color(0, 0, 0, 0).endVertex();
        bufferBuilder.vertex(base.width, headerHeight + shadowDepth, 0.0D).uv(1.0F, 1.0F).color(0, 0, 0, 0).endVertex();

        bufferBuilder.vertex(base.width, headerHeight, 0.0D).uv(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
        bufferBuilder.vertex(0, headerHeight, 0.0D).uv(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();

        tessellator.end();

        // FOOTER - SHADOW
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        bufferBuilder.vertex(0, base.height - footerHeight, 0.0D).uv(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
        bufferBuilder.vertex(base.width, base.height - footerHeight, 0.0D).uv(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();

        bufferBuilder.vertex(base.width, base.height - footerHeight - shadowDepth, 0.0D).uv(1.0F, 0.0F).color(0, 0, 0, 0).endVertex();
        bufferBuilder.vertex(0, base.height - footerHeight - shadowDepth, 0.0D).uv(0.0F, 0.0F).color(0, 0, 0, 0).endVertex();

        tessellator.end();

        RenderSystem.enableTexture();
        //RenderSystem.shadeModel(7424); //TODO
        //RenderSystem.enableAlphaTest(); //TODO
        RenderSystem.disableBlend();

        matrixStack.popPose();

        matrixStack.pushPose();

        base.getMinecraft().getTextureManager().bindForSetup(GuiComponent.BACKGROUND_LOCATION);

        //RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F); //TODO
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

        matrixStack.popPose();

        GuiComponent.drawCenteredString(matrixStack, base.getMinecraft().font, headerText, base.width / 2, 8, 16777215);
    }
}