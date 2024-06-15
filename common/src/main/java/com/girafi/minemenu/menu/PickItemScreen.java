package com.girafi.minemenu.menu;

import com.girafi.minemenu.gui.ScreenStack;
import com.girafi.minemenu.helper.ItemRenderHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.drawCenteredString(this.font, "Pick an item", this.width / 2, 8, 16777215);
        Minecraft mc = this.minecraft;
        guiGraphics.blit(ResourceLocation.withDefaultNamespace("textures/gui/container/inventory.png"), guiLeft, guiTop, 0, 0, XSIZE, YSIZE);

        Slot mousedOver = null;

        // Draw inventory contents
        if (mc != null && mc.player != null) {
            for (int i1 = 0; i1 < mc.player.inventoryMenu.slots.size(); ++i1) {
                Slot slot = mc.player.inventoryMenu.slots.get(i1);
                if (mouseX - guiLeft >= slot.x && mouseX - guiLeft <= slot.x + 16 && mouseY - guiTop >= slot.y && mouseY - guiTop <= slot.y + 16) {
                    mousedOver = slot;
                } else {
                    this.drawSlot(guiGraphics, slot, false);
                }
            }
            if (mousedOver != null && !mousedOver.getItem().isEmpty()) {
                drawSlot(guiGraphics, mousedOver, true);
                guiGraphics.renderTooltip(this.font, mousedOver.getItem(), mouseX, mouseY);
            }
        }
    }

    private void drawSlot(@Nonnull GuiGraphics guiGraphics, Slot slot, boolean scale) {
        int x = slot.x;
        int y = slot.y;
        ItemStack stack = slot.getItem();
        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, 100.0F);

        if (stack.isEmpty()) {
            Pair<ResourceLocation, ResourceLocation> pair = slot.getNoItemIcon();

            if (pair != null) {
                TextureAtlasSprite sprite = this.minecraft.getTextureAtlas(pair.getFirst()).apply(pair.getSecond());
                RenderSystem.setShaderTexture(0, sprite.atlasLocation());
                guiGraphics.blit(this.guiLeft + x, this.guiTop + y, 0, 16, 16, sprite);
            }
        }

        if (!stack.isEmpty()) {
            if (scale) {
                poseStack.scale(1.5F, 1.5F, 1.5F);
                ItemRenderHelper.renderItem(guiGraphics, (int) ((this.guiLeft + x) / 1.5D) + 6, (int) ((this.guiTop + y) / 1.5D) + 6, stack);
            } else {
                ItemRenderHelper.renderItem(guiGraphics, this.guiLeft + x + 8, this.guiTop + y + 8, stack);
            }
        }
        poseStack.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Minecraft mc = this.minecraft;

        if (mc != null && (button == 0 && mc.player != null)) {
            for (int i1 = 0; i1 < mc.player.inventoryMenu.slots.size(); ++i1) {
                Slot slot = mc.player.inventoryMenu.slots.get(i1);
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