package dmillerw.menu;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import dmillerw.menu.proxy.CommonProxy;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * @author dmillerw
 */
@Mod(modid = "MineMenu", name = "MineMenu", version = "%MOD_VERSION%", dependencies = "required-after:Forge@[%FORGE_VERSION%,)", guiFactory = "dmillerw.menu.gui.config.MineMenuGuiFactory")
public class MineMenu {

	@Mod.Instance("MineMenu")
	public static MineMenu instance;

	@SidedProxy(serverSide = "dmillerw.menu.proxy.CommonProxy", clientSide = "dmillerw.menu.proxy.ClientProxy")
	public static CommonProxy proxy;

	public static Configuration configuration;

	public static File configFolder;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		configFolder = new File(event.getModConfigurationDirectory(), "MineMenu");
		configuration = new Configuration(new File(configFolder, "MineMenu.cfg"));
		configuration.load();

		configuration.setCategoryComment("server", "All these values control security when a client connects to a MineMenu capable server");
		configuration.setCategoryComment("visual", "All values here correspond to the RGBA standard, and must be whole numbers between 0 and 255");

		proxy.syncConfig(configuration);

		FMLCommonHandler.instance().bus().register(MineMenu.instance);

		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.PostConfigChangedEvent event) {
		if (event.modID.equals("MineMenu")) {
			proxy.syncConfig(configuration);
		}
	}
}
