package dmillerw.menu.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.ForgeHooksClient;

/**
 * @author dmillerw
 */
public class RenderHelper {

	private static RenderBlocks renderBlocks;

	private static RenderItem renderItem;

	public static void renderItem(float x, float y, float z, ItemStack stack) {
		if (renderBlocks == null || renderBlocks.blockAccess != Minecraft.getMinecraft().theWorld) {
			renderBlocks = new RenderBlocks(Minecraft.getMinecraft().theWorld);
		}

		if (renderItem == null) {
			renderItem = new RenderItem();
		}

		if (!ForgeHooksClient.renderInventoryItem(renderBlocks, Minecraft.getMinecraft().getTextureManager(), stack, true, z, x, y)) {
			renderItem.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), stack, (int)x, (int)y);
		}
	}

	public static void renderIcon(float x, float y, float z, IIcon icon, float width, float height) {
		render(x, y, z, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), width, height);
	}

	public static void render(float x, float y, float z, double minU, double minV, double maxU, double maxV, float width, float height) {
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + 0, y + height, z, minU, maxV);
		tessellator.addVertexWithUV(x + width, y + height, z, maxU, maxV);
		tessellator.addVertexWithUV(x + width, y + 0, z, maxU, minV);
		tessellator.addVertexWithUV(x + 0, y + 0, z, minU, minV);
		tessellator.draw();
	}
}
