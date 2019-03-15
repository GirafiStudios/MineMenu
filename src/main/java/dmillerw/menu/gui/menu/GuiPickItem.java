package dmillerw.menu.gui.menu;

import dmillerw.menu.gui.GuiStack;
import dmillerw.menu.helper.GuiRenderHelper;
import dmillerw.menu.helper.ItemRenderHelper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.glfw.GLFW;

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
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.render(mouseX, mouseY, partialTicks);
        GuiRenderHelper.renderHeaderAndFooter(this, 25, 20, 5, "Pick an Item:");
        this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, XSIZE, YSIZE);

        Slot mousedOver = null;

        // Draw inventory contents
        GlStateManager.pushMatrix();
        for (int i1 = 0; i1 < this.mc.player.inventoryContainer.inventorySlots.size(); ++i1) {
            Slot slot = this.mc.player.inventoryContainer.inventorySlots.get(i1);
            if (mouseX - guiLeft >= slot.xPos && mouseX - guiLeft <= slot.xPos + 16 && mouseY - guiTop >= slot.yPos && mouseY - guiTop <= slot.yPos + 16) {
                mousedOver = slot;
            } else {
                this.drawSlot(slot, false);
            }
        }
        if (mousedOver != null && !mousedOver.getStack().isEmpty()) {
            GlStateManager.pushMatrix();
            drawSlot(mousedOver, true);
            GlStateManager.popMatrix();
            renderToolTip(mousedOver.getStack(), mouseX, mouseY);
        }
        GlStateManager.popMatrix();
    }

    private void drawSlot(Slot slot, boolean scale) {
        int x = slot.xPos;
        int y = slot.yPos;
        ItemStack stack = slot.getStack();

        this.zLevel = 100.0F;
        itemRender.zLevel = 100.0F;

        if (stack.isEmpty()) {
            TextureAtlasSprite sprite = slot.getBackgroundSprite();

            if (sprite != null) {
                GlStateManager.disableLighting();
                this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                this.drawTexturedModalRect(this.guiLeft + x, this.guiTop + y, sprite, 16, 16);
                GlStateManager.enableLighting();
            }
        }

        if (!stack.isEmpty()) {
            if (scale) {
                GlStateManager.scaled(2, 2, 2);
                ItemRenderHelper.renderItem((this.guiLeft + x + 8) / 2, (this.guiTop + y + 8) / 2, stack);
            } else {
                ItemRenderHelper.renderItem(this.guiLeft + x + 8, this.guiTop + y + 8, stack);
            }
        }

        itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (int i1 = 0; i1 < this.mc.player.inventoryContainer.inventorySlots.size(); ++i1) {
                Slot slot = this.mc.player.inventoryContainer.inventorySlots.get(i1);
                if (mouseX - guiLeft >= slot.xPos && mouseX - guiLeft <= slot.xPos + 16 && mouseY - guiTop >= slot.yPos && mouseY - guiTop <= slot.yPos + 16) {
                    ItemStack stack = slot.getStack();
                    if (!stack.isEmpty()) {
                        GuiClickAction.item = stack.copy();
                        GuiStack.pop();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onGuiClosed() {
        this.mc.keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (p_keyPressed_1_ == GLFW.GLFW_KEY_ESCAPE) {
            GuiStack.pop();
            return true;
        } else {
            return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        }
    }
}