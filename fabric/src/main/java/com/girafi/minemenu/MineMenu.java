package com.girafi.minemenu;

import com.girafi.minemenu.data.menu.RadialMenu;
import com.girafi.minemenu.gui.RadialMenuScreen;
import com.girafi.minemenu.handler.ClientTickHelper;
import com.girafi.minemenu.handler.KeyboardHandlerHelper;
import com.girafi.minemenu.util.Config;
import fuzs.forgeconfigapiport.api.config.v3.ForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.fml.config.ModConfig;

public class MineMenu implements ModInitializer {

    @Override
    public void onInitialize() {
        MineMenuCommon.loadCommon(FabricLoader.getInstance().getGameDir().toFile());
        MineMenuCommon.setupMenuLoader();
        KeyBindingHelper.registerKeyBinding(MineMenuCommon.WHEEL);
        ForgeConfigRegistry.INSTANCE.register(Constants.MOD_ID, ModConfig.Type.COMMON, Config.spec);

        ClientTickEvents.END_CLIENT_TICK.register((mc) -> {
                RadialMenu.tickTimer();

                if ((mc.level == null || mc.isPaused()) && RadialMenuScreen.active) {
                    RadialMenuScreen.deactivate();
                }

            KeyboardHandlerHelper.onClientTick();
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