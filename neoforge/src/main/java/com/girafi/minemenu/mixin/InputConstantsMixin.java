package com.girafi.minemenu.mixin;

import com.girafi.minemenu.helper.KeyEmulator;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InputConstants.class)
public class InputConstantsMixin {

    // Support for NeoForge key modifiers (Ctrl/Shift/Alt bindings)
    @Inject(method = "isKeyDown", at = @At("HEAD"), cancellable = true)
    private static void virtualModifierKeys(Window window, int key, CallbackInfoReturnable<Boolean> cir) {
        if (KeyEmulator.isVirtuallyDown(key)) {
            cir.setReturnValue(true);
        }
    }
}
