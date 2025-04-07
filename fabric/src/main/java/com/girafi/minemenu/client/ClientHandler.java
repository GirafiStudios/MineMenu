package com.girafi.minemenu.client;

import com.girafi.minemenu.MineMenuCommon;
import com.girafi.minemenu.data.menu.RadialMenu;
import com.girafi.minemenu.gui.RadialMenuScreen;
import com.girafi.minemenu.handler.KeyboardHandlerHelper;
import com.girafi.minemenu.util.MineMenuKeybinds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.loader.api.FabricLoader;

public class ClientHandler implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MineMenuCommon.registerPackets();
        MineMenuCommon.loadMenuJson(FabricLoader.getInstance().getGameDir().toFile());
        ServerWorldEvents.LOAD.register(((phase, load) -> MineMenuCommon.setupMenuLoader(load.registryAccess())));
        KeyBindingHelper.registerKeyBinding(MineMenuKeybinds.RADIAL_MENU_OPEN);

        ClientTickEvents.END_CLIENT_TICK.register((mc) -> {
            RadialMenu.tickTimer();

            if ((mc.level == null || mc.isPaused()) && RadialMenuScreen.active) {
                RadialMenuScreen.deactivate();
            }

            KeyboardHandlerHelper.onClientTick();
        });
    }
}