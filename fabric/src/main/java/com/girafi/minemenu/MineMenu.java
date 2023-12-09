package com.girafi.minemenu;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;

public class MineMenu implements ModInitializer {

    @Override
    public void onInitialize() {
        MineMenuCommon.loadCommon(FabricLoader.getInstance().getGameDir().toFile());


        ClientTickEvents.END_CLIENT_TICK.register((mc) -> {

        });
    }
}