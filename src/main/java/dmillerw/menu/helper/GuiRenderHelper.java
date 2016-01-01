package dmillerw.menu.helper;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

/**
 * @author dmillerw
 */
public class GuiRenderHelper {

    public static void renderHeaderAndFooter(GuiScreen base, int headerHeight, int footerHeight, int shadowDepth, String headerText) {
        GL11.glPushMatrix();

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 0, 1);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        // HEADER - SHADOW
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

        GlStateManager.color(0 >> 16 & 255, 0 >> 8 & 255, 0 & 255, 0);

        worldrenderer.pos(0, headerHeight + shadowDepth, 0.0D).tex(0.0D, 1.0D).endVertex();
        worldrenderer.pos(base.width, headerHeight + shadowDepth, 0.0D).tex(1.0D, 1.0D).endVertex();

        GlStateManager.color(0 >> 16 & 255, 0 >> 8 & 255, 0 & 255, 255);

        worldrenderer.pos(base.width, headerHeight, 0.0D).tex(1.0D, 0.0D).endVertex();
        worldrenderer.pos(0, headerHeight, 0.0D).tex(0.0D, 0.0D).endVertex();

        tessellator.draw();

        // FOOTER - SHADOW
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

        GlStateManager.color(0 >> 16 & 255, 0 >> 8 & 255, 0 & 255, 255);

        worldrenderer.pos(0, base.height - footerHeight, 0.0D).tex(0.0D, 1.0D).endVertex();
        worldrenderer.pos(base.width, base.height - footerHeight, 0.0D).tex(1.0D, 1.0D).endVertex();

        GlStateManager.color(0 >> 16 & 255, 0 >> 8 & 255, 0 & 255, 0);

        worldrenderer.pos(base.width, base.height - footerHeight - shadowDepth, 0.0D).tex(1.0D, 0.0D).endVertex();
        worldrenderer.pos(0, base.height - footerHeight - shadowDepth, 0.0D).tex(0.0D, 0.0D).endVertex();

        tessellator.draw();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL11.glPopMatrix();

        GL11.glPushMatrix();

        base.mc.getTextureManager().bindTexture(Gui.optionsBackground);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32F;

        // HEADER
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

        GlStateManager.color(4210752 >> 16 & 255, 4210752 >> 8 & 255, 4210752 & 255, 255);

        worldrenderer.pos((double) 0, (double) headerHeight, 0.0D).tex(0.0D, (double) ((float) headerHeight / f)).endVertex();
        worldrenderer.pos((double) 0 + base.width, (double) headerHeight, 0.0D).tex((double) ((float) base.width / f), (double) ((float) headerHeight / f)).endVertex();

        GlStateManager.color(4210752 >> 16 & 255, 4210752 >> 8 & 255, 4210752 & 255, 255);

        worldrenderer.pos((double) 0 + base.width, (double) 0, 0.0D).tex((double) ((float) base.width / f), (double) ((float) 0 / f)).endVertex();
        worldrenderer.pos((double) 0, (double) 0, 0.0D).tex(0.0D, (double) ((float) 0 / f)).endVertex();

        tessellator.draw();

        // FOOTER
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

        GlStateManager.color(4210752 >> 16 & 255, 4210752 >> 8 & 255, 4210752 & 255, 255);

        worldrenderer.pos((double) 0, (double) base.height, 0.0D).tex(0.0D, (double) ((float) headerHeight / f)).endVertex();
        worldrenderer.pos((double) 0 + base.width, (double) base.height, 0.0D).tex((double) ((float) base.width / f), (double) ((float) headerHeight / f)).endVertex();

        GlStateManager.color(4210752 >> 16 & 255, 4210752 >> 8 & 255, 4210752 & 255, 255);

        worldrenderer.pos((double) 0 + base.width, (double) base.height - footerHeight, 0.0D).tex((double) ((float) base.width / f), (double) ((float) 0 / f)).endVertex();
        worldrenderer.pos((double) 0, (double) base.height - footerHeight, 0.0D).tex(0.0D, (double) ((float) 0 / f)).endVertex();

        tessellator.draw();

        GL11.glPopMatrix();

        base.drawCenteredString(base.mc.fontRendererObj, headerText, base.width / 2, 8, 16777215);
    }
}
