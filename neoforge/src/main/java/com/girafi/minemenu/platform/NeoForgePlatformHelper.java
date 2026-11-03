package com.girafi.minemenu.platform;

import com.girafi.minemenu.platform.services.IPlatformHelper;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.InputQuirks;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.settings.KeyModifier;

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
        return !FMLLoader.getCurrent().isProduction();
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
            case CONTROL -> InputConstants.MOD_CONTROL;
            case CONTROL_OR_COMMAND -> InputQuirks.ON_OSX ? InputConstants.MOD_SUPER : InputConstants.MOD_CONTROL;
            case SHIFT -> InputConstants.MOD_SHIFT;
            case ALT -> InputConstants.MOD_ALT;
            case NONE -> 0;
        };
    }
}
