package dmillerw.menu.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dmillerw.menu.network.NetworkEventHandler;
import dmillerw.menu.network.PacketHandler;
import net.minecraftforge.common.config.Configuration;

/**
 * @author dmillerw
 */
public class CommonProxy implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        PacketHandler.initialize();
        NetworkEventHandler.register();
    }

    @Override
    public void init(FMLInitializationEvent event) {

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Override
    public void syncConfig(Configuration configuration) {

    }
}
