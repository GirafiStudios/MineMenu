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
    public void func_230431_b_(MatrixStack mStack, int mouseX, int mouseY, float partial) {
        if (this.field_230694_p_) {
            this.field_230692_n_ = mouseX >= this.field_230690_l_ && mouseY >= this.field_230691_m_ && mouseX < this.field_230690_l_ + this.field_230688_j_ && mouseY < this.field_230691_m_ + this.field_230689_k_;
            int k = this.func_230989_a_(this.field_230692_n_);
            GuiUtils.drawContinuousTexturedBox(field_230687_i_, this.field_230690_l_, this.field_230691_m_, 0, 46 + k * 20, this.field_230688_j_, this.field_230689_k_, 200, 20, 2, 3, 2, 2, this.func_230927_p_());
            this.func_230441_a_(mStack, Minecraft.getInstance(), mouseX, mouseY);

            RenderSystem.pushMatrix();
            if (this.icon.isEmpty()) {
                this.icon = new ItemStack(Blocks.STONE);
            }
            ItemRenderHelper.renderItem(this.field_230690_l_ + this.field_230688_j_ / 2, this.field_230691_m_ + this.field_230689_k_ / 2, icon);
            RenderSystem.popMatrix();
        }
    }
}