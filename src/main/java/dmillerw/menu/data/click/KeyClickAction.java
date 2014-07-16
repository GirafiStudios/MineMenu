package dmillerw.menu.data.click;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.gameevent.InputEvent;
import dmillerw.menu.data.ClickAction;
import dmillerw.menu.handler.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

/**
 * @author dmillerw
 */
public class KeyClickAction implements IClickAction {

	public final String key;

	public KeyClickAction(String key) {
		this.key = key;
	}

	public KeyBinding getKeyBinding() {
		for (KeyBinding binding : Minecraft.getMinecraft().gameSettings.keyBindings) {
			if (binding.getKeyDescription().equalsIgnoreCase(key)) {
				return binding;
			}
		}
		return null;
	}

	@Override
	public ClickAction getClickAction() {
		return ClickAction.KEYBIND;
	}

	@Override
	public void onClicked() {
		KeyBinding binding = getKeyBinding();
		if (binding != null) {
			KeyboardHandler.INSTANCE.fireKey(binding);
			FMLCommonHandler.instance().bus().post(new InputEvent.KeyInputEvent());
		}
	}
}
