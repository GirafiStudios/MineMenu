package dmillerw.menu.network.packet.client;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dmillerw.menu.data.SecuritySettings;
import dmillerw.menu.handler.LogHandler;
import io.netty.buffer.ByteBuf;

/**
 * @author dmillerw
 */
public class PacketSecuritySettings implements IMessage, IMessageHandler<PacketSecuritySettings, IMessage> {

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(SecuritySettings.allowCommands);
		buf.writeBoolean(SecuritySettings.allowKeybinds);
		buf.writeBoolean(SecuritySettings.allowItemUsage);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		SecuritySettings.allowCommands = buf.readBoolean();
		SecuritySettings.allowKeybinds = buf.readBoolean();
		SecuritySettings.allowItemUsage = buf.readBoolean();

		LogHandler.info("CLIENT: Received security settings from server - Commands: " + SecuritySettings.allowCommands + " Keybinds: " + SecuritySettings.allowKeybinds + " Item Usage: " + SecuritySettings.allowKeybinds);
	}

	@Override
	public IMessage onMessage(PacketSecuritySettings message, MessageContext ctx) {
		return null;
	}
}
