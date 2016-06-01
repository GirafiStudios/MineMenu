package dmillerw.menu.data.click;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.ClientCommandHandler;

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
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        String parsedCommand = command.replace("@p", player.getName());

        if (ClientCommandHandler.instance.executeCommand(player, parsedCommand) == 0) {
            player.sendChatMessage(parsedCommand);
        }
        return false;
    }

    @Override
    public void onRemoved() {

    }
}