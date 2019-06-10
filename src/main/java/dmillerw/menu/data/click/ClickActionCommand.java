package dmillerw.menu.data.click;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

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
        ClientPlayerEntity player = mc.player;
        String parsedCommand = command.replace("@p", player.getName().getString());

        if (clipboard) {
            mc.keyboardListener.setClipboardString(parsedCommand);
            player.sendStatusMessage(new TranslationTextComponent("Command copied to clipboard"), true);
        } else {
            player.sendChatMessage(parsedCommand);
        }
    }

    @Override
    public void onRemoved() {
        clipboard = !clipboard;
    }
}