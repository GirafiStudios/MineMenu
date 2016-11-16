package dmillerw.menu.network.packet.client;

import dmillerw.menu.data.session.ActionSessionData;
import dmillerw.menu.handler.LogHandler;
import dmillerw.menu.network.packet.Packet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

/**
 * @author dmillerw
 */
public class PacketServerResponse extends Packet<PacketServerResponse> {

    @Override
    protected void handleClientSide(EntityPlayer player) {
        LogHandler.info("CLIENT: Received response from server, activating server-side click actions");
        ActionSessionData.activateAll();
    }

    @Override
    protected void handleServerSide(EntityPlayer player) {
    }

    @Override
    protected void toBytes(PacketBuffer buffer) {
        buffer.writeBoolean(true);
    }

    @Override
    protected void fromBytes(PacketBuffer buffer) throws IOException {
    }
}