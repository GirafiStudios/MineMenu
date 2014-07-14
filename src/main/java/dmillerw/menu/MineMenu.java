package dmillerw.menu;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dmillerw.menu.data.RadialMenu;
import dmillerw.menu.handler.ClientTickHandler;
import dmillerw.menu.handler.ConfigHandler;
import dmillerw.menu.handler.KeyboardHandler;
import dmillerw.menu.handler.MouseHandler;
import net.minecraftforge.common.config.Configuration;

/**
 * @author dmillerw
 */
@Mod(modid = "MineMenu", name = "MineMenu", version = "%MOD_VERSION", dependencies = "required-after:Forge@[%FORGE_VERSION%,)", guiFactory = "dmillerw.menu.gui.MineMenuGuiFactory")
public class MineMenu {

	@Mod.Instance("MineMenu")
	public static MineMenu instance;

	public static ConfigHandler configHandler;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		configHandler = new ConfigHandler(new Configuration(event.getSuggestedConfigurationFile()));
		configHandler.syncConfig();

		KeyboardHandler.register();
		MouseHandler.register();
		ClientTickHandler.register();
		RadialMenu.fillWithDummyData();
	}
}
