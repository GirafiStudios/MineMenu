package com.girafi.minemenu;

import com.girafi.minemenu.handler.ClientHandler;
import com.girafi.minemenu.util.Config;
import com.girafi.minemenu.util.MineMenuKeybinds;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod(Constants.MOD_ID)
public class MineMenu {
    private final ModContainer modContainer;

    public MineMenu(ModContainer modContainer, IEventBus modBus) {
        this.modContainer = modContainer;
        modBus.addListener(this::setupClient);
        modBus.addListener(this::registerKeybind);

        MineMenuCommon.registerPackets();

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.spec);
    }

    public void setupClient(FMLClientSetupEvent event) {
        ClientHandler.setup(event, this.modContainer);
    }

    public void registerKeybind(RegisterKeyMappingsEvent event) {
        event.register(MineMenuKeybinds.RADIAL_MENU_OPEN);
    }
}
