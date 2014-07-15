package dmillerw.menu;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dmillerw.menu.data.json.MenuLoader;
import dmillerw.menu.handler.ClientTickHandler;
import dmillerw.menu.handler.ConfigHandler;
import dmillerw.menu.handler.KeyboardHandler;
import dmillerw.menu.handler.MouseHandler;
import dmillerw.menu.helper.KeyReflectionHelper;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * @author dmillerw
 */
@Mod(modid = "MineMenu", name = "MineMenu", version = "%MOD_VERSION%", dependencies = "required-after:Forge@[%FORGE_VERSION%,)", guiFactory = "dmillerw.menu.gui.config.MineMenuGuiFactory")
public class MineMenu {

	@Mod.Instance("MineMenu")
	public static MineMenu instance;

	public static ConfigHandler configHandler;

	public static File configFolder;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		configFolder = new File(event.getModConfigurationDirectory(), "MineMenu");
		configHandler = new ConfigHandler(new Configuration(new File(configFolder, "MineMenu.cfg")));
		configHandler.syncConfig();

		FMLInterModComms.sendRuntimeMessage(this, "VersionChecker", "addVersionCheck", "https://raw.githubusercontent.com/dmillerw/MineMenu/master/version.json");

		KeyReflectionHelper.gatherFields();

		KeyboardHandler.register();
		MouseHandler.register();
		ClientTickHandler.register();
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		MenuLoader.load();
	}
}
