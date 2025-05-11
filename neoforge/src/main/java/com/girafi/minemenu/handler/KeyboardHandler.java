package com.girafi.minemenu.handler;

import com.girafi.minemenu.Constants;
import com.girafi.minemenu.helper.KeyboardHandlerHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class KeyboardHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        KeyboardHandlerHelper.onClientTick();
    }
}