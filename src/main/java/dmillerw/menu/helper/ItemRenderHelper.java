package dmillerw.menu.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * @author dmillerw
 */
public class ItemRenderHelper {

    public static void renderItem(float x, float y, @Nonnull ItemStack stack) {
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();

        x -= 8;
        y -= 8;

        if (!stack.isEmpty()) {
            Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(stack, (int) x, (int) y);
        }

        GlStateManager.popMatrix();
    }
}