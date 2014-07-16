package dmillerw.menu.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dmillerw.menu.data.SecuritySettings;
import dmillerw.menu.network.PacketHandler;
import net.minecraftforge.common.config.Configuration;

/**
 * @author dmillerw
 */
public class CommonProxy implements IProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		PacketHandler.initialize();
	}

	@Override
	public void init(FMLInitializationEvent event) {

	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {

	}

	@Override
	public void syncConfig(Configuration configuration) {
		SecuritySettings.allowCommands = configuration.get("server.security", "allowCommands", true).getBoolean();
		SecuritySettings.allowKeybinds = configuration.get("server.security", "allowKeybinds", true).getBoolean();
		SecuritySettings.allowItemUsage = configuration.get("server.security", "allowItemUsage", true).getBoolean();

		if (configuration.hasChanged()) {
			configuration.save();
		}
	}
}
