package dmillerw.menu.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.List;

public class GuiStack {
    private static final List<GuiScreen> GUI_LIST = new ArrayList<>();

    public static void push(GuiScreen screen) {
        GUI_LIST.add(0, screen);
        Minecraft.getMinecraft().displayGuiScreen(screen);
    }

    public static void pop() {
        if (GUI_LIST.size() > 0) {
            GUI_LIST.remove(0);
        }
        if (GUI_LIST.size() > 0) {
            Minecraft.getMinecraft().displayGuiScreen(GUI_LIST.get(0));
        } else {
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
    }
}