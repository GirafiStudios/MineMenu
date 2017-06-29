package dmillerw.menu.proxy;

import dmillerw.menu.MineMenu;
import dmillerw.menu.data.json.MenuLoader;
import dmillerw.menu.handler.KeyboardHandler;
import dmillerw.menu.helper.KeyReflectionHelper;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        KeyReflectionHelper.gatherFields();
        KeyboardHandler.register();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

        if (!MineMenu.menuFile.exists()) {
            MenuLoader.save(MineMenu.menuFile);
        }
        MenuLoader.load(MineMenu.menuFile);
    }
}