package com.girafi.minemenu.helper;

import com.girafi.minemenu.data.menu.RadialMenu;
import com.girafi.minemenu.gui.RadialMenuScreen;
import com.girafi.minemenu.util.Config;
import com.girafi.minemenu.util.MineMenuKeybinds;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import org.lwjgl.sdl.SDLMouse;


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

        KeyMapping key = MineMenuKeybinds.RADIAL_MENU_OPEN;
        int keycode = key.key.getValue();
        boolean radialMenuKeyDown = RadialMenuInput.isOpenHeld();
        if (!key.isUnbound() && !radialMenuKeyDown) {
            radialMenuKeyDown = key.key.getType() == InputConstants.Type.MOUSE ? isMouseButtonDown(keycode) : KeyEmulator.readPhysical(() -> InputConstants.isKeyDown(keycode));
        }
        if (radialMenuKeyDown != lastWheelState) {
            if (Config.GENERAL.toggle.get()) {
                if (radialMenuKeyDown) {
                    if (RadialMenuScreen.active) {
                        if (Config.GENERAL.releaseToSelect.get()) {
                            RadialMenuScreen.INSTANCE.mouseClicked(new MouseButtonEvent(mc.mouseHandler.xpos(), mc.mouseHandler.ypos(), new MouseButtonInfo(InputConstants.MOUSE_BUTTON_LEFT, 0)), false);
                        }
                        RadialMenuScreen.deactivate();
                    } else {
                        if (mc.gui.screen() == null || mc.gui.screen() instanceof RadialMenuScreen) {
                            RadialMenu.resetCategory();
                            RadialMenu.resetTimer();
                            RadialMenuScreen.activate();
                        }
                    }
                }
            } else {
                if (radialMenuKeyDown != RadialMenuScreen.active) {
                    if (radialMenuKeyDown) {
                        if (mc.gui.screen() == null || mc.gui.screen() instanceof RadialMenuScreen) {
                            RadialMenu.resetCategory();
                            RadialMenu.resetTimer();
                            RadialMenuScreen.activate();
                        }
                    } else {
                        if (Config.GENERAL.releaseToSelect.get()) {
                            RadialMenuScreen.INSTANCE.mouseClicked(new MouseButtonEvent(mc.mouseHandler.xpos(), mc.mouseHandler.ypos(), new MouseButtonInfo(InputConstants.MOUSE_BUTTON_LEFT, 0)), false);
                        }
                        RadialMenuScreen.deactivate();
                    }
                }
            }
        }
        lastWheelState = radialMenuKeyDown;
    }

    public static boolean hasShiftDown() {
        return InputConstants.isKeyDown(InputConstants.KEY_LSHIFT)
                || InputConstants.isKeyDown(InputConstants.KEY_RSHIFT);
    }

    private static boolean isMouseButtonDown(int button) {
        return button > 0 && button <= 32 && (SDLMouse.SDL_GetMouseState(null, null) & (1 << (button - 1))) != 0;
    }
}
