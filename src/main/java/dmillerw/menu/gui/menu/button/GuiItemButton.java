package dmillerw.menu.gui.menu.button;

import dmillerw.menu.helper.ItemRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;

import javax.annotation.Nonnull;

public class GuiItemButton extends GuiButtonExt {
    @Nonnull
    public ItemStack icon;

    public GuiItemButton(int id, int xPos, int yPos, int width, int height, @Nonnull ItemStack icon) {
        super(id, xPos, yPos, width, height, "");
        this.icon = icon;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int k = this.getHoverState(this.hovered);
            GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.xPosition, this.yPosition, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
            this.mouseDragged(mc, mouseX, mouseY);

            GlStateManager.pushMatrix();
            if (this.icon.isEmpty()) {
                this.icon = new ItemStack(Blocks.STONE);
            }
            ItemRenderHelper.renderItem(this.xPosition + this.width / 2, this.yPosition + this.height / 2, icon);
            GlStateManager.popMatrix();
        }
    }
}