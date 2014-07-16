package dmillerw.menu.gui.config;

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
				(new ConfigElement(MineMenu.configuration.getCategory("visual"))).getChildElements(),
				"MineMenu",
				false,
				false,
				GuiConfig.getAbridgedConfigPath(MineMenu.configuration.toString())
		);
	}
}
