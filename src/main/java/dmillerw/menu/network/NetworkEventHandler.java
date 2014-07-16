package dmillerw.menu.network;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import dmillerw.menu.data.SecuritySettings;
import dmillerw.menu.network.packet.server.PacketServerPing;

/**
 * @author dmillerw
 */
public class NetworkEventHandler {

	public static void register() {
		FMLCommonHandler.instance().bus().register(new NetworkEventHandler());
	}

	@SubscribeEvent
	public void onClientConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		if (!event.isLocal) {
			// Disable server specific options, which will be re-enabled if the server replies
			SecuritySettings.allowItemUsage = false;

			PacketHandler.INSTANCE.sendToServer(new PacketServerPing());
		}
	}

	@SubscribeEvent
	public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
		SecuritySettings.restoreDefaults();
	}
}
