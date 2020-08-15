package dmillerw.menu.gui.menu.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dmillerw.menu.helper.ItemRenderHelper;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import javax.annotation.Nonnull;

public class ItemButton extends ExtendedButton {
    @Nonnull
    public ItemStack icon;

    public ItemButton(int xPos, int yPos, int width, int height, @Nonnull ItemStack icon, IPressable handler) {
        super(xPos, yPos, width, height, new StringTextComponent(""), handler);
        this.icon = icon;
    }

    @Override
    public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int k = this.getYImage(this.isHovered);
            GuiUtils.drawContinuousTexturedBox(WIDGETS_LOCATION, this.x, this.y, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, this.getBlitOffset());
            this.renderBg(mStack, Minecraft.getInstance(), mouseX, mouseY);

            RenderSystem.pushMatrix();
            if (this.icon.isEmpty()) {
                this.icon = new ItemStack(Blocks.STONE);
            }
            ItemRenderHelper.renderItem(this.x + this.width / 2, this.y + this.height / 2, icon);
            RenderSystem.popMatrix();
        }
    }
}