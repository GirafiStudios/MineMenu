package dmillerw.menu.gui.menu.button;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiUtils;
import dmillerw.menu.helper.ItemRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class GuiItemButton extends GuiButtonExt {

    public ItemStack icon;

    public GuiItemButton(int id, int xPos, int yPos, int width, int height, ItemStack icon) {
        super(id, xPos, yPos, width, height, "");
        this.icon = icon;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int k = this.getHoverState(this.field_146123_n);
            GuiUtils.drawContinuousTexturedBox(buttonTextures, this.xPosition, this.yPosition, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
            this.mouseDragged(mc, mouseX, mouseY);

            GL11.glPushMatrix();
            if (this.icon == null) {
                this.icon = new ItemStack(Blocks.stone);
            }
            ItemRenderHelper.renderItem(this.xPosition + this.width / 2, this.yPosition + this.height / 2, this.zLevel, icon);
            GL11.glPopMatrix();
        }
    }
}