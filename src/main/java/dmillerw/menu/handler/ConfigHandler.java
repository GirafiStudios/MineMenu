package dmillerw.menu.handler;

import dmillerw.menu.reference.Reference;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

public class ConfigHandler {
    public static Configuration config;
    public static final String CATEGORY_SERVER = "server";
    public static final String CATEGORY_VISUAL = "visual";
    public static int menuAlpha;
    public static int menuRed;
    public static int menuGreen;
    public static int menuBlue;
    public static int selectAlpha;
    public static int selectRed;
    public static int selectGreen;
    public static int selectBlue;
    public static boolean toggle;
    public static boolean rightClickToEdit;
    public static boolean releaseToSelect;
    public static boolean removeStoneOnMenuButton;

    public static void init(File configFile) {
        if (config == null) {
            config = new Configuration(configFile);
            loadConfig();
        }
    }

    private static void loadConfig() {
        config.setCategoryComment(CATEGORY_SERVER, "All these values control security when a client connects to a MineMenu capable server");
        config.setCategoryComment(CATEGORY_VISUAL, "All values here correspond to the RGBA standard, and must be whole numbers between 0 and 255");

        Property p_menuAlpha = config.get("visual.menu", "alpha", 153);
        Property p_menuRed = config.get("visual.menu", "red", 0);
        Property p_menuGreen = config.get("visual.menu", "green", 0);
        Property p_menuBlue = config.get("visual.menu", "blue", 0);
        Property p_selectAlpha = config.get("visual.select", "alpha", 153);
        Property p_selectRed = config.get("visual.select", "red", 255);
        Property p_selectGreen = config.get("visual.select", "green", 0);
        Property p_selectBlue = config.get("visual.select", "blue", 0);

        menuAlpha = verify(p_menuAlpha);
        menuRed = verify(p_menuRed);
        menuGreen = verify(p_menuGreen);
        menuBlue = verify(p_menuBlue);
        selectAlpha = verify(p_selectAlpha);
        selectRed = verify(p_selectRed);
        selectGreen = verify(p_selectGreen);
        selectBlue = verify(p_selectBlue);

        toggle = config.get(Configuration.CATEGORY_GENERAL, "toggle", false).getBoolean();
        rightClickToEdit = config.get(Configuration.CATEGORY_GENERAL, "rightClickToEdit", false).getBoolean();
        releaseToSelect = config.get(Configuration.CATEGORY_GENERAL, "releaseToSelect", false).getBoolean();
        removeStoneOnMenuButton = config.get(Configuration.CATEGORY_GENERAL, "removeStoneOnMenuButton", false).getBoolean();

        if (config.hasChanged()) {
            config.save();
        }
    }

    private static int verify(Property property) {
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
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equalsIgnoreCase(Reference.MOD_ID)) {
            loadConfig();
        }
    }
}