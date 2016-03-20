package dmillerw.menu;

import dmillerw.menu.handler.LogHandler;
import dmillerw.menu.proxy.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * @author dmillerw
 */
@Mod(modid = "MineMenu", name = "MineMenu", version = "%MOD_VERSION%", dependencies = "required-after:Forge@[12.16.0,)", guiFactory = "dmillerw.menu.gui.config.MineMenuGuiFactory")
public class MineMenu {

    @Mod.Instance("MineMenu")
    public static MineMenu instance;

    @SidedProxy(serverSide = "dmillerw.menu.proxy.CommonProxy", clientSide = "dmillerw.menu.proxy.ClientProxy")
    public static CommonProxy proxy;

    public static Configuration configuration;

    public static File mainFolder;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File minecraftDir;
        try {
            Field field = Loader.class.getDeclaredField("minecraftDir");
            field.setAccessible(true);
            minecraftDir = (File) field.get(Loader.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to acquire the main Minecraft directory!");
        }

        mainFolder = new File(minecraftDir, "MineMenu");
        mainFolder.mkdir();

        File oldFile = new File(event.getModConfigurationDirectory(), "MineMenu/menu.json");
        File newFile = new File(mainFolder, "menu.json");

        if (oldFile.exists() && !newFile.exists()) {
            LogHandler.info("Found old menu.json file. Transitioning to new location!");
            try {
                IOUtils.copy(new FileInputStream(oldFile), new FileOutputStream(newFile));
            } catch (IOException ex) {
                LogHandler.warn("Failed to copy old memu.json to new location! Reason: " + ex.getLocalizedMessage());
            } finally {
                oldFile.delete();
            }
        }

        configuration = new Configuration(new File(event.getModConfigurationDirectory(), "MineMenu/MineMenu.cfg"));
        configuration.load();

        configuration.setCategoryComment("server", "All these values control security when a client connects to a MineMenu capable server");
        configuration.setCategoryComment("visual", "All values here correspond to the RGBA standard, and must be whole numbers between 0 and 255");

        proxy.syncConfig(configuration);

        MinecraftForge.EVENT_BUS.register(MineMenu.instance);

        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.PostConfigChangedEvent event) {
        if (event.modID.equals("MineMenu")) {
            proxy.syncConfig(configuration);
        }
    }
}
