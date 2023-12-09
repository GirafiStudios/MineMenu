package com.girafi.minemenu.data.menu;

import com.girafi.minemenu.data.click.ClickAction;
import com.google.gson.annotations.SerializedName;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class MenuItem {
    public final String title;
    @Nonnull
    public final ItemStack icon;

    @SerializedName("action")
    public final ClickAction.IClickAction clickAction;

    public MenuItem(String title, @Nonnull ItemStack icon, ClickAction.IClickAction clickAction) {
        this.title = title;
        this.icon = icon;
        this.clickAction = clickAction;
    }

    public void onRemoved() {
        if (clickAction != null) {
            clickAction.onRemoved();
        }
    }
}