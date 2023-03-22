package dmillerw.menu.helper;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemRenderHelper {

    public static void renderItem(PoseStack poseStack, int x, int y, @Nonnull ItemStack stack) {
        poseStack.pushPose();
        x -= 8;
        y -= 8;

        if (!stack.isEmpty()) {
            Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(poseStack, stack, x, y);
        }
        poseStack.popPose();
    }
}