package dmillerw.menu.data.click;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.ClientCommandHandler;

public class ClickActionCommand implements ClickAction.IClickAction {
    public final String command;
    public boolean clipboard;

    public ClickActionCommand(String command, boolean clipboard) {
        this.command = command;
        this.clipboard = clipboard;
    }

    @Override
    public ClickAction getClickAction() {
        return ClickAction.COMMAND;
    }

    @Override
    public boolean deactivates() {
	return false;
    }
    
    @Override
    public void onClicked() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        String parsedCommand = command.replace("@p", player.getName());

        if (ClientCommandHandler.instance.executeCommand(player, parsedCommand) == 0) {
            if (clipboard) {
                GuiConfirmOpenLink.setClipboardString(parsedCommand);
                player.sendStatusMessage(new TextComponentString("Command copied to clipboard"), true);
            } else {
                player.sendChatMessage(parsedCommand);
            }
        }
    }

    @Override
    public void onRemoved() {
        clipboard = !clipboard;
    }
}