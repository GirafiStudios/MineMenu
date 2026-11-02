package com.girafi.minemenu.handler;

import com.girafi.minemenu.data.menu.RadialMenu;
import com.girafi.minemenu.gui.RadialMenuScreen;
import com.girafi.minemenu.util.Config;
import com.girafi.minemenu.util.MineMenuKeybinds;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;


public class KeyboardHandlerHelper {
    public static final KeyboardHandlerHelper INSTANCE = new KeyboardHandlerHelper();
    private static boolean lastWheelState = false;

    private KeyboardHandlerHelper() {
    }

    public static void onClientTick() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null) {
            return;
        }
        KeyEmulator.tick();

        long handle = Minecraft.getInstance().getWindow().getWindow();
        int keycode = MineMenuKeybinds.RADIAL_MENU_OPEN.key.getValue();
        boolean radialMenuKeyDown = RadialMenuInput.isOpenHeld();
        if (keycode >= 0 && !radialMenuKeyDown) {
            radialMenuKeyDown = (MineMenuKeybinds.RADIAL_MENU_OPEN.matchesMouse(keycode) ? GLFW.glfwGetMouseButton(handle, keycode) == 1 : KeyEmulator.readPhysical(() -> InputConstants.isKeyDown(handle, keycode)));
        }
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
    }
}
