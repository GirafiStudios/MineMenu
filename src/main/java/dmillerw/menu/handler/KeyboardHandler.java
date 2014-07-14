package dmillerw.menu.handler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.settings.KeyBinding;

/**
 * @author dmillerw
 */
public class KeyboardHandler {

	public static final KeyboardHandler INSTANCE = new KeyboardHandler();

	public static void register() {
		FMLCommonHandler.instance().bus().register(KeyboardHandler.INSTANCE);
	}

	public int lastKey;

	private boolean ignoreNextTick = false;

	private KeyboardHandler() {

	}

	public void fireKey(KeyBinding binding) {
		fireKey(binding.getKeyCode());
	}

	public void fireKey(int key) {
		lastKey = key;
		KeyBinding.setKeyBindState(key, true);
		KeyBinding.onTick(key);
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
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
