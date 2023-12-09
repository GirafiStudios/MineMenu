package com.girafi.minemenu;

import com.girafi.minemenu.util.Config;
import net.neoforged.api.distmarker.Dist;
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

    public MineMenu() {
        final IEventBus modBus  = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(EventPriority.LOWEST, this::loadMenuLoader);
        modBus.addListener(this::registerKeybind);

        MineMenuCommon.loadCommon(FMLPaths.GAMEDIR.get().toFile());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.spec);
    }

    public void loadMenuLoader(final FMLClientSetupEvent event) {
        MineMenuCommon.setupMenuLoader();
    }

    public void registerKeybind(RegisterKeyMappingsEvent event) {
        event.register(MineMenuCommon.WHEEL);
    }
}