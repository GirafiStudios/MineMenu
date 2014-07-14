package dmillerw.menu.data.click;

import net.minecraft.client.Minecraft;

/**
 * @author dmillerw
 */
public class CommandClickAction implements IClickAction {

	private final String command;

	public CommandClickAction(String command) {
		this.command = command;
	}

	@Override
	public void onClicked() {
		Minecraft.getMinecraft().thePlayer.sendChatMessage(command);
	}
}
