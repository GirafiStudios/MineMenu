package dmillerw.menu;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dmillerw.menu.data.RadialMenu;
import dmillerw.menu.handler.ClientTickHandler;
import dmillerw.menu.handler.KeyboardHandler;
import dmillerw.menu.handler.MouseHandler;

/**
 * @author dmillerw
 */
@Mod(modid = "MineMenu", name = "MineMenu", version = "%MOD_VERSION", dependencies = "required-after:Forge@[%FORGE_VERSION%,)")
public class MineMenu {

	@Mod.Instance("MineMenu")
	public static MineMenu instance;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		KeyboardHandler.register();
		MouseHandler.register();
		ClientTickHandler.register();
		RadialMenu.fillWithDummyData();
	}
}
