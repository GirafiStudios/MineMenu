package dmillerw.menu;

import dmillerw.menu.data.json.MenuLoader;
import dmillerw.menu.handler.ConfigHandler;
import dmillerw.menu.handler.KeyboardHandler;
import dmillerw.menu.helper.KeyReflectionHelper;
import dmillerw.menu.network.PacketHandler;
import net.minecraftforge.eventbus.api.EventPriority;
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
public class MineMenu /*implements ISelectiveResourceReloadListener*/ {
    public static final String MOD_ID = "minemenu";
    public static final String MOD_NAME = "MineMenu";
    public static File menuFolder = new File(FMLPaths.GAMEDIR.get().toFile(), MOD_ID);
    public static File menuFile = new File(menuFolder, "menu.json");

    public MineMenu() {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::setupCommon);
        modBus.addListener(EventPriority.LOWEST,this::setupClient);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.spec);
    }

    private void setupCommon(FMLCommonSetupEvent event) {
        PacketHandler.initialize();
    }

    private void setupClient(FMLClientSetupEvent event) {
        KeyReflectionHelper.gatherFields();
        KeyboardHandler.register();

        if (!menuFolder.exists()) {
            menuFolder.mkdir();
        }

        if (!menuFile.exists()) {
            MenuLoader.save(menuFile);
        }
        //Load the keybinding later, since it may not being fully registered during this time
        event.enqueueWork(()->{
            MenuLoader.load(menuFile);
        });


        //((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(this);
    }

    /*@Override
    public void onResourceManagerReload(@Nonnull ResourceManager manager, @Nonnull Predicate<IResourceType> predicate) {
        if (menuFile.exists()) {
            MenuLoader.load(menuFile);
        }
    }*/
}