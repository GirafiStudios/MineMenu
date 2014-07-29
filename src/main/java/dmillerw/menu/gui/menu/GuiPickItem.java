package dmillerw.menu.gui.menu;

import dmillerw.menu.gui.GuiStack;
import dmillerw.menu.helper.GuiRenderHelper;
import dmillerw.menu.helper.ItemRenderHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

/**
 * @author dmillerw
 */
public class GuiPickItem extends GuiScreen {

    private static final int XSIZE = 176;
    private static final int YSIZE = 166;

    private int guiLeft;
    private int guiTop;

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - XSIZE) / 2;
        this.guiTop = (this.height - YSIZE) / 2;
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();
        super.drawScreen(par1, par2, par3);
        GuiRenderHelper.renderHeaderAndFooter(this, 25, 20, 5, "Pick an Item:");
        this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, XSIZE, YSIZE);

        Slot mousedOver = null;

        // Draw inventory contents
        GL11.glPushMatrix();
        for (int i1 = 0; i1 < this.mc.thePlayer.inventoryContainer.inventorySlots.size(); ++i1) {
            Slot slot = (Slot) this.mc.thePlayer.inventoryContainer.inventorySlots.get(i1);
            if (par1 - guiLeft >= slot.xDisplayPosition && par1 - guiLeft <= slot.xDisplayPosition + 16 && par2 - guiTop >= slot.yDisplayPosition && par2 - guiTop <= slot.yDisplayPosition + 16) {
                mousedOver = slot;
            } else {
                this.drawSlot(slot, false);
            }
        }
        if (mousedOver != null) {
            drawSlot(mousedOver, true);
        }
        GL11.glPopMatrix();
    }

    private void drawSlot(Slot slot, boolean scale) {
        int i = slot.xDisplayPosition;
        int j = slot.yDisplayPosition;
        ItemStack itemstack = slot.getStack();

        this.zLevel = 100.0F;
        itemRender.zLevel = 100.0F;

        if (itemstack == null) {
            IIcon iicon = slot.getBackgroundIconIndex();

            if (iicon != null) {
                GL11.glDisable(GL11.GL_LIGHTING);
                this.mc.getTextureManager().bindTexture(TextureMap.locationItemsTexture);
                this.drawTexturedModelRectFromIcon(this.guiLeft + i, this.guiTop + j, iicon, 16, 16);
                GL11.glEnable(GL11.GL_LIGHTING);
            }
        }

        if (itemstack != null) {
            if (scale) {
                GL11.glScaled(2, 2, 2);
                ItemRenderHelper.renderItem((this.guiLeft + i + 8) / 2, (this.guiTop + j + 8) / 2, 100F, itemstack);
            } else {
                ItemRenderHelper.renderItem(this.guiLeft + i + 8, this.guiTop + j + 8, 100F, itemstack);
            }
        }

        itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 0) {
            for (int i1 = 0; i1 < this.mc.thePlayer.inventoryContainer.inventorySlots.size(); ++i1) {
                Slot slot = (Slot) this.mc.thePlayer.inventoryContainer.inventorySlots.get(i1);
                if (mouseX - guiLeft >= slot.xDisplayPosition && mouseX - guiLeft <= slot.xDisplayPosition + 16 && mouseY - guiTop >= slot.yDisplayPosition && mouseY - guiTop <= slot.yDisplayPosition + 16) {
                    GuiClickAction.item = slot.getStack().copy();
                    GuiStack.pop();
                }
            }
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void keyTyped(char key, int keycode) {
        if (keycode == 1) {
            GuiStack.pop();
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button.id == 0) {
                GuiStack.pop();
            }
        }
    }
}
