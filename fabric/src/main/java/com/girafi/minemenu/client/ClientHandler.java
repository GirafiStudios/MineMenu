package com.girafi.minemenu.client;

import com.girafi.minemenu.MineMenuCommon;
import com.girafi.minemenu.compat.controllable.ControllableCompat;
import com.girafi.minemenu.data.menu.MenuSession;
import com.girafi.minemenu.data.menu.RadialMenu;
import com.girafi.minemenu.gui.RadialMenuScreen;
import com.girafi.minemenu.handler.ClientTickHelper;
import com.girafi.minemenu.handler.KeyboardHandlerHelper;
import com.girafi.minemenu.handler.RadialMenuInput;
import com.girafi.minemenu.util.MineMenuKeybinds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

public class ClientHandler implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MineMenuCommon.registerPackets();
        MineMenuCommon.loadMenuJson(FabricLoader.getInstance().getGameDir().toFile());
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> MenuSession.onJoin(handler.registryAccess()));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> client.execute(MenuSession::onLeave));
        KeyBindingHelper.registerKeyBinding(MineMenuKeybinds.RADIAL_MENU_OPEN);
        if (FabricLoader.getInstance().isModLoaded("controllable")) {
            ControllableCompat.init();
        }

        ClientTickEvents.END_CLIENT_TICK.register((mc) -> {
            RadialMenu.tickTimer();

            if ((mc.level == null || mc.isPaused()) && RadialMenuScreen.active) {
                RadialMenuScreen.deactivate();
            }

            KeyboardHandlerHelper.onClientTick();
            RadialMenuInput.tick();
        });

        HudRenderCallback.EVENT.register((guiGraphics, delta) -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null && !mc.options.hideGui && !mc.isPaused() && RadialMenuScreen.active) {
                ClientTickHelper.renderButtonBackgrounds();
                ClientTickHelper.renderItems(guiGraphics);
                ClientTickHelper.renderText(guiGraphics);
            }
        });
    }
}