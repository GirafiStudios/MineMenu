package dmillerw.menu.network;

import dmillerw.menu.network.packet.client.PacketServerResponse;
import dmillerw.menu.network.packet.server.PacketUseItem;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

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