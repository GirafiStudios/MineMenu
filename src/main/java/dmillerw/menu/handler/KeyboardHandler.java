package dmillerw.menu.handler;

import com.mojang.blaze3d.platform.InputConstants;
import dmillerw.menu.MineMenu;
import dmillerw.menu.data.menu.RadialMenu;
import dmillerw.menu.gui.RadialMenuScreen;
import dmillerw.menu.helper.KeyReflectionHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@EventBusSubscriber(modid = MineMenu.MOD_ID, value = Dist.CLIENT)
public class KeyboardHandler {
    public static final KeyboardHandler INSTANCE = new KeyboardHandler();
    private static boolean lastWheelState = false;
    private static final List<KeyMapping> FIRED_KEYS = new ArrayList<>();
    private static final List<KeyMapping> TOGGLED_KEYS = new ArrayList<>();
    private static boolean ignoreNextTick = false;

    private KeyboardHandler() {
    }

    public void fireKey(KeyMapping key) {
        FIRED_KEYS.add(key);
        KeyMapping.set(key.getKey(), true);
        KeyReflectionHelper.setPressTime(key, 1);

        ignoreNextTick = true;
    }

    public void toggleKey(KeyMapping key) {
        if (!TOGGLED_KEYS.contains(key)) {
            TOGGLED_KEYS.add(key);
            KeyMapping.set(key.getKey(), true);
            KeyReflectionHelper.setPressTime(key, 1);
        } else {
            TOGGLED_KEYS.remove(key);
            KeyMapping.set(key.getKey(), false);
        }
        ignoreNextTick = true;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null) {
            return;
        }

        long handle = Minecraft.getInstance().getWindow().getWindow();
        boolean wheelKeyPressed = MineMenuKeyMappings.WHEEL.getKey().getValue() >= 0 ? InputConstants.isKeyDown(handle, MineMenuKeyMappings.WHEEL.getKey().getValue()) : InputConstants.isKeyDown(handle, MineMenuKeyMappings.WHEEL.getKey().getValue() + 100);

        if (wheelKeyPressed != lastWheelState) {
            if (ConfigHandler.GENERAL.toggle.get()) {
                if (wheelKeyPressed) {
                    if (RadialMenuScreen.active) {
                        if (ConfigHandler.GENERAL.releaseToSelect.get()) {
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
                if (wheelKeyPressed != RadialMenuScreen.active) {
                    if (wheelKeyPressed) {
                        if (mc.screen == null || mc.screen instanceof RadialMenuScreen) {
                            RadialMenu.resetCategory();
                            RadialMenu.resetTimer();
                            RadialMenuScreen.activate();
                        }
                    } else {
                        if (ConfigHandler.GENERAL.releaseToSelect.get()) {
                            RadialMenuScreen.INSTANCE.mouseClicked(mc.mouseHandler.xpos(), mc.mouseHandler.ypos(), 0);
                        }
                        RadialMenuScreen.deactivate();
                    }
                }
            }
        }
        lastWheelState = wheelKeyPressed;

        if (ignoreNextTick) {
            ignoreNextTick = false;
            return;
        }

        Iterator<KeyMapping> iterator = FIRED_KEYS.iterator();
        while (iterator.hasNext()) {
            KeyMapping keyBinding = iterator.next();
            KeyMapping.set(keyBinding.getKey(), false);
            iterator.remove();
        }

        iterator = TOGGLED_KEYS.iterator();
        while (iterator.hasNext()) {
            KeyMapping keyBinding = iterator.next();
            if ((keyBinding.getKey().getValue() >= 0 ? keyBinding.consumeClick() : InputConstants.isKeyDown(handle, keyBinding.getKey().getValue() + 100)) || mc.screen != null) {
                iterator.remove();
            }
        }

        for (KeyMapping keyBinding : TOGGLED_KEYS) {
            KeyReflectionHelper.setPressTime(keyBinding, 1);
        }
    }
}