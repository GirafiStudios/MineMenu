package com.girafi.minemenu.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.List;

public class ScreenStack {
    private static final List<Screen> GUI_LIST = new ArrayList<>();

    public static void open(Screen screen) {
        GUI_LIST.clear();
        push(screen);
    }

    public static void close() {
        GUI_LIST.clear();
        Minecraft.getInstance().gui.setScreen(null);
    }

    public static void push(Screen screen) {
        GUI_LIST.addFirst(screen);
        Minecraft.getInstance().gui.setScreen(screen);
    }

    public static void pop() {
        if (!GUI_LIST.isEmpty()) {
            GUI_LIST.removeFirst();
        }
        if (!GUI_LIST.isEmpty()) {
            Minecraft.getInstance().gui.setScreen(GUI_LIST.get(0));
        } else {
            Minecraft.getInstance().gui.setScreen(null);
        }
    }
}