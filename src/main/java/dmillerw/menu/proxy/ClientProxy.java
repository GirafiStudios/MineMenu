package dmillerw.menu.proxy;

import dmillerw.menu.MineMenu;
import dmillerw.menu.data.json.MenuLoader;
import dmillerw.menu.handler.KeyboardHandler;
import dmillerw.menu.helper.KeyReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class ClientProxy extends CommonProxy implements ISelectiveResourceReloadListener {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        KeyReflectionHelper.gatherFields();
        KeyboardHandler.register();

        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

        if (!MineMenu.menuFile.exists()) {
            MenuLoader.save(MineMenu.menuFile);
        }
        MenuLoader.load(MineMenu.menuFile);
    }

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager manager, @Nonnull Predicate<IResourceType> predicate) {
        MenuLoader.load(MineMenu.menuFile);
    }
}