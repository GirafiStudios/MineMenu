package com.girafi.minemenu;

import com.girafi.minemenu.util.Config;
import com.girafi.minemenu.util.MineMenuKeybinds;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod(Constants.MOD_ID)
public class MineMenu {

    public MineMenu(IEventBus modBus) {
        modBus.addListener(EventPriority.LOWEST, this::loadMenuLoader);
        modBus.addListener(this::registerKeybind);

        MineMenuCommon.registerPackets();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.spec);
    }

    public void loadMenuLoader(final FMLClientSetupEvent event) {
        MineMenuCommon.loadMenuJson(FMLPaths.GAMEDIR.get().toFile());
        MineMenuCommon.setupMenuLoader();
    }

    public void registerKeybind(RegisterKeyMappingsEvent event) {
        event.register(MineMenuKeybinds.WHEEL);
    }
}