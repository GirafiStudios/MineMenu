package com.girafi.minemenu.helper;

import com.girafi.minemenu.data.menu.RadialMenu;
import com.girafi.minemenu.gui.RadialMenuScreen;
import com.girafi.minemenu.util.Config;
import com.girafi.minemenu.util.MineMenuKeybinds;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

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
        int keycode = MineMenuKeybinds.RADIAL_MENU_OPEN.key.getValue();
        if (keycode >= 0) {
            boolean radialMenuKeyDown = (MineMenuKeybinds.RADIAL_MENU_OPEN.matchesMouse(keycode) ? GLFW.glfwGetMouseButton(handle, keycode) == 1 : InputConstants.isKeyDown(handle, keycode));
            if (radialMenuKeyDown != lastWheelState) {
                if (Config.GENERAL.toggle.get()) {
                    if (radialMenuKeyDown) {
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
                    if (radialMenuKeyDown != RadialMenuScreen.active) {
                        if (radialMenuKeyDown) {
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
            lastWheelState = radialMenuKeyDown;

            if (ignoreNextTick) {
                ignoreNextTick = false;
                return;
            }
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
        keyMapping.clickCount = setDown ? 1 : 0;
    }
}