package com.girafi.minemenu;

import com.girafi.minemenu.client.KeyHelper;
import com.girafi.minemenu.client.PingHandlerHelper;
import com.girafi.minemenu.client.PingKeybinds;
import com.girafi.minemenu.client.gui.PingSelectGui;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class MineMenu implements ModInitializer {

    @Override
    public void onInitialize() {
        MineMenuCommon.loadCommon();

        //Register keybinds
        KeyBindingHelper.registerKeyBinding(PingKeybinds.KEY_BINDING);
        KeyBindingHelper.registerKeyBinding(PingKeybinds.PING_ALERT);
        KeyBindingHelper.registerKeyBinding(PingKeybinds.PING_MINE);
        KeyBindingHelper.registerKeyBinding(PingKeybinds.PING_LOOK);
        KeyBindingHelper.registerKeyBinding(PingKeybinds.PING_GOTO);

        //TODO Register Shader Instance (Probably not here, but somehow somewhere)

        ClientTickEvents.END_CLIENT_TICK.register((mc) -> {
            PingHandlerHelper.pingTimer();
            KeyHelper.onTick();

            if ((mc.level == null || mc.isPaused()) && PingSelectGui.active) {
                PingSelectGui.deactivate();
            }
        });
    }
}