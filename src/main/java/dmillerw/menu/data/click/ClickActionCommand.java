package dmillerw.menu.data.click;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;

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
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        String parsedCommand = command.replace("@p", player.getCommandSenderName());
        
        //Sends command directly to server if user is on a dedicated server
        //player.sendChatMessage(parsedCommand);
        
        //Acts as if the user typed the command, allowing client commands to work while on a server
        chat(parsedCommand, player);
        return false;
    }

    @Override
    public void onRemoved() {

    }
    
    public void chat(String msg, EntityClientPlayerMP player)
    {
    	//this is the old function chat(msg, player) was derived from
    	/*new GuiChat().func_146403_a(msg);*/
    	
        if (net.minecraftforge.client.ClientCommandHandler.instance.executeCommand(player, msg) == 0)
        	player.sendChatMessage(msg);
    }
}
