package com.girafi;

import com.girafi.minemenu.Constants;
import com.girafi.minemenu.MineMenuCommon;
import com.girafi.minemenu.util.Config;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.neoforged.fml.config.ModConfig;

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