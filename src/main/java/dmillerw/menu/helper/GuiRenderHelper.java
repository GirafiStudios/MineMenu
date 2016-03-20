package dmillerw.menu.helper;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

/**
 * @author dmillerw
 */
public class GuiRenderHelper {

    public static void renderHeaderAndFooter(GuiScreen base, int headerHeight, int footerHeight, int shadowDepth, String headerText) {
        GlStateManager.pushMatrix();

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.disableTexture2D();

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();

        // HEADER - SHADOW
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

        vertexbuffer.pos(0, headerHeight + shadowDepth, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 0).endVertex();
        vertexbuffer.pos(base.width, headerHeight + shadowDepth, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 0).endVertex();

        vertexbuffer.pos(base.width, headerHeight, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
        vertexbuffer.pos(0, headerHeight, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();

        tessellator.draw();

        // FOOTER - SHADOW
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

        vertexbuffer.pos(0, base.height - footerHeight, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
        vertexbuffer.pos(base.width, base.height - footerHeight, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();

        vertexbuffer.pos(base.width, base.height - footerHeight - shadowDepth, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 0).endVertex();
        vertexbuffer.pos(0, base.height - footerHeight - shadowDepth, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 0).endVertex();

        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();

        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();

        base.mc.getTextureManager().bindTexture(Gui.optionsBackground);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32F;

        // HEADER
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

        vertexbuffer.pos((double) 0, (double) headerHeight, 0.0D).tex(0.0D, (double) ((float) headerHeight / f)).color(64, 64, 64, 255).endVertex();
        vertexbuffer.pos((double) 0 + base.width, (double) headerHeight, 0.0D).tex((double) ((float) base.width / f), (double) ((float) headerHeight / f)).color(64, 64, 64, 255).endVertex();

        vertexbuffer.pos((double) 0 + base.width, (double) 0, 0.0D).tex((double) ((float) base.width / f), (double) ((float) 0 / f)).color(64, 64, 64, 255).endVertex();
        vertexbuffer.pos((double) 0, (double) 0, 0.0D).tex(0.0D, (double) ((float) 0 / f)).color(64, 64, 64, 255).endVertex();

        tessellator.draw();

        // FOOTER
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

        vertexbuffer.pos((double) 0, (double) base.height, 0.0D).tex(0.0D, (double) ((float) headerHeight / f)).color(64, 64, 64, 255).endVertex();
        vertexbuffer.pos((double) 0 + base.width, (double) base.height, 0.0D).tex((double) ((float) base.width / f), (double) ((float) headerHeight / f)).color(64, 64, 64, 255).endVertex();

        vertexbuffer.pos((double) 0 + base.width, (double) base.height - footerHeight, 0.0D).tex((double) ((float) base.width / f), (double) ((float) 0 / f)).color(64, 64, 64, 255).endVertex();
        vertexbuffer.pos((double) 0, (double) base.height - footerHeight, 0.0D).tex(0.0D, (double) ((float) 0 / f)).color(64, 64, 64, 255).endVertex();

        tessellator.draw();

        GlStateManager.popMatrix();

        base.drawCenteredString(base.mc.fontRendererObj, headerText, base.width / 2, 8, 16777215);
    }
}