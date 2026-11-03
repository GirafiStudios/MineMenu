package com.girafi.minemenu.platform.services;

import net.minecraft.client.KeyMapping;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    /**
     * Gets the keys a key binding needs held as a modifier, e.g. Ctrl for a "Ctrl + K" binding. Client only.
     *
     * @return Key values (SDL scancodes), or an empty array if the binding has no modifier. Only NeoForge supports key modifiers
     */
    default int[] getKeyModifierKeys(KeyMapping mapping) {
        return new int[0];
    }

    /**
     * Gets the SDL modifier flags (InputConstants.MOD_*) matching the key binding's modifier, sent along with emulated key events. Client only.
     */
    default int getKeyModifierFlags(KeyMapping mapping) {
        return 0;
    }
}
