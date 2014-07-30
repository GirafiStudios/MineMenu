package dmillerw.menu.network.packet.client;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dmillerw.menu.data.session.ActionSessionData;
import dmillerw.menu.handler.LogHandler;
import io.netty.buffer.ByteBuf;

/**
 * @author dmillerw
 */
public class PacketServerResponse implements IMessage, IMessageHandler<PacketServerResponse, IMessage> {

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(true);
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public IMessage onMessage(PacketServerResponse message, MessageContext ctx) {
        LogHandler.info("CLIENT: Received response from server, activating server-side click actions");
        ActionSessionData.activateAll();
        return null;
    }
}
