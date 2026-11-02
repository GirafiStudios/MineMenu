package com.girafi.minemenu.helper;

import com.girafi.minemenu.data.menu.RadialMenu;
import com.girafi.minemenu.gui.RadialMenuScreen;
import com.girafi.minemenu.util.Config;
import com.girafi.minemenu.util.MineMenuKeybinds;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
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

        Window window = Minecraft.getInstance().getWindow();
        KeyMapping key = MineMenuKeybinds.RADIAL_MENU_OPEN;
        int keycode = key.key.getValue();
        boolean radialMenuKeyDown = RadialMenuInput.isOpenHeld();
        if (keycode >= 0 && !radialMenuKeyDown) {
            radialMenuKeyDown = key.key.getType() == InputConstants.Type.MOUSE ? GLFW.glfwGetMouseButton(window.handle(), keycode) == GLFW.GLFW_PRESS : KeyEmulator.readPhysical(() -> InputConstants.isKeyDown(window, keycode));
        }
        if (radialMenuKeyDown != lastWheelState) {
            if (Config.GENERAL.toggle.get()) {
                if (radialMenuKeyDown) {
                    if (RadialMenuScreen.active) {
                        if (Config.GENERAL.releaseToSelect.get()) {
                            RadialMenuScreen.INSTANCE.mouseClicked(new MouseButtonEvent(mc.mouseHandler.xpos(), mc.mouseHandler.ypos(), new MouseButtonInfo(GLFW.GLFW_MOUSE_BUTTON_LEFT, 0)), false);
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
                            RadialMenuScreen.INSTANCE.mouseClicked(new MouseButtonEvent(mc.mouseHandler.xpos(), mc.mouseHandler.ypos(), new MouseButtonInfo(GLFW.GLFW_MOUSE_BUTTON_LEFT, 0)), false);
                        }
                        RadialMenuScreen.deactivate();
                    }
                }
            }
        }
        lastWheelState = radialMenuKeyDown;
    }

    public static boolean hasShiftDown() {
        Window window = Minecraft.getInstance().getWindow();
        return InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_SHIFT)
                || InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_SHIFT);
    }
}
