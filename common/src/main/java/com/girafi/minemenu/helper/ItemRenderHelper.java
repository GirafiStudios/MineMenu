package com.girafi.minemenu.helper;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemRenderHelper {

    public static void renderItem(GuiGraphicsExtractor guiGraphics, int x, int y, @Nonnull ItemStack stack) {
        x -= 8;
        y -= 8;

        if (!stack.isEmpty()) {
            guiGraphics.item(stack, x, y);
        }
    }
}