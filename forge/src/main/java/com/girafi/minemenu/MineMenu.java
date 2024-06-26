package com.girafi.minemenu;

import com.girafi.minemenu.util.Config;
import com.girafi.minemenu.util.MineMenuKeybinds;
import fuzs.forgeconfigapiport.forge.api.neoforge.v4.NeoForgeConfigRegistry;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(value = Constants.MOD_ID)
public class MineMenu {

    public MineMenu() {
        final IEventBus modBus  = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(EventPriority.LOWEST, this::loadMenuLoader);
        modBus.addListener(this::registerKeybind);

        MineMenuCommon.registerPackets();

        NeoForgeConfigRegistry.INSTANCE.register(ModConfig.Type.COMMON, Config.spec);
    }

    public void loadMenuLoader(final FMLClientSetupEvent event) {
        MineMenuCommon.loadMenuJson(FMLPaths.GAMEDIR.get().toFile());
        MineMenuCommon.setupMenuLoader();
    }

    public void registerKeybind(RegisterKeyMappingsEvent event) {
        event.register(MineMenuKeybinds.RADIAL_MENU_OPEN);
    }
}