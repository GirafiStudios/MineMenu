package dmillerw.menu.gui.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dmillerw
 */
public class GuiStack {

	public static List<GuiScreen> guiList = new ArrayList<GuiScreen>();

	public static void push(GuiScreen screen) {
		if (guiList.size() > 0) {
			GuiScreen top = guiList.get(0);
			top.onGuiClosed();
		}
		guiList.add(0, screen);
		Minecraft.getMinecraft().displayGuiScreen(screen);
	}

	public static void pop() {
		if (guiList.size() > 0) {
			GuiScreen top = guiList.get(0);
			top.onGuiClosed();
			guiList.remove(0);
		}

		if (guiList.size() > 0) {
			Minecraft.getMinecraft().displayGuiScreen(guiList.get(0));
		} else {
			Minecraft.getMinecraft().displayGuiScreen(null);
		}
	}
}
