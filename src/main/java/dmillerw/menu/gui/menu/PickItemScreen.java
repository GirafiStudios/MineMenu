package dmillerw.menu.gui.menu;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import dmillerw.menu.gui.ScreenStack;
import dmillerw.menu.helper.GuiRenderHelper;
import dmillerw.menu.helper.ItemRenderHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;

public class PickItemScreen extends Screen {
    private static final int XSIZE = 176;
    private static final int YSIZE = 166;
    private int guiLeft;
    private int guiTop;

    public PickItemScreen() {
        super(new TranslationTextComponent("minemenu.itemScreen.title"));
    }

    @Override
    public void func_231160_c_() {
        super.func_231160_c_();
        this.guiLeft = (this.field_230708_k_ - XSIZE) / 2;
        this.guiTop = (this.field_230709_l_ - YSIZE) / 2;
    }

    @Override
    public void func_230430_a_(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.func_230446_a_(matrixStack);
        super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
        GuiRenderHelper.renderHeaderAndFooter(matrixStack, this, 25, 20, 5, "Pick an Item:");
        this.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
        this.func_238474_b_(matrixStack, guiLeft, guiTop, 0, 0, XSIZE, YSIZE);

        Slot mousedOver = null;

        // Draw inventory contents
        if (this.getMinecraft().player != null) {
            RenderSystem.pushMatrix();
            for (int i1 = 0; i1 < this.getMinecraft().player.container.inventorySlots.size(); ++i1) {
                Slot slot = this.getMinecraft().player.container.inventorySlots.get(i1);
                if (mouseX - guiLeft >= slot.xPos && mouseX - guiLeft <= slot.xPos + 16 && mouseY - guiTop >= slot.yPos && mouseY - guiTop <= slot.yPos + 16) {
                    mousedOver = slot;
                } else {
                    this.drawSlot(matrixStack, slot, false);
                }
            }
            if (mousedOver != null && !mousedOver.getStack().isEmpty()) {
                RenderSystem.pushMatrix();
                drawSlot(matrixStack, mousedOver, true);
                RenderSystem.popMatrix();
                func_230457_a_(matrixStack, mousedOver.getStack(), mouseX, mouseY);
            }
            RenderSystem.popMatrix();
        }
    }

    private void drawSlot(@Nonnull MatrixStack matrixStack, Slot slot, boolean scale) {
        int x = slot.xPos;
        int y = slot.yPos;
        ItemStack stack = slot.getStack();

        this.func_230926_e_(100);
        field_230707_j_.zLevel = 100.0F;

        if (stack.isEmpty()) {
            Pair<ResourceLocation, ResourceLocation> pair = slot.func_225517_c_();

            if (pair != null) {
                TextureAtlasSprite sprite = this.getMinecraft().getAtlasSpriteGetter(pair.getFirst()).apply(pair.getSecond());
                this.getMinecraft().getTextureManager().bindTexture(sprite.getAtlasTexture().getTextureLocation());
                func_238470_a_(matrixStack, this.guiLeft + x, this.guiTop + y, this.func_230927_p_(), 16, 16, sprite);
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

        field_230707_j_.zLevel = 0.0F;
        this.func_230926_e_(0);
    }

    @Override
    public boolean func_231044_a_(double mouseX, double mouseY, int button) {
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
    public void func_231164_f_() {
        this.getMinecraft().keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public boolean func_231177_au__() {
        return false;
    }

    @Override
    public boolean func_231046_a_(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (p_keyPressed_1_ == GLFW.GLFW_KEY_ESCAPE) {
            ScreenStack.pop();
            return true;
        } else {
            return super.func_231046_a_(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        }
    }
}