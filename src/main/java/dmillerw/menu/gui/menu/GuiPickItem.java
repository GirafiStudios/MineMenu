package dmillerw.menu.gui.menu;

import dmillerw.menu.gui.GuiStack;
import dmillerw.menu.helper.GuiRenderHelper;
import dmillerw.menu.helper.ItemRenderHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

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
        GlStateManager.pushMatrix();
        for (int i1 = 0; i1 < this.mc.player.inventoryContainer.inventorySlots.size(); ++i1) {
            Slot slot = this.mc.player.inventoryContainer.inventorySlots.get(i1);
            if (par1 - guiLeft >= slot.xPos && par1 - guiLeft <= slot.xPos + 16 && par2 - guiTop >= slot.yPos && par2 - guiTop <= slot.yPos + 16) {
                mousedOver = slot;
            } else {
                this.drawSlot(slot, false);
            }
        }
        if (mousedOver != null && !mousedOver.getStack().isEmpty()) {
            GlStateManager.pushMatrix();
            drawSlot(mousedOver, true);
            GlStateManager.popMatrix();
            renderToolTip(mousedOver.getStack(), par1, par2);
        }
        GlStateManager.popMatrix();
    }

    private void drawSlot(Slot slot, boolean scale) {
        int i = slot.xPos;
        int j = slot.yPos;
        ItemStack stack = slot.getStack();

        this.zLevel = 100.0F;
        itemRender.zLevel = 100.0F;

        if (stack.isEmpty()) {
            TextureAtlasSprite sprite = slot.getBackgroundSprite();

            if (sprite != null) {
                GlStateManager.disableLighting();
                this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                this.drawTexturedModalRect(this.guiLeft + i, this.guiTop + j, sprite, 16, 16);
                GlStateManager.enableLighting();
            }
        }

        if (!stack.isEmpty()) {
            if (scale) {
                GlStateManager.scale(2, 2, 2);
                ItemRenderHelper.renderItem((this.guiLeft + i + 8) / 2, (this.guiTop + j + 8) / 2, stack);
            } else {
                ItemRenderHelper.renderItem(this.guiLeft + i + 8, this.guiTop + j + 8, stack);
            }
        }

        itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 0) {
            for (int i1 = 0; i1 < this.mc.player.inventoryContainer.inventorySlots.size(); ++i1) {
                Slot slot = this.mc.player.inventoryContainer.inventorySlots.get(i1);
                if (mouseX - guiLeft >= slot.xPos && mouseX - guiLeft <= slot.xPos + 16 && mouseY - guiTop >= slot.yPos && mouseY - guiTop <= slot.yPos + 16) {
                    ItemStack stack = slot.getStack();
                    if (!stack.isEmpty()) {
                        GuiClickAction.item = stack.copy();
                        GuiStack.pop();
                    }
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