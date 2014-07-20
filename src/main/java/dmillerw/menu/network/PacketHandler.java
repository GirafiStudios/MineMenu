package dmillerw.menu.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import dmillerw.menu.network.client.PacketServerResponse;
import dmillerw.menu.network.packet.server.PacketUseItem;

/**
 * @author dmillerw
 */
public class PacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("MineMenu");

    public static void initialize() {
        INSTANCE.registerMessage(PacketServerResponse.class, PacketServerResponse.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(PacketUseItem.class, PacketUseItem.class, 1, Side.SERVER);
    }
}
