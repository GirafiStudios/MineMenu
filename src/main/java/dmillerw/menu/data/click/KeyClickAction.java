package dmillerw.menu.data.click;

import dmillerw.menu.handler.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

/**
 * @author dmillerw
 */
public class KeyClickAction implements IClickAction {

	public final KeyBinding keyBinding;

	public KeyClickAction(String description) {
		KeyBinding temp = null; // Fucking really? *sigh*
		for (KeyBinding binding : Minecraft.getMinecraft().gameSettings.keyBindings) {
			if (binding.getKeyDescription().equals(description)) {
				temp = binding;
				break;
			}
		}
		this.keyBinding = temp; // Ridiculous
	}

	public KeyClickAction(KeyBinding keyBinding) {
		this.keyBinding = keyBinding;
	}

	@Override
	public void onClicked() {
		KeyboardHandler.INSTANCE.fireKey(keyBinding);
	}
}
