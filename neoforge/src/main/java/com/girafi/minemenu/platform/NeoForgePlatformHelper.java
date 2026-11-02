package com.girafi.minemenu.platform;

import com.girafi.minemenu.platform.services.IPlatformHelper;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public int[] getKeyModifierKeys(KeyMapping mapping) {
        KeyModifier modifier = mapping.getKeyModifier();
        InputConstants.Key[] codes = modifier.codes();
        return modifier == KeyModifier.NONE || codes == null || codes.length == 0 ? new int[0] : new int[]{codes[0].getValue()};
    }

    @Override
    public int getKeyModifierFlags(KeyMapping mapping) {
        return switch (mapping.getKeyModifier()) {
            case CONTROL -> Minecraft.ON_OSX ? GLFW.GLFW_MOD_SUPER : GLFW.GLFW_MOD_CONTROL;
            case SHIFT -> GLFW.GLFW_MOD_SHIFT;
            case ALT -> GLFW.GLFW_MOD_ALT;
            case NONE -> 0;
        };
    }
}
