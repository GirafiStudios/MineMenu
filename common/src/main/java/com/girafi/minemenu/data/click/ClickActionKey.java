package com.girafi.minemenu.data.click;

import com.girafi.minemenu.handler.KeyboardHandlerHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class ClickActionKey implements ClickAction.IClickAction {
    public final String key;
    public static boolean toggle;

    public ClickActionKey(String key, boolean toggle) {
        this.key = key;
        this.toggle = toggle;
    }

    public KeyMapping getKeyBinding() {
        for (KeyMapping binding : Minecraft.getInstance().options.keyMappings) {
            if (binding.getName().equalsIgnoreCase(key)) {
                return binding;
            }
        }
        return null;
    }

    @Override
    public ClickAction getClickAction() {
        return ClickAction.KEYBIND;
    }

    @Override
    public void onClicked() {
        KeyMapping binding = getKeyBinding();
        if (binding != null) {
            if (toggle) {
                KeyboardHandlerHelper.INSTANCE.toggleKey(binding);
            } else {
                KeyboardHandlerHelper.INSTANCE.fireKey(binding);
            }
        }
    }

    @Override
    public boolean deactivates() {
        return true;
    }

    @Override
    public void onRemoved() {
        KeyMapping keyBinding = getKeyBinding();
        if (keyBinding != null) {
            keyBinding.setDown(false);
            keyBinding.clickCount = 0;
        }
    }
}