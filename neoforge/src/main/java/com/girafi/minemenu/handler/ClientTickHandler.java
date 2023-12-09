package com.girafi.minemenu.handler;

import com.girafi.minemenu.Constants;
import com.girafi.minemenu.data.menu.RadialMenu;
import com.girafi.minemenu.gui.RadialMenuScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderGuiOverlayEvent;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
import net.neoforged.neoforge.event.TickEvent;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class ClientTickHandler {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            RadialMenu.tickTimer();

            Minecraft mc = Minecraft.getInstance();
            if ((mc.level == null || mc.isPaused()) && RadialMenuScreen.active) {
                RadialMenuScreen.deactivate();
            }
        }
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (!(event.getOverlay().id().equals(VanillaGuiOverlay.PLAYER_LIST.id()))) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && !mc.options.hideGui && !mc.isPaused() && RadialMenuScreen.active) {
            GuiGraphics guiGraphics = event.getGuiGraphics();
            ClientTickHelper.renderButtonBackgrounds();
            ClientTickHelper.renderItems(guiGraphics);
            ClientTickHelper.renderText(guiGraphics);
        }
    }
}