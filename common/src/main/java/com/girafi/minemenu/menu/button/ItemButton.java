package com.girafi.minemenu.menu.button;

import com.girafi.minemenu.helper.ItemRenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nonnull;

public class ItemButton extends Button {
    @Nonnull
    public ItemStack icon;

    public ItemButton(int xPos, int yPos, int width, int height, @Nonnull ItemStack icon, Button.OnPress handler) {
        super(xPos, yPos, width, height, Component.literal(""), handler, DEFAULT_NARRATION);
        this.icon = icon;
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.renderDefaultSprite(guiGraphics);
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;

            if (this.icon.isEmpty()) {
                this.icon = new ItemStack(Blocks.STONE);
            }
            ItemRenderHelper.renderItem(guiGraphics, this.getX() + this.width / 2, this.getY() + this.height / 2, icon);
        }
    }
}