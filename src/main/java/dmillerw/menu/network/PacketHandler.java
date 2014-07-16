package dmillerw.menu.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import dmillerw.menu.network.packet.client.PacketSecuritySettings;
import dmillerw.menu.network.packet.server.PacketServerPing;
import dmillerw.menu.network.packet.server.PacketUseItem;

/**
 * @author dmillerw
 */
public class PacketHandler {

	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("MineMenu");

	public static void initialize() {
		INSTANCE.registerMessage(PacketServerPing.class, PacketServerPing.class, 0, Side.SERVER);
		INSTANCE.registerMessage(PacketSecuritySettings.class, PacketSecuritySettings.class, 1, Side.CLIENT);
		INSTANCE.registerMessage(PacketUseItem.class, PacketUseItem.class, 2, Side.SERVER);
	}
}
