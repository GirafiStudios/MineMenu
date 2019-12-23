package dmillerw.menu.helper;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class GuiRenderHelper {

    public static void renderHeaderAndFooter(Screen base, int headerHeight, int footerHeight, int shadowDepth, String headerText) {
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

        bufferBuilder.func_225582_a_(0, headerHeight + shadowDepth, 0.0D).func_225583_a_(0.0F, 1.0F).func_225586_a_(0, 0, 0, 0).endVertex();
        bufferBuilder.func_225582_a_(base.width, headerHeight + shadowDepth, 0.0D).func_225583_a_(1.0F, 1.0F).func_225586_a_(0, 0, 0, 0).endVertex();

        bufferBuilder.func_225582_a_(base.width, headerHeight, 0.0D).func_225583_a_(1.0F, 0.0F).func_225586_a_(0, 0, 0, 255).endVertex();
        bufferBuilder.func_225582_a_(0, headerHeight, 0.0D).func_225583_a_(0.0F, 0.0F).func_225586_a_(0, 0, 0, 255).endVertex();

        tessellator.draw();

        // FOOTER - SHADOW
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

        bufferBuilder.func_225582_a_(0, base.height - footerHeight, 0.0D).func_225583_a_(0.0F, 1.0F).func_225586_a_(0, 0, 0, 255).endVertex();
        bufferBuilder.func_225582_a_(base.width, base.height - footerHeight, 0.0D).func_225583_a_(1.0F, 1.0F).func_225586_a_(0, 0, 0, 255).endVertex();

        bufferBuilder.func_225582_a_(base.width, base.height - footerHeight - shadowDepth, 0.0D).func_225583_a_(1.0F, 0.0F).func_225586_a_(0, 0, 0, 0).endVertex();
        bufferBuilder.func_225582_a_(0, base.height - footerHeight - shadowDepth, 0.0D).func_225583_a_(0.0F, 0.0F).func_225586_a_(0, 0, 0, 0).endVertex();

        tessellator.draw();

        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();

        RenderSystem.popMatrix();

        RenderSystem.pushMatrix();

        base.getMinecraft().getTextureManager().bindTexture(AbstractGui.BACKGROUND_LOCATION);

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32F;

        // HEADER
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

        bufferBuilder.func_225582_a_(0, headerHeight, 0.0D).func_225583_a_(0.0F, ((float) headerHeight / f)).func_225586_a_(64, 64, 64, 255).endVertex();
        bufferBuilder.func_225582_a_((double) 0 + base.width, headerHeight, 0.0D).func_225583_a_(((float) base.width / f), ((float) headerHeight / f)).func_225586_a_(64, 64, 64, 255).endVertex();

        bufferBuilder.func_225582_a_((double) 0 + base.width, 0, 0.0D).func_225583_a_(((float) base.width / f), ((float) 0 / f)).func_225586_a_(64, 64, 64, 255).endVertex();
        bufferBuilder.func_225582_a_(0, 0, 0.0D).func_225583_a_(0.0F, ((float) 0 / f)).func_225586_a_(64, 64, 64, 255).endVertex();

        tessellator.draw();

        // FOOTER
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

        bufferBuilder.func_225582_a_(0, base.height, 0.0D).func_225583_a_(0.0F, ((float) headerHeight / f)).func_225586_a_(64, 64, 64, 255).endVertex();
        bufferBuilder.func_225582_a_((double) 0 + base.width, base.height, 0.0D).func_225583_a_(((float) base.width / f), ((float) headerHeight / f)).func_225586_a_(64, 64, 64, 255).endVertex();

        bufferBuilder.func_225582_a_((double) 0 + base.width, (double) base.height - footerHeight, 0.0D).func_225583_a_(((float) base.width / f), ((float) 0 / f)).func_225586_a_(64, 64, 64, 255).endVertex();
        bufferBuilder.func_225582_a_(0, (double) base.height - footerHeight, 0.0D).func_225583_a_(0.0F, ((float) 0 / f)).func_225586_a_(64, 64, 64, 255).endVertex();

        tessellator.draw();

        RenderSystem.popMatrix();

        base.drawCenteredString(base.getMinecraft().fontRenderer, headerText, base.width / 2, 8, 16777215);
    }
}