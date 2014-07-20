package dmillerw.menu.handler;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import dmillerw.menu.helper.KeyReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * @author dmillerw
 */
public class KeyboardHandler {

	public static final KeyboardHandler INSTANCE = new KeyboardHandler();

	public static final KeyBinding WHEEL = new KeyBinding("key.open_menu", Keyboard.KEY_R, "key.categories.gameplay");

	public static void register() {
		FMLCommonHandler.instance().bus().register(KeyboardHandler.INSTANCE);
		ClientRegistry.registerKeyBinding(WHEEL);
	}

	public KeyBinding lastKey;

	private boolean ignoreNextTick = false;
	private boolean ignoreNextWheelTick = false;

	private KeyboardHandler() {

	}

	public void fireKey(KeyBinding key) {
		lastKey = key;
		KeyReflectionHelper.pressKey(key);
		KeyReflectionHelper.increasePressTime(key);
		ignoreNextTick = true;
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			return;
		}

		if (Minecraft.getMinecraft().theWorld == null) {
			return;
		}

        boolean wheelKeyPressed = (WHEEL.getKeyCode() >= 0 ? Keyboard.isKeyDown(WHEEL.getKeyCode()) : Mouse.isButtonDown(WHEEL.getKeyCode() + 100));
        boolean showMenu = wheelKeyPressed && Minecraft.getMinecraft().currentScreen == null;

        if (showMenu != MouseHandler.showMenu) {
            MouseHandler.showMenu = showMenu;
            if (showMenu) {
                Minecraft.getMinecraft().setIngameNotInFocus();
            } else {
                if (Minecraft.getMinecraft().currentScreen == null) {
                    Minecraft.getMinecraft().setIngameFocus();
                }
            }
        }

		if (lastKey != null) {
			if (ignoreNextTick) {
				ignoreNextTick = false;
				return;
			}

			KeyReflectionHelper.unpressKey(lastKey);

			lastKey = null;
		}
	}
}
