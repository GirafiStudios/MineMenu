package dmillerw.menu.data.click;

import net.minecraft.client.Minecraft;

/**
 * @author dmillerw
 */
public class CommandClickAction implements IClickAction {

    public final String command;

    public CommandClickAction(String command) {
        this.command = command;
    }

    @Override
    public ClickAction getClickAction() {
        return ClickAction.COMMAND;
    }

    @Override
    public boolean onClicked() {
        Minecraft.getMinecraft().thePlayer.sendChatMessage(command);
        return false;
    }
}
