package dmillerw.menu.handler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import dmillerw.menu.data.MenuItem;
import dmillerw.menu.data.RadialMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

/**
 * @author dmillerw
 */
public class MouseHandler {

	public static void register() {
		FMLCommonHandler.instance().bus().register(new MouseHandler());
	}

	public static boolean showMenu = false;

	@SubscribeEvent
	public void onMouseEvent(InputEvent.MouseInputEvent event) {
		Minecraft mc = Minecraft.getMinecraft();

		if (Minecraft.getMinecraft().currentScreen != null || Minecraft.getMinecraft().theWorld == null) {
			return;
		}

		if (Mouse.isButtonDown(0)) {
			if (mc.theWorld != null) {
				if (MouseHandler.showMenu) {
					ScaledResolution resolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

					int mx = Mouse.getX();
					int my = Mouse.getY();
					int roundness = 100;

					double mouse = Math.atan2(my - mc.displayHeight / 2, mx - mc.displayWidth / 2);
					mouse = Math.toDegrees(mouse);
					if (mouse < 0) {
						mouse += 360;
					}

					float rad = (0.82F) * 1 * (257F / (float) resolution.getScaledHeight());

					if (!mc.gameSettings.hideGUI) {
						for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
							double anglePer = 360F / RadialMenu.MAX_ITEMS;
							boolean mouseIn = mouse > anglePer * i && mouse < anglePer * (i + 1);

							if (mouseIn) {
								MenuItem item = RadialMenu.menuItems[i];

								if (item != null && item.clickAction != null) {
									item.clickAction.onClicked();
								}
							}
						}
					}
				}
			}
		}

		if (Mouse.isButtonDown(2)) {
			grabMouse(false);
			MouseHandler.showMenu = true;
		} else {
			grabMouse(true);
			MouseHandler.showMenu = false;
		}
	}

	private void grabMouse(boolean grab) {
		if (grab != Mouse.isGrabbed()) {
			Mouse.setGrabbed(grab);
			Mouse.setCursorPosition(Minecraft.getMinecraft().displayWidth / 2, Minecraft.getMinecraft().displayHeight / 2);
			Minecraft.getMinecraft().inGameHasFocus = grab;
		}
	}
}
