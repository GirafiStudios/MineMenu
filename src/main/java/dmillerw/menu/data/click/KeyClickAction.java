package dmillerw.menu.data.click;

import dmillerw.menu.handler.KeyboardHandler;
import net.minecraft.client.settings.KeyBinding;

/**
 * @author dmillerw
 */
public class KeyClickAction implements IClickAction {

	private final KeyBinding keyBinding;

	public KeyClickAction(KeyBinding keyBinding) {
		this.keyBinding = keyBinding;
	}

	@Override
	public void onClicked() {
		KeyboardHandler.INSTANCE.fireKey(keyBinding);
	}
}
