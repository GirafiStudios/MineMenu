package com.girafi.minemenu.mixin;

import com.girafi.minemenu.gui.RadialMenuScreen;
import com.girafi.minemenu.handler.ClientTickHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(at = @At("TAIL"), method = "render(FJZ)V")
    private void render(float f, long l, boolean b, CallbackInfo info) {
        GameRenderer gameRenderer = (GameRenderer) (Object) this;
        Minecraft mc = gameRenderer.getMinecraft();

        if (mc.level != null && !mc.options.hideGui && !mc.isPaused() && RadialMenuScreen.active) {
            System.out.println("RENDER MINE MENU");
            GuiGraphics guiGraphics = new GuiGraphics(mc, gameRenderer.renderBuffers.bufferSource());
            ClientTickHelper.renderButtonBackgrounds();
            ClientTickHelper.renderItems(guiGraphics);
            ClientTickHelper.renderText(guiGraphics);
        }
    }
}