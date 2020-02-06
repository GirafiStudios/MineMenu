package dmillerw.menu.gui.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import dmillerw.menu.gui.ScreenStack;
import dmillerw.menu.helper.GuiRenderHelper;
import dmillerw.menu.helper.ItemRenderHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

public class PickItemScreen extends Screen {
    private static final int XSIZE = 176;
    private static final int YSIZE = 166;
    private int guiLeft;
    private int guiTop;

    public PickItemScreen() {
        super(new TranslationTextComponent("minemenu.itemScreen.title"));
    }

    @Override
    public void init() {
        super.init();
        this.guiLeft = (this.width - XSIZE) / 2;
        this.guiTop = (this.height - YSIZE) / 2;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        GuiRenderHelper.renderHeaderAndFooter(this, 25, 20, 5, "Pick an Item:");
        this.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
        this.blit(guiLeft, guiTop, 0, 0, XSIZE, YSIZE);

        Slot mousedOver = null;

        // Draw inventory contents
        if (this.getMinecraft().player != null) {
            RenderSystem.pushMatrix();
            for (int i1 = 0; i1 < this.getMinecraft().player.container.inventorySlots.size(); ++i1) {
                Slot slot = this.getMinecraft().player.container.inventorySlots.get(i1);
                if (mouseX - guiLeft >= slot.xPos && mouseX - guiLeft <= slot.xPos + 16 && mouseY - guiTop >= slot.yPos && mouseY - guiTop <= slot.yPos + 16) {
                    mousedOver = slot;
                } else {
                    this.drawSlot(slot, false);
                }
            }
            if (mousedOver != null && !mousedOver.getStack().isEmpty()) {
                RenderSystem.pushMatrix();
                drawSlot(mousedOver, true);
                RenderSystem.popMatrix();
                renderTooltip(mousedOver.getStack(), mouseX, mouseY);
            }
            RenderSystem.popMatrix();
        }
    }

    private void drawSlot(Slot slot, boolean scale) {
        int x = slot.xPos;
        int y = slot.yPos;
        ItemStack stack = slot.getStack();

        this.setBlitOffset(100);
        itemRenderer.zLevel = 100.0F;

        if (stack.isEmpty()) {
            Pair<ResourceLocation, ResourceLocation> pair = slot.func_225517_c_();

            if (pair != null) {
                TextureAtlasSprite sprite = this.getMinecraft().getTextureGetter(pair.getFirst()).apply(pair.getSecond());
                this.getMinecraft().getTextureManager().bindTexture(sprite.getAtlasTexture().getBasePath());
                blit(this.guiLeft + x, this.guiTop + y, this.getBlitOffset(), 16, 16, sprite);
            }
        }

        if (!stack.isEmpty()) {
            if (scale) {
                RenderSystem.scaled(2, 2, 2);
                ItemRenderHelper.renderItem((this.guiLeft + x + 8) / 2, (this.guiTop + y + 8) / 2, stack);
            } else {
                ItemRenderHelper.renderItem(this.guiLeft + x + 8, this.guiTop + y + 8, stack);
            }
        }

        itemRenderer.zLevel = 0.0F;
        this.setBlitOffset(0);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.getMinecraft().player != null) {
            for (int i1 = 0; i1 < this.getMinecraft().player.container.inventorySlots.size(); ++i1) {
                Slot slot = this.getMinecraft().player.container.inventorySlots.get(i1);
                if (mouseX - guiLeft >= slot.xPos && mouseX - guiLeft <= slot.xPos + 16 && mouseY - guiTop >= slot.yPos && mouseY - guiTop <= slot.yPos + 16) {
                    ItemStack stack = slot.getStack();
                    if (!stack.isEmpty()) {
                        ClickActionScreen.item = stack.copy();
                        ScreenStack.pop();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void removed() {
        this.getMinecraft().keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (p_keyPressed_1_ == GLFW.GLFW_KEY_ESCAPE) {
            ScreenStack.pop();
            return true;
        } else {
            return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        }
    }
}