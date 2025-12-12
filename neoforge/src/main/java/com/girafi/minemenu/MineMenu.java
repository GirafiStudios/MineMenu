package com.girafi.minemenu;

import com.girafi.minemenu.util.Config;
import com.girafi.minemenu.util.MineMenuKeybinds;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;

@Mod(Constants.MOD_ID)
public class MineMenu {

    public MineMenu(ModContainer modContainer, IEventBus modBus) {
        NeoForge.EVENT_BUS.addListener(this::setupMenuLoader);
        modBus.addListener(this::registerKeybind);

        MineMenuCommon.registerPackets();

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.spec);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
    }

    public void setupMenuLoader(LevelEvent.Load event) {
        MineMenuCommon.loadMenuJson(FMLPaths.GAMEDIR.get().toFile());
        MineMenuCommon.setupMenuLoader(event.getLevel().registryAccess());
    }

    public void registerKeybind(RegisterKeyMappingsEvent event) {
        event.register(MineMenuKeybinds.RADIAL_MENU_OPEN);
    }
}