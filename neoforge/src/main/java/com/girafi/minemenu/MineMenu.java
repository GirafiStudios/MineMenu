package com.girafi.minemenu;

import com.girafi.minemenu.util.Config;
import com.girafi.minemenu.util.MineMenuKeybinds;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(Constants.MOD_ID)
public class MineMenu {

    public MineMenu(ModContainer modContainer, IEventBus modBus) {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::loadMenuLoader);
        modBus.addListener(this::registerKeybind);

        MineMenuCommon.registerPackets();

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.spec);
    }

    public void loadMenuLoader(ServerStartingEvent event) {
        MineMenuCommon.loadMenuJson(FMLPaths.GAMEDIR.get().toFile());
        MineMenuCommon.setupMenuLoader(event.getServer().registryAccess());
    }

    public void registerKeybind(RegisterKeyMappingsEvent event) {
        event.register(MineMenuKeybinds.RADIAL_MENU_OPEN);
    }
}