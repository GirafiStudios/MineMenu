package dmillerw.menu.data.click;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

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
    public void onClicked() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player != null) {
            String parsedCommand = command.replace("@p", player.getName().getString());

            if (clipboard) {
                mc.keyboardHandler.setClipboard(parsedCommand);
                player.displayClientMessage(Component.translatable("mine_menu.clipboardCopy"), true);
            } else {
                if (parsedCommand.startsWith("//")) { //Special Case for World Edit //commands
                    parsedCommand = parsedCommand.replace("//", "/");
                } else {
                    parsedCommand = parsedCommand.replaceAll("^/+", "");
                }
                player.connection.sendUnsignedCommand(parsedCommand);}
        }
    }

    @Override
    public void onRemoved() {
        clipboard = !clipboard;
    }
}