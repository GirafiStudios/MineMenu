package com.girafi.minemenu.helper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemRenderHelper {

    public static void renderItem(GuiGraphics guiGraphics, int x, int y, @Nonnull ItemStack stack) {
        x -= 8;
        y -= 8;

        if (!stack.isEmpty()) {
            guiGraphics.renderItem(stack, x, y);
        }
    }
}