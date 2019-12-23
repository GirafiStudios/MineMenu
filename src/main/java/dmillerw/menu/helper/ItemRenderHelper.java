package dmillerw.menu.helper;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemRenderHelper {

    public static void renderItem(int x, int y, @Nonnull ItemStack stack) {
        RenderSystem.pushMatrix();
        x -= 8;
        y -= 8;

        if (!stack.isEmpty()) {
            Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(stack, x, y);
        }
        RenderSystem.popMatrix();
    }
}