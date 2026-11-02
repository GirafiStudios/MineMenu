package com.girafi.minemenu.handler;

import com.girafi.minemenu.platform.Services;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;

/**
 * Emulates key presses for key bindings triggered from the radial menu.
 * Keyboard bound keys are sent through ({@link net.minecraft.client.KeyboardHandler#keyPress}), exactly like a physical key press.
 * Unbound and mouse bound keys can't be sent that way, so their key binding state is changed directly instead.
 */
public class KeyEmulator {
    /** Ticks between the radial menu closing and the key press */
    private static final int PRESS_DELAY = 1;
    /** How long a single press is held */
    private static final int HOLD_TICKS = 2;

    private static final List<ScheduledAction> SCHEDULED = new ArrayList<>();
    private static final Set<KeyMapping> TOGGLED = new LinkedHashSet<>();
    /** Keys currently pressed by the emulator, from press until release. Their modifier keys (NeoForge) are reported as held */
    private static final Set<KeyMapping> HELD = new LinkedHashSet<>();
    private static KeyMapping sending;
    private static boolean physicalOnly;

    public static void pressOnce(KeyMapping mapping) {
        schedule(PRESS_DELAY, () -> press(mapping));
        schedule(PRESS_DELAY + HOLD_TICKS, () -> release(mapping));
    }

    public static void toggle(KeyMapping mapping) {
        if (TOGGLED.remove(mapping)) {
            schedule(PRESS_DELAY, () -> release(mapping));
        } else {
            TOGGLED.add(mapping);
            schedule(PRESS_DELAY, () -> press(mapping));
        }
    }

    public static void stopToggle(KeyMapping mapping) {
        if (TOGGLED.remove(mapping)) {
            release(mapping);
        }
    }

    public static void releaseAll() {
        SCHEDULED.clear();
        for (KeyMapping mapping : TOGGLED) {
            setState(mapping, false);
        }
        TOGGLED.clear();
        HELD.clear();
    }

    /**
     * Whether a key should be reported as held because an emulated key binding uses it as a NeoForge key modifier.
     */
    public static boolean isVirtuallyDown(int keyCode) {
        if (HELD.isEmpty() || physicalOnly) {
            return false;
        }
        if (sending == null && Minecraft.getInstance().screen != null) {
            return false;
        }
        for (KeyMapping mapping : HELD) {
            for (int modifierKey : Services.PLATFORM.getKeyModifierKeys(mapping)) {
                if (modifierKey == keyCode) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean readPhysical(BooleanSupplier read) {
        physicalOnly = true;
        try {
            return read.getAsBoolean();
        } finally {
            physicalOnly = false;
        }
    }

    public static void tick() {
        Iterator<ScheduledAction> iterator = SCHEDULED.iterator();
        List<Runnable> due = new ArrayList<>();
        while (iterator.hasNext()) {
            ScheduledAction action = iterator.next();
            if (--action.ticks <= 0) {
                due.add(action.action);
                iterator.remove();
            }
        }
        // Run after iterating, as an action may schedule more actions
        due.forEach(Runnable::run);

        // Opening and closing screens resets key states to the physical keyboard (KeyMapping.releaseAll/setAll), which would drop toggled keys.
        // Quietly restore them while in game, like a physically held key, without sending another press
        if (Minecraft.getInstance().screen == null) {
            for (KeyMapping mapping : TOGGLED) {
                mapping.setDown(true);
            }
        }
    }

    private static void schedule(int ticks, Runnable action) {
        SCHEDULED.add(new ScheduledAction(ticks, action));
    }

    private static void press(KeyMapping mapping) {
        HELD.add(mapping);
        if (!sendKeyEvent(mapping, GLFW.GLFW_PRESS)) {
            setState(mapping, true);
        }
    }

    private static void release(KeyMapping mapping) {
        // Still held while releasing, as NeoForge looks up bindings with a modifier only while that modifier is down
        if (!sendKeyEvent(mapping, GLFW.GLFW_RELEASE)) {
            setState(mapping, false);
        }
        HELD.remove(mapping);
    }

    /**
     * Sends a key event through vanilla's key handler, as if the key was physically pressed or released.
     *
     * @return False if the key can't be sent as a keyboard event (unbound, or bound to a mouse button)
     */
    private static boolean sendKeyEvent(KeyMapping mapping, int action) {
        InputConstants.Key key = mapping.key;
        if (key == null || key.equals(InputConstants.UNKNOWN)) {
            return false;
        }

        int keyCode;
        int scanCode;
        switch (key.getType()) {
            case KEYSYM -> {
                keyCode = key.getValue();
                scanCode = GLFW.glfwGetKeyScancode(keyCode);
            }
            case SCANCODE -> {
                keyCode = GLFW.GLFW_KEY_UNKNOWN;
                scanCode = key.getValue();
            }
            default -> {
                return false; // Mouse buttons
            }
        }

        Minecraft mc = Minecraft.getInstance();
        sending = mapping;
        try {
            mc.keyboardHandler.keyPress(mc.getWindow().getWindow(), keyCode, scanCode, action, Services.PLATFORM.getKeyModifierFlags(mapping));
        } finally {
            sending = null;
        }
        return true;
    }

    private static void setState(KeyMapping mapping, boolean down) {
        mapping.setDown(down);
        if (down) {
            mapping.clickCount++;
        }
    }

    private static class ScheduledAction {
        private int ticks;
        private final Runnable action;

        private ScheduledAction(int ticks, Runnable action) {
            this.ticks = ticks;
            this.action = action;
        }
    }
}
