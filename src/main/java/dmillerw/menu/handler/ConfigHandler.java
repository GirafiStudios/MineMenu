package dmillerw.menu.handler;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * @author dmillerw
 */
public class ConfigHandler {

	public static int menuAlpha;
	public static int menuRed;
	public static int menuGreen;
	public static int menuBlue;
	public static int selectAlpha;
	public static int selectRed;
	public static int selectGreen;
	public static int selectBlue;

	public final Configuration configuration;

	public ConfigHandler(Configuration configuration) {
		this.configuration = configuration;
		this.configuration.load();

		this.configuration.setCategoryComment("visual", "All values here correspond to the RGBA standard, and must be whole numbers between 0 and 255");
		this.configuration.setCategoryLanguageKey("visual.menu", "config.visual.menu");
		this.configuration.setCategoryLanguageKey("visual.select", "config.visual.select");

		FMLCommonHandler.instance().bus().register(this);
	}

	public void syncConfig() {
		Property p_menuAlpha = configuration.get("visual.menu", "alpha", 153);
		Property p_menuRed = configuration.get("visual.menu", "red", 0);
		Property p_menuGreen = configuration.get("visual.menu", "green", 0);
		Property p_menuBlue = configuration.get("visual.menu", "blue", 0);
		Property p_selectAlpha = configuration.get("visual.menu", "alpha", 153);
		Property p_selectRed = configuration.get("visual.select", "red", 255);
		Property p_selectGreen = configuration.get("visual.select", "green", 0);
		Property p_selectBlue = configuration.get("visual.select", "blue", 0);

		menuAlpha = verify(p_menuAlpha);
		menuRed = verify(p_menuRed);
		menuGreen = verify(p_menuGreen);
		menuBlue = verify(p_menuBlue);
		selectAlpha = verify(p_selectAlpha);
		selectRed = verify(p_selectRed);
		selectGreen = verify(p_selectGreen);
		selectBlue = verify(p_selectBlue);

		if (configuration.hasChanged()) {
			configuration.save();
		}
	}

	private int verify(Property property) {
		int value = property.getInt();
		if (value < 0) {
			value = 0;
			property.set(0);
		} else if (value > 255) {
			value = 255;
			property.set(255);
		}
		return value;
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.PostConfigChangedEvent event) {
		if (event.modID.equals("MineMenu")) {
			syncConfig();
		}
	}
}
