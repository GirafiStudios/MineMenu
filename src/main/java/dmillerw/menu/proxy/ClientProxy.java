package dmillerw.menu.proxy;

import dmillerw.menu.MineMenu;
import dmillerw.menu.data.json.MenuLoader;
import dmillerw.menu.handler.ClientTickHandler;
import dmillerw.menu.handler.KeyboardHandler;
import dmillerw.menu.helper.KeyReflectionHelper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

/**
 * @author dmillerw
 */
public class ClientProxy extends CommonProxy {

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

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        KeyReflectionHelper.gatherFields();

        KeyboardHandler.register();
        ClientTickHandler.register();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

        MenuLoader.load(new File(MineMenu.mainFolder, "menu.json"));
    }

    @Override
    public void syncConfig(Configuration configuration) {
        super.syncConfig(configuration);

        Property p_menuAlpha = configuration.get("visual.menu", "alpha", 153);
        Property p_menuRed = configuration.get("visual.menu", "red", 0);
        Property p_menuGreen = configuration.get("visual.menu", "green", 0);
        Property p_menuBlue = configuration.get("visual.menu", "blue", 0);
        Property p_selectAlpha = configuration.get("visual.select", "alpha", 153);
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

        toggle = configuration.get("general", "toggle", false).getBoolean();
        rightClickToEdit = configuration.get("general", "rightClickToEdit", false).getBoolean();
        releaseToSelect = configuration.get("general", "releaseToSelect", false).getBoolean();

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
}
