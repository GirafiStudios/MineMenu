package com.girafi.minemenu;

import com.girafi.minemenu.compat.controllable.ControllableCompat;
import com.girafi.minemenu.data.menu.MenuSession;
import com.girafi.minemenu.gui.RadialMenuScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class MineMenuClient {

    public MineMenuClient(ModContainer modContainer, IEventBus modBus) {
        if (ModList.get().isLoaded("controllable")) {
            modBus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(ControllableCompat::init));
        }
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        NeoForge.EVENT_BUS.addListener(MineMenuClient::hideCrosshair);

        MineMenuCommon.loadMenuJson(FMLPaths.GAMEDIR.get().toFile());
        NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingIn event) -> MenuSession.onJoin());
        NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut event) -> MenuSession.onLeave());
    }

    private static void hideCrosshair(RenderGuiLayerEvent.Pre event) {
        if (RadialMenuScreen.active && event.getName().equals(VanillaGuiLayers.CROSSHAIR)) {
            event.setCanceled(true);
        }
    }
}
