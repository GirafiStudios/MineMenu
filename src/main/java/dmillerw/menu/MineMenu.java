package dmillerw.menu;

import dmillerw.menu.data.json.MenuLoader;
import dmillerw.menu.handler.ConfigHandler;
import dmillerw.menu.proxy.CommonProxy;
import dmillerw.menu.reference.Reference;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nonnull;
import java.io.File;
import java.lang.reflect.Field;
import java.util.function.Predicate;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, acceptedMinecraftVersions = "[1.12,1.13)", dependencies = Reference.DEPENDENCIES, guiFactory = Reference.GUI_FACTORY_CLASS)
public class MineMenu implements ISelectiveResourceReloadListener {

    @Mod.Instance(Reference.MOD_ID)
    public static MineMenu instance;

    @SidedProxy(serverSide = Reference.SERVER_PROXY_ClASS, clientSide = Reference.CLIENT_PROXY_CLASS)
    public static CommonProxy proxy;

    public static File mainFolder;
    public static File menuFile;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File minecraftDir;
        try { //TODO Remove in 1.13
            Field field = Loader.class.getDeclaredField("minecraftDir");
            field.setAccessible(true);
            minecraftDir = (File) field.get(Loader.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to acquire the main Minecraft directory!");
        }
        mainFolder = new File(minecraftDir, Reference.MOD_ID);
        menuFile = new File(MineMenu.mainFolder, "menu.json");
        mainFolder.mkdir();

        ConfigHandler.init(new File(event.getModConfigurationDirectory(), Reference.MOD_NAME + ".cfg"));

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

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager manager, @Nonnull Predicate<IResourceType> predicate) {
        MenuLoader.load(menuFile);
    }
}