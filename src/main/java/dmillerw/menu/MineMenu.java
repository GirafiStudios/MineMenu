package dmillerw.menu;

import dmillerw.menu.data.json.MenuLoader;
import dmillerw.menu.handler.ConfigHandler;
import dmillerw.menu.network.PacketHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;

@Mod(value = MineMenu.MOD_ID)
public class MineMenu {
    public static final String MOD_ID = "minemenu";
    public static final String MOD_NAME = "MineMenu";
    public static File menuFolder = new File(FMLPaths.GAMEDIR.get().toFile(), MOD_ID);
    public static File menuFile = new File(menuFolder, "menu.json");

    public MineMenu() {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::setupCommon);
        modBus.addListener(this::setupClient);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.spec);
    }

    private void setupCommon(FMLCommonSetupEvent event) {
        PacketHandler.initialize();
    }

    private void setupClient(FMLClientSetupEvent event) {
        if (!menuFolder.exists()) {
            menuFolder.mkdir();
        }

        if (!menuFile.exists()) {
            MenuLoader.save(menuFile);
        }
        MenuLoader.load(menuFile);
    }
}