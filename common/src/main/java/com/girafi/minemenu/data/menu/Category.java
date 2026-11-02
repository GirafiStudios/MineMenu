package com.girafi.minemenu.data.menu;

import com.girafi.minemenu.util.Config;
import net.minecraft.util.FastColor;

import javax.annotation.Nullable;

public class Category {
    public final MenuItem[] items = new MenuItem[RadialMenu.MAX_ITEMS];
    @Nullable
    public Integer menuColor;
    @Nullable
    public Integer selectColor;

    public int getMenuColor() {
        return this.menuColor != null ? this.menuColor : getDefaultMenuColor();
    }

    public int getSelectColor() {
        return this.selectColor != null ? this.selectColor : getDefaultSelectColor();
    }

    public boolean hasCustomColors() {
        return this.menuColor != null || this.selectColor != null;
    }

    public static int getDefaultMenuColor() {
        return FastColor.ARGB32.color(Config.VISUAL.menuAlpha.get(), Config.VISUAL.menuRed.get(), Config.VISUAL.menuGreen.get(), Config.VISUAL.menuBlue.get());
    }

    public static int getDefaultSelectColor() {
        return FastColor.ARGB32.color(Config.VISUAL.selectAlpha.get(), Config.VISUAL.selectRed.get(), Config.VISUAL.selectGreen.get(), Config.VISUAL.selectBlue.get());
    }
}
