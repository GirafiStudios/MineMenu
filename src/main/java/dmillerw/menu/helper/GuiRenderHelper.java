package dmillerw.menu.helper;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class GuiRenderHelper {

    public static void renderHeaderAndFooter(MatrixStack matrixStack, Screen base, int headerHeight, int footerHeight, int shadowDepth, String headerText) {
        RenderSystem.pushMatrix();

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 0, 1);
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(7425);
        RenderSystem.disableTexture();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        // HEADER - SHADOW
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

        bufferBuilder.pos(0, headerHeight + shadowDepth, 0.0D).tex(0.0F, 1.0F).color(0, 0, 0, 0).endVertex();
        bufferBuilder.pos(base.field_230708_k_, headerHeight + shadowDepth, 0.0D).tex(1.0F, 1.0F).color(0, 0, 0, 0).endVertex();

        bufferBuilder.pos(base.field_230708_k_, headerHeight, 0.0D).tex(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
        bufferBuilder.pos(0, headerHeight, 0.0D).tex(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();

        tessellator.draw();

        // FOOTER - SHADOW
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

        bufferBuilder.pos(0, base.field_230709_l_ - footerHeight, 0.0D).tex(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
        bufferBuilder.pos(base.field_230708_k_, base.field_230709_l_ - footerHeight, 0.0D).tex(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();

        bufferBuilder.pos(base.field_230708_k_, base.field_230709_l_ - footerHeight - shadowDepth, 0.0D).tex(1.0F, 0.0F).color(0, 0, 0, 0).endVertex();
        bufferBuilder.pos(0, base.field_230709_l_ - footerHeight - shadowDepth, 0.0D).tex(0.0F, 0.0F).color(0, 0, 0, 0).endVertex();

        tessellator.draw();

        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();

        RenderSystem.popMatrix();

        RenderSystem.pushMatrix();

        base.getMinecraft().getTextureManager().bindTexture(AbstractGui.field_230663_f_);

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32F;

        // HEADER
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

        bufferBuilder.pos(0, headerHeight, 0.0D).tex(0.0F, ((float) headerHeight / f)).color(64, 64, 64, 255).endVertex();
        bufferBuilder.pos((double) 0 + base.field_230708_k_, headerHeight, 0.0D).tex(((float) base.field_230708_k_ / f), ((float) headerHeight / f)).color(64, 64, 64, 255).endVertex();

        bufferBuilder.pos((double) 0 + base.field_230708_k_, 0, 0.0D).tex(((float) base.field_230708_k_ / f), ((float) 0 / f)).color(64, 64, 64, 255).endVertex();
        bufferBuilder.pos(0, 0, 0.0D).tex(0.0F, ((float) 0 / f)).color(64, 64, 64, 255).endVertex();

        tessellator.draw();

        // FOOTER
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

        bufferBuilder.pos(0, base.field_230709_l_, 0.0D).tex(0.0F, ((float) headerHeight / f)).color(64, 64, 64, 255).endVertex();
        bufferBuilder.pos((double) 0 + base.field_230708_k_, base.field_230709_l_, 0.0D).tex(((float) base.field_230708_k_ / f), ((float) headerHeight / f)).color(64, 64, 64, 255).endVertex();

        bufferBuilder.pos((double) 0 + base.field_230708_k_, (double) base.field_230709_l_ - footerHeight, 0.0D).tex(((float) base.field_230708_k_ / f), ((float) 0 / f)).color(64, 64, 64, 255).endVertex();
        bufferBuilder.pos(0, (double) base.field_230709_l_ - footerHeight, 0.0D).tex(0.0F, ((float) 0 / f)).color(64, 64, 64, 255).endVertex();

        tessellator.draw();

        RenderSystem.popMatrix();

        base.func_238471_a_(matrixStack, base.getMinecraft().fontRenderer, headerText, base.field_230708_k_ / 2, 8, 16777215);
    }
}