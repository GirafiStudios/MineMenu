package dmillerw.menu.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;

/**
 * @author dmillerw
 */
public class ItemRenderHelper {

    private static RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

    public static void renderItem(float x, float y, float z, ItemStack stack) {
        GlStateManager.pushMatrix();

        RenderHelper.enableGUIStandardItemLighting();

        GL11.glDisable(2896);
        GL11.glEnable(32826);
        GL11.glEnable(2903);
        GL11.glEnable(2896);

         x -= 8;
        y -= 8;

        /*if (renderBlocks == null || renderBlocks.blockAccess != Minecraft.getMinecraft().theWorld) {
            renderBlocks = new RenderBlocks(Minecraft.getMinecraft().theWorld);
        }*/

        /*if (renderItem == null) {
            renderItem = new RenderItem(Minecraft.getMinecraft().getTextureManager(), new ModelManager(TextureMap.LOCATION_MISSING_TEXTURE.));
        }*/

        //if (!ForgeHooksClient.renderInventoryItem(renderBlocks, Minecraft.getMinecraft().getTextureManager(), stack, true, z, x, y)) {
        //renderItem.renderItemAndEffectIntoGUI(stack, (int) x, (int) y);
        //}

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glDisable(2896);

        GlStateManager.popMatrix();
    }

    public static void renderIcon(float x, float y, float z, TextureAtlasSprite icon, float width, float height) {
        render(x, y, z, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), width, height);
    }

    public static void render(float x, float y, float z, double minU, double minV, double maxU, double maxV, float width, float height) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x - width / 2, y + height / 2, z).tex(minU, maxV).endVertex();
        worldrenderer.pos(x + width / 2, y + height / 2, z).tex(maxU, maxV).endVertex();
        worldrenderer.pos(x + width / 2, y - height / 2, z).tex(maxU, minV).endVertex();
        worldrenderer.pos(x - width / 2, y - height / 2, z).tex(minU, minV).endVertex();
        tessellator.draw();
    }
}
