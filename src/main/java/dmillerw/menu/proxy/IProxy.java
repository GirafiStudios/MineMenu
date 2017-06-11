package dmillerw.menu.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

interface IProxy {

    void preInit(FMLPreInitializationEvent event);

    void init(FMLInitializationEvent event);

    void postInit(FMLPostInitializationEvent event);
}