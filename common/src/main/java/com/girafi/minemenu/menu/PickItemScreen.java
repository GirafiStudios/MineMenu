package com.girafi.minemenu.menu;

import com.girafi.minemenu.gui.ScreenStack;
import com.girafi.minemenu.helper.ItemRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.util.List;

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
        guiGraphics.drawCenteredString(this.font, "Pick an item", this.width / 2, 8, -1);
        Minecraft mc = this.minecraft;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, AbstractContainerScreen.INVENTORY_LOCATION, guiLeft, guiTop, 0, 0, XSIZE, YSIZE, 256, 256);

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
                guiGraphics.renderTooltip(this.font, List.of(ClientTooltipComponent.create(mousedOver.getItem().getItemName().getVisualOrderText())), mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
            }
        }
    }

    private void drawSlot(@Nonnull GuiGraphics guiGraphics, Slot slot, boolean scale) {
        int x = slot.x;
        int y = slot.y;
        ItemStack stack = slot.getItem();

        if (stack.isEmpty()) {
            Identifier slotNoItemIcon = slot.getNoItemIcon();

            if (slotNoItemIcon != null) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, slotNoItemIcon, this.guiLeft + x, this.guiTop + y, 0, 16, 16);
            }
        }

        if (!stack.isEmpty()) {
            if (scale) {
                Matrix3x2fStack ps = guiGraphics.pose();
                ps.pushMatrix();
                ps.scale(1.5F, 1.5F);
                ItemRenderHelper.renderItem(guiGraphics, (int) ((this.guiLeft + x) / 1.5D) + 6, (int) ((this.guiTop + y) / 1.5D) + 6, stack);
                ps.popMatrix();
            } else {
                ItemRenderHelper.renderItem(guiGraphics, this.guiLeft + x + 8, this.guiTop + y + 8, stack);
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        Minecraft mc = this.minecraft;

        if (mc != null && (event.input() == 0 && mc.player != null)) {
            for (int i1 = 0; i1 < mc.player.inventoryMenu.slots.size(); ++i1) {
                Slot slot = mc.player.inventoryMenu.slots.get(i1);
                if (event.x() - guiLeft >= slot.x && event.x() - guiLeft <= slot.x + 16 && event.y() - guiTop >= slot.y && event.y() - guiTop <= slot.y + 16) {
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
    public boolean keyPressed(KeyEvent keyEvent) {
        if (keyEvent.key() == GLFW.GLFW_KEY_ESCAPE) {
            ScreenStack.pop();
            return true;
        } else {
            return super.keyPressed(keyEvent);
        }
    }
}