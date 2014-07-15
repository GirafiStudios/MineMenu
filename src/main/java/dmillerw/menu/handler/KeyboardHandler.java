package dmillerw.menu.handler;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

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

	public int lastKey;

	private boolean ignoreNextTick = false;
	private boolean ignoreNextWheelTick = false;

	private KeyboardHandler() {

	}

	public void fireKey(KeyBinding binding) {
		fireKey(binding.getKeyCode());
	}

	public void fireKey(int key) {
		lastKey = key;
		KeyBinding.setKeyBindState(key, true);
		KeyBinding.onTick(key);
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

		if (Keyboard.isKeyDown(WHEEL.getKeyCode()) && Minecraft.getMinecraft().currentScreen == null && !MouseHandler.showMenu) {
			if (ignoreNextWheelTick) {
				ignoreNextWheelTick = false;
			} else {
				MouseHandler.showMenu = true;
				Minecraft.getMinecraft().setIngameNotInFocus();
			}
		} else if ((!Keyboard.isKeyDown(WHEEL.getKeyCode()) || Minecraft.getMinecraft().currentScreen != null) && MouseHandler.showMenu) {
			MouseHandler.showMenu = false;
			ignoreNextWheelTick = true;
			if (Minecraft.getMinecraft().currentScreen == null) {
				Minecraft.getMinecraft().setIngameFocus();
			}
		}

		if (lastKey != -1) {
			if (ignoreNextTick) {
				ignoreNextTick = false;
				return;
			}

			KeyBinding.setKeyBindState(lastKey, false);
			lastKey = -1;
		}
	}
}
