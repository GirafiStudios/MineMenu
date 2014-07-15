package dmillerw.menu.helper;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

/**
 * @author dmillerw
 */
public class GuiRenderHelper {

	public static void renderHeaderAndFooter(GuiScreen base, int headerHeight, int footerHeight, int shadowDepth) {
		GL11.glPushMatrix();

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_FOG);

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(770, 771, 0, 1);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		Tessellator tessellator = Tessellator.instance;

		// HEADER - SHADOW
		tessellator.startDrawingQuads();

		tessellator.setColorRGBA_I(0, 0);

		tessellator.addVertexWithUV(0,          headerHeight + shadowDepth, 0.0D, 0.0D, 1.0D);
		tessellator.addVertexWithUV(base.width, headerHeight + shadowDepth, 0.0D, 1.0D, 1.0D);

		tessellator.setColorRGBA_I(0, 255);

		tessellator.addVertexWithUV(base.width, headerHeight, 0.0D, 1.0D, 0.0D);
		tessellator.addVertexWithUV(0,          headerHeight, 0.0D, 0.0D, 0.0D);

		tessellator.draw();

		// FOOTER - SHADOW
		tessellator.startDrawingQuads();

		tessellator.setColorRGBA_I(0, 255);

		tessellator.addVertexWithUV(0,          base.height - footerHeight, 0.0D, 0.0D, 1.0D);
		tessellator.addVertexWithUV(base.width, base.height - footerHeight, 0.0D, 1.0D, 1.0D);

		tessellator.setColorRGBA_I(0, 0);

		tessellator.addVertexWithUV(base.width, base.height - footerHeight - shadowDepth, 0.0D, 1.0D, 0.0D);
		tessellator.addVertexWithUV(0,          base.height - footerHeight - shadowDepth, 0.0D, 0.0D, 0.0D);

		tessellator.draw();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		GL11.glPopMatrix();

		GL11.glPushMatrix();

		base.mc.getTextureManager().bindTexture(Gui.optionsBackground);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32F;

		// HEADER
		tessellator.startDrawingQuads();

		tessellator.setColorRGBA_I(4210752, 255);

		tessellator.addVertexWithUV((double) 0,              (double) headerHeight, 0.0D, 0.0D,                            (double)((float)headerHeight / f));
		tessellator.addVertexWithUV((double) 0 + base.width, (double) headerHeight, 0.0D, (double)((float)base.width / f), (double)((float)headerHeight / f));

		tessellator.setColorRGBA_I(4210752, 255);

		tessellator.addVertexWithUV((double) 0 + base.width, (double) 0, 0.0D, (double)((float)base.width / f), (double)((float)0 / f));
		tessellator.addVertexWithUV((double) 0,              (double) 0, 0.0D, 0.0D,                            (double)((float)0 / f));

		tessellator.draw();

		// FOOTER
		tessellator.startDrawingQuads();

		tessellator.setColorRGBA_I(4210752, 255);

		tessellator.addVertexWithUV((double) 0,              (double) base.height, 0.0D, 0.0D,                            (double)((float)headerHeight / f));
		tessellator.addVertexWithUV((double) 0 + base.width, (double) base.height, 0.0D, (double)((float)base.width / f), (double)((float)headerHeight / f));

		tessellator.setColorRGBA_I(4210752, 255);

		tessellator.addVertexWithUV((double) 0 + base.width, (double) base.height - footerHeight, 0.0D, (double)((float)base.width / f), (double)((float)0 / f));
		tessellator.addVertexWithUV((double) 0,              (double) base.height - footerHeight, 0.0D, 0.0D,                            (double)((float)0 / f));

		tessellator.draw();

		GL11.glPopMatrix();
	}
}
