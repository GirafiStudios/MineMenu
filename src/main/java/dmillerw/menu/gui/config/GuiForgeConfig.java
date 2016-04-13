package dmillerw.menu.gui.config;

import dmillerw.menu.MineMenu;
import dmillerw.menu.reference.Reference;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
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
        list.addAll((new ConfigElement(MineMenu.configuration.getCategory("visual"))).getChildElements());
        list.addAll((new ConfigElement(MineMenu.configuration.getCategory("general"))).getChildElements());
        return list;
    }

    public GuiForgeConfig(GuiScreen parent) {
        super(parent, getElements(), Reference.MOD_ID, false, false, GuiConfig.getAbridgedConfigPath(MineMenu.configuration.toString()));
    }
}