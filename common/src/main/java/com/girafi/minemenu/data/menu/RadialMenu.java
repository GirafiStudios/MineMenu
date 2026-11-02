package com.girafi.minemenu.data.menu;

import com.girafi.minemenu.util.Config;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Set;

public class RadialMenu {
    public static final int MAX_ITEMS = 10;
    private static final String MAIN_TAG = "main";
    private static final Map<String, Category> MENU_MAP = Maps.newHashMap();
    public static String currentCategory = MAIN_TAG;
    public static int animationTimer = 20;

    public static Set<String> getCategories() {
        return MENU_MAP.keySet();
    }

    public static void resetCategory() {
        currentCategory = MAIN_TAG;
    }

    public static void tickTimer() {
        if (!Config.GENERAL.menuAnimation.get()) {
            animationTimer = 0;
        } else if (animationTimer > 0) {
            animationTimer -= 5;
        }
    }

    public static void resetTimer() {
        animationTimer = 20;
    }

    public static MenuItem[] getActiveArray() {
        return getActiveCategory().items;
    }

    public static Category getActiveCategory() {
        return getCategory(currentCategory);
    }

    public static MenuItem[] getArray(String tag) {
        return getCategory(tag).items;
    }

    public static Category getCategory(String tag) {
        return MENU_MAP.computeIfAbsent(tag, t -> new Category());
    }
}