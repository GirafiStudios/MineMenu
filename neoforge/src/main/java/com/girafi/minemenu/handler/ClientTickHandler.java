package com.girafi.minemenu.handler;

import com.girafi.minemenu.Constants;
import com.girafi.minemenu.data.menu.RadialMenu;
import com.girafi.minemenu.gui.RadialMenuScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class ClientTickHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        RadialMenu.tickTimer();

        Minecraft mc = Minecraft.getInstance();
        if ((mc.level == null || mc.isPaused()) && RadialMenuScreen.active) {
            RadialMenuScreen.deactivate();
        }
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiLayerEvent.Post event) {
        if (!(event.getName().equals(VanillaGuiLayers.TAB_LIST))) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && !mc.options.hideGui && !mc.isPaused() && RadialMenuScreen.active) {
            GuiGraphics guiGraphics = event.getGuiGraphics();
            ClientTickHelper.renderButtonBackgrounds(guiGraphics);
            ClientTickHelper.renderItems(guiGraphics);
            ClientTickHelper.renderText(guiGraphics);
        }
    }
}