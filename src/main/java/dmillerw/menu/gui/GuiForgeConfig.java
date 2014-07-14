package dmillerw.menu.gui;

import cpw.mods.fml.client.config.GuiConfig;
import dmillerw.menu.MineMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

/**
 * @author dmillerw
 */
public class GuiForgeConfig extends GuiConfig {

	public GuiForgeConfig(GuiScreen parent) {
		super(
			parent,
			(new ConfigElement(MineMenu.configHandler.configuration.getCategory("visual"))).getChildElements(),
			"MineMenu",
			false,
			false,
			GuiConfig.getAbridgedConfigPath(MineMenu.configHandler.configuration.toString())
		);
	}
}
