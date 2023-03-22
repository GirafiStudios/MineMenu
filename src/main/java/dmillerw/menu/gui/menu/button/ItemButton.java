package dmillerw.menu.gui.menu.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dmillerw.menu.helper.ItemRenderHelper;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.gui.ScreenUtils;
import net.minecraftforge.client.gui.widget.ExtendedButton;

import javax.annotation.Nonnull;

public class ItemButton extends ExtendedButton {
    @Nonnull
    public ItemStack icon;

    public ItemButton(int xPos, int yPos, int width, int height, @Nonnull ItemStack icon, OnPress handler) {
        super(xPos, yPos, width, height, Component.literal(""), handler);
        this.icon = icon;
    }

    @Override
    public void renderWidget(PoseStack poseStack, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            super.renderWidget(poseStack, mouseX, mouseY, partial);
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
            int k = !this.active ? 0 : (this.isHoveredOrFocused() ? 2 : 1);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            ScreenUtils.blitWithBorder(poseStack, WIDGETS_LOCATION, this.getX(), this.getY(), 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, 0);

            if (this.icon.isEmpty()) {
                this.icon = new ItemStack(Blocks.STONE);
            }
            ItemRenderHelper.renderItem(poseStack, this.getX() + this.width / 2, this.getY() + this.height / 2, icon);
        }
    }
}