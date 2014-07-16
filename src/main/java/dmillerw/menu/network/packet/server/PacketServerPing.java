package dmillerw.menu.network.packet.server;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dmillerw.menu.handler.LogHandler;
import dmillerw.menu.network.PacketHandler;
import dmillerw.menu.network.packet.client.PacketSecuritySettings;
import io.netty.buffer.ByteBuf;

/**
 * Sent to the server upon client login, to check for server availability and security settings
 * @author dmillerw
 */
public class PacketServerPing implements IMessage, IMessageHandler<PacketServerPing, IMessage> {

	@Override
	public void fromBytes(ByteBuf buf) {
		buf.writeByte(0);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.readByte();
	}

	@Override
	public IMessage onMessage(PacketServerPing message, MessageContext ctx) {
		// If the server has the mod installed, this will be run, otherwise there won't be a response
		LogHandler.info("SERVER: Received ping from client, sending security settings");
		PacketHandler.INSTANCE.sendTo(new PacketSecuritySettings(), ctx.getServerHandler().playerEntity);
		return null;
	}
}
