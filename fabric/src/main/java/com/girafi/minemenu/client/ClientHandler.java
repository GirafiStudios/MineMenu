package com.girafi.minemenu.client;

import com.girafi.minemenu.MineMenuCommon;
import com.girafi.minemenu.data.menu.RadialMenu;
import com.girafi.minemenu.gui.RadialMenuScreen;
import com.girafi.minemenu.helper.KeyboardHandlerHelper;
import com.girafi.minemenu.util.MineMenuKeybinds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.loader.api.FabricLoader;

public class ClientHandler implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MineMenuCommon.registerPackets();
        MineMenuCommon.loadMenuJson(FabricLoader.getInstance().getGameDir().toFile());
        ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register(((phase, load) -> MineMenuCommon.setupMenuLoader(load.registryAccess())));
        KeyMappingHelper.registerKeyMapping(MineMenuKeybinds.RADIAL_MENU_OPEN);

        ClientTickEvents.END_CLIENT_TICK.register((mc) -> {
            RadialMenu.tickTimer();

            if ((mc.level == null || mc.isPaused()) && RadialMenuScreen.active) {
                RadialMenuScreen.deactivate();
            }

            KeyboardHandlerHelper.onClientTick();
        });
    }
}