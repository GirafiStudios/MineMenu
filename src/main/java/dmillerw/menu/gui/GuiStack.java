package dmillerw.menu.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dmillerw
 */
public class GuiStack {
    private static final List<GuiScreen> guiList = new ArrayList<GuiScreen>();

    public static void push(GuiScreen screen) {
        guiList.add(0, screen);
        Minecraft.getMinecraft().displayGuiScreen(screen);
    }

    public static void pop() {
        if (guiList.size() > 0) {
            guiList.remove(0);
        }
        if (guiList.size() > 0) {
            Minecraft.getMinecraft().displayGuiScreen(guiList.get(0));
        } else {
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
    }
}