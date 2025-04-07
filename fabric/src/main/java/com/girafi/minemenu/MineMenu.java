package com.girafi.minemenu;

import com.girafi.minemenu.util.Config;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.neoforged.fml.config.ModConfig;

public class MineMenu implements ModInitializer {

    @Override
    public void onInitialize() {
        MineMenuCommon.registerPackets();
        ConfigRegistry.INSTANCE.register(Constants.MOD_ID, ModConfig.Type.CLIENT, Config.spec);
    }
}