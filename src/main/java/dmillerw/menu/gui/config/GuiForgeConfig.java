package dmillerw.menu.gui.config;

import dmillerw.menu.handler.ConfigHandler;
import dmillerw.menu.reference.Reference;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dmillerw
 */
public class GuiForgeConfig extends GuiConfig {

    private static List<IConfigElement> getElements() {
        List<IConfigElement> list = new ArrayList<IConfigElement>();
        list.addAll((new ConfigElement(ConfigHandler.config.getCategory(ConfigHandler.CATEGORY_VISUAL))).getChildElements());
        list.addAll((new ConfigElement(ConfigHandler.config.getCategory(Configuration.CATEGORY_GENERAL))).getChildElements());
        return list;
    }

    public GuiForgeConfig(GuiScreen parent) {
        super(parent, getElements(), Reference.MOD_ID, false, false, GuiConfig.getAbridgedConfigPath(ConfigHandler.config.toString()));
    }
}