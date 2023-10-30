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
    private static boolean ignoreNextTick = false;

    private KeyboardHandler() {
    }

    public void fireKey(KeyMapping keyMapping) {
        FIRED_KEYS.add(keyMapping);
        activateKeybind(keyMapping, true);
        ignoreNextTick = true;
    }

    public void toggleKey(KeyMapping keyMapping) {
        activateKeybind(keyMapping, true);
        ignoreNextTick = true;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END || event.type != TickEvent.Type.CLIENT) {
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
            activateKeybind(keyBinding, false);
            iterator.remove();
        }
    }

    public static void activateKeybind(KeyMapping keyMapping, boolean setDown) {
        //if (keyMapping.getCategory().equals("key.categories.movement"))
        keyMapping.setDown(setDown);
        KeyReflectionHelper.setClickCount(keyMapping, setDown ? 1 : 0);
    }
}