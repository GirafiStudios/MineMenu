package dmillerw.menu.data.click;

import net.minecraft.client.Minecraft;

/**
 * @author dmillerw
 */
public class ClickActionCommand implements ClickAction.IClickAction {

    public final String command;

    public ClickActionCommand(String command) {
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
