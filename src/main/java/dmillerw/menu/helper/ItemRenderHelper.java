package dmillerw.menu.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemRenderHelper {

    public static void renderItem(int x, int y, @Nonnull ItemStack stack) {
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        x -= 8;
        y -= 8;

        if (!stack.isEmpty()) {
            Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(stack, x, y);
        }
        GlStateManager.popMatrix();
    }
}