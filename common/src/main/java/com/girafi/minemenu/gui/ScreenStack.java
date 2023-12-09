package com.girafi.minemenu.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.List;

public class ScreenStack {
    private static final List<Screen> GUI_LIST = new ArrayList<>();

    public static void push(Screen screen) {
        GUI_LIST.add(0, screen);
        Minecraft.getInstance().setScreen(screen);
    }

    public static void pop() {
        if (GUI_LIST.size() > 0) {
            GUI_LIST.remove(0);
        }
        if (GUI_LIST.size() > 0) {
            Minecraft.getInstance().setScreen(GUI_LIST.get(0));
        } else {
            Minecraft.getInstance().setScreen(null);
        }
    }
}