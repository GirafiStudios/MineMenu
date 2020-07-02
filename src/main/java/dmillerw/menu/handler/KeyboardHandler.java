package dmillerw.menu.handler;

import dmillerw.menu.MineMenu;
import dmillerw.menu.data.menu.RadialMenu;
import dmillerw.menu.gui.RadialMenuScreen;
import dmillerw.menu.helper.KeyReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@EventBusSubscriber(modid = MineMenu.MOD_ID, value = Dist.CLIENT)
public class KeyboardHandler {
    public static final KeyboardHandler INSTANCE = new KeyboardHandler();
    private static final KeyBinding WHEEL = new KeyBinding("key.open_menu", GLFW.GLFW_KEY_R, "key.categories.misc");

    public static void register() {
        ClientRegistry.registerKeyBinding(WHEEL);
    }

    private static boolean lastWheelState = false;
    private static final List<KeyBinding> FIRED_KEYS = new ArrayList<>();
    private static final List<KeyBinding> TOGGLED_KEYS = new ArrayList<>();
    private static boolean ignoreNextTick = false;

    private KeyboardHandler() {
    }

    public void fireKey(KeyBinding key) {
        FIRED_KEYS.add(key);
        KeyBinding.setKeyBindState(key.getKey(), true);
        KeyReflectionHelper.setPressTime(key, 1);

        ignoreNextTick = true;
    }

    public void toggleKey(KeyBinding key) {
        if (!TOGGLED_KEYS.contains(key)) {
            TOGGLED_KEYS.add(key);
            KeyBinding.setKeyBindState(key.getKey(), true);
            KeyReflectionHelper.setPressTime(key, 1);
        } else {
            TOGGLED_KEYS.remove(key);
            KeyBinding.setKeyBindState(key.getKey(), false);
        }
        ignoreNextTick = true;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();

        if (mc.world == null) {
            return;
        }

        long handle = Minecraft.getInstance().getMainWindow().getHandle();
        boolean wheelKeyPressed = WHEEL.getKey().getKeyCode() >= 0 ? InputMappings.isKeyDown(handle, WHEEL.getKey().getKeyCode()) : InputMappings.isKeyDown(handle, WHEEL.getKey().getKeyCode() + 100);

        if (wheelKeyPressed != lastWheelState) {
            if (ConfigHandler.GENERAL.toggle.get()) {
                if (wheelKeyPressed) {
                    if (RadialMenuScreen.active) {
                        if (ConfigHandler.GENERAL.releaseToSelect.get()) {
                            RadialMenuScreen.INSTANCE.func_231044_a_(mc.mouseHelper.getMouseX(), mc.mouseHelper.getMouseY(), 0);
                        }
                        RadialMenuScreen.deactivate();
                    } else {
                        if (mc.currentScreen == null || mc.currentScreen instanceof RadialMenuScreen) {
                            RadialMenu.resetCategory();
                            RadialMenu.resetTimer();
                            RadialMenuScreen.activate();
                        }
                    }
                }
            } else {
                if (wheelKeyPressed != RadialMenuScreen.active) {
                    if (wheelKeyPressed) {
                        if (mc.currentScreen == null || mc.currentScreen instanceof RadialMenuScreen) {
                            RadialMenu.resetCategory();
                            RadialMenu.resetTimer();
                            RadialMenuScreen.activate();
                        }
                    } else {
                        if (ConfigHandler.GENERAL.releaseToSelect.get()) {
                            RadialMenuScreen.INSTANCE.func_231044_a_(mc.mouseHelper.getMouseX(), mc.mouseHelper.getMouseY(), 0);
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

        Iterator<KeyBinding> iterator = FIRED_KEYS.iterator();
        while (iterator.hasNext()) {
            KeyBinding keyBinding = iterator.next();
            KeyBinding.setKeyBindState(keyBinding.getKey(), false);
            iterator.remove();
        }

        iterator = TOGGLED_KEYS.iterator();
        while (iterator.hasNext()) {
            KeyBinding keyBinding = iterator.next();
            if ((keyBinding.getKey().getKeyCode() >= 0 ? keyBinding.isPressed() : InputMappings.isKeyDown(handle, keyBinding.getKey().getKeyCode() + 100)) || mc.currentScreen != null) {
                iterator.remove();
            }
        }

        for (KeyBinding keyBinding : TOGGLED_KEYS) {
            KeyReflectionHelper.setPressTime(keyBinding, 1);
        }
    }
}