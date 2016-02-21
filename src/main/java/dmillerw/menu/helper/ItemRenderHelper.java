package dmillerw.menu.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

/**
 * @author dmillerw
 */
public class ItemRenderHelper {

    public static void renderItem(float x, float y, ItemStack stack) {
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();

        x -= 8;
        y -= 8;

        if (stack != null) {
            Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(stack, (int) x, (int) y);
        }

        GlStateManager.popMatrix();
    }
}