package com.girafi.minemenu.handler;

import com.girafi.minemenu.MineMenuCommon;
import com.girafi.minemenu.data.menu.RadialMenu;
import com.girafi.minemenu.gui.RadialMenuScreen;
import com.girafi.minemenu.helper.KeyReflectionHelper;
import com.girafi.minemenu.util.Config;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KeyboardHandlerHelper {
    public static final KeyboardHandlerHelper INSTANCE = new KeyboardHandlerHelper();
    private static boolean lastWheelState = false;
    private static final List<KeyMapping> FIRED_KEYS = new ArrayList<>();
    private static boolean ignoreNextTick = false;

    private KeyboardHandlerHelper() {
    }

    public void fireKey(KeyMapping keyMapping) {
        FIRED_KEYS.add(keyMapping);
        activateKeybind(keyMapping, true);
        ignoreNextTick = true;
    }

    public void toggleKey(KeyMapping keyMapping) {
        activateKeybind(keyMapping, true);
        ignoreNextTick = true;
    }

    public static void onClientTick() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null) {
            return;
        }

        long handle = Minecraft.getInstance().getWindow().getWindow();
        boolean wheelKeyPressed = MineMenuCommon.WHEEL.key.getValue() >= 0 ? InputConstants.isKeyDown(handle, MineMenuCommon.WHEEL.key.getValue()) : InputConstants.isKeyDown(handle, MineMenuCommon.WHEEL.key.getValue() + 100);

        if (wheelKeyPressed != lastWheelState) {
            if (Config.GENERAL.toggle.get()) {
                if (wheelKeyPressed) {
                    if (RadialMenuScreen.active) {
                        if (Config.GENERAL.releaseToSelect.get()) {
                            RadialMenuScreen.INSTANCE.mouseClicked(mc.mouseHandler.xpos(), mc.mouseHandler.ypos(), 0);
                        }
                        RadialMenuScreen.deactivate();
                    } else {
                        if (mc.screen == null || mc.screen instanceof RadialMenuScreen) {
                            RadialMenu.resetCategory();
                            RadialMenu.resetTimer();
                            RadialMenuScreen.activate();
                        }
                    }
                }
            } else {
                if (wheelKeyPressed != RadialMenuScreen.active) {
                    if (wheelKeyPressed) {
                        if (mc.screen == null || mc.screen instanceof RadialMenuScreen) {
                            RadialMenu.resetCategory();
                            RadialMenu.resetTimer();
                            RadialMenuScreen.activate();
                        }
                    } else {
                        if (Config.GENERAL.releaseToSelect.get()) {
                            RadialMenuScreen.INSTANCE.mouseClicked(mc.mouseHandler.xpos(), mc.mouseHandler.ypos(), 0);
                        }
                        RadialMenuScreen.deactivate();
                    }
                }
            }
        }
        lastWheelState = wheelKeyPressed;

        if (ignoreNextTick) {
            ignoreNextTick = false;
            return;
        }

        Iterator<KeyMapping> iterator = FIRED_KEYS.iterator();
        while (iterator.hasNext()) {
            KeyMapping keyBinding = iterator.next();
            activateKeybind(keyBinding, false);
            iterator.remove();
        }
    }

    public static void activateKeybind(KeyMapping keyMapping, boolean setDown) {
        //if (keyMapping.getCategory().equals("key.categories.movement"))
        keyMapping.setDown(setDown);
        KeyReflectionHelper.setClickCount(keyMapping, setDown ? 1 : 0);
    }
}