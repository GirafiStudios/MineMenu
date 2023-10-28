package dmillerw.menu.gui.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import dmillerw.menu.gui.ScreenStack;
import dmillerw.menu.helper.GuiRenderHelper;
import dmillerw.menu.helper.ItemRenderHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;

public class PickItemScreen extends Screen {
    private static final int XSIZE = 176;
    private static final int YSIZE = 166;
    private int guiLeft;
    private int guiTop;

    public PickItemScreen() {
        super(Component.translatable("mine_menu.itemScreen.title"));
    }

    @Override
    public void init() {
        super.init();
        this.guiLeft = (this.width - XSIZE) / 2;
        this.guiTop = (this.height - YSIZE) / 2;
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        GuiRenderHelper.renderHeaderAndFooter(poseStack, this, 25, 20, 5, "Pick an Item:");
        RenderSystem.setShaderTexture(0, new ResourceLocation("textures/gui/container/inventory.png"));
        this.blit(poseStack, guiLeft, guiTop, 0, 0, XSIZE, YSIZE);

        Slot mousedOver = null;

        // Draw inventory contents
        if (this.getMinecraft().player != null) {
            poseStack.pushPose();
            for (int i1 = 0; i1 < this.getMinecraft().player.inventoryMenu.slots.size(); ++i1) {
                Slot slot = this.getMinecraft().player.inventoryMenu.slots.get(i1);
                if (mouseX - guiLeft >= slot.x && mouseX - guiLeft <= slot.x + 16 && mouseY - guiTop >= slot.y && mouseY - guiTop <= slot.y + 16) {
                    mousedOver = slot;
                } else {
                    this.drawSlot(poseStack, slot, false);
                }
            }
            if (mousedOver != null && !mousedOver.getItem().isEmpty()) {
                poseStack.pushPose();
                drawSlot(poseStack, mousedOver, true);
                poseStack.popPose();
                renderTooltip(poseStack, mousedOver.getItem(), mouseX, mouseY);
            }
            poseStack.popPose();
        }
    }

    private void drawSlot(@Nonnull PoseStack poseStack, Slot slot, boolean scale) {
        int x = slot.x;
        int y = slot.y;
        ItemStack stack = slot.getItem();

        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, 100.0F);

        if (stack.isEmpty()) {
            Pair<ResourceLocation, ResourceLocation> pair = slot.getNoItemIcon();

            if (pair != null) {
                TextureAtlasSprite sprite = this.getMinecraft().getTextureAtlas(pair.getFirst()).apply(pair.getSecond());
                RenderSystem.setShaderTexture(0, sprite.atlasLocation());
                blit(poseStack, this.guiLeft + x, this.guiTop + y, 0, 16, 16, sprite);
            }
        }

        if (!stack.isEmpty()) {
            if (scale) {
                poseStack.scale(1.5F, 1.5F, 1.5F);
                ItemRenderHelper.renderItem(poseStack, (int) ((this.guiLeft + x) / 1.5D) + 6, (int) ((this.guiTop + y) / 1.5D) + 6, stack);
            } else {
                ItemRenderHelper.renderItem(poseStack, this.guiLeft + x + 8, this.guiTop + y + 8, stack);
            }
        }

        poseStack.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.getMinecraft().player != null) {
            for (int i1 = 0; i1 < this.getMinecraft().player.inventoryMenu.slots.size(); ++i1) {
                Slot slot = this.getMinecraft().player.inventoryMenu.slots.get(i1);
                if (mouseX - guiLeft >= slot.x && mouseX - guiLeft <= slot.x + 16 && mouseY - guiTop >= slot.y && mouseY - guiTop <= slot.y + 16) {
                    ItemStack stack = slot.getItem();
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