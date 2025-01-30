package com.girafi.minemenu.menu.button;

import com.girafi.minemenu.helper.ItemRenderHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.GameRenderer;
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
    public void renderWidget(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            super.renderWidget(guiGraphics, mouseX, mouseY, partial);
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
            int k = !this.active ? 0 : (this.isHoveredOrFocused() ? 2 : 1);
            RenderSystem.setShader(CoreShaders.POSITION_TEX);
            //ScreenUtils.blitWithBorder(guiGraphics, WIDGETS_LOCATION, this.getX(), this.getY(), 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, 0); //Old way


            if (this.icon.isEmpty()) {
                this.icon = new ItemStack(Blocks.STONE);
            }
            ItemRenderHelper.renderItem(guiGraphics, this.getX() + this.width / 2, this.getY() + this.height / 2, icon);
        }
    }
}