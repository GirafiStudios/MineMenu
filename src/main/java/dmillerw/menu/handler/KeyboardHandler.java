package dmillerw.menu.handler;

import dmillerw.menu.data.menu.RadialMenu;
import dmillerw.menu.gui.GuiRadialMenu;
import dmillerw.menu.helper.KeyReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author dmillerw
 */
public class KeyboardHandler {
    public static final KeyboardHandler INSTANCE = new KeyboardHandler();

    private static final KeyBinding WHEEL = new KeyBinding("key.open_menu", Keyboard.KEY_R, "key.categories.misc");

    public static void register() {
        MinecraftForge.EVENT_BUS.register(KeyboardHandler.INSTANCE);
        ClientRegistry.registerKeyBinding(WHEEL);
    }

    private static boolean lastWheelState = false;

    private final List<KeyBinding> firedKeys = new ArrayList<KeyBinding>();
    private final List<KeyBinding> toggledKeys = new ArrayList<KeyBinding>();

    private boolean ignoreNextTick = false;

    private KeyboardHandler() {

    }

    public void fireKey(KeyBinding key) {
        firedKeys.add(key);
        KeyReflectionHelper.pressKey(key);
        KeyReflectionHelper.increasePressTime(key);

        boolean old = Minecraft.getMinecraft().inGameHasFocus;
        Minecraft.getMinecraft().inGameHasFocus = true;
        MinecraftForge.EVENT_BUS.post(new InputEvent.KeyInputEvent());
        Minecraft.getMinecraft().inGameHasFocus = old;

        ignoreNextTick = true;
    }

    public void toggleKey(KeyBinding key) {
        if (!toggledKeys.contains(key)) {
            toggledKeys.add(key);
            KeyReflectionHelper.pressKey(key);
            KeyReflectionHelper.increasePressTime(key);
        } else {
            toggledKeys.remove(key);
            KeyReflectionHelper.unpressKey(key);
        }

        boolean old = Minecraft.getMinecraft().inGameHasFocus;
        Minecraft.getMinecraft().inGameHasFocus = true;
        MinecraftForge.EVENT_BUS.post(new InputEvent.KeyInputEvent());
        Minecraft.getMinecraft().inGameHasFocus = old;

        ignoreNextTick = true;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.world == null) {
            return;
        }

        boolean wheelKeyPressed = (WHEEL.getKeyCode() >= 0 ? Keyboard.isKeyDown(WHEEL.getKeyCode()) : Mouse.isButtonDown(WHEEL.getKeyCode() + 100));

        if (wheelKeyPressed != lastWheelState) {
            if (ConfigHandler.toggle) {
                if (wheelKeyPressed) {
                    if (GuiRadialMenu.active) {
                        if (ConfigHandler.releaseToSelect) {
                            GuiRadialMenu.INSTANCE.mouseClicked(Mouse.getX(), Mouse.getY(), 0);
                        }
                        GuiRadialMenu.deactivate();
                    } else {
                        if (mc.currentScreen == null || mc.currentScreen instanceof GuiRadialMenu) {
                            RadialMenu.resetCategory();
                            RadialMenu.resetTimer();
                            GuiRadialMenu.activate();
                        }
                    }
                }
            } else {
                if (wheelKeyPressed != GuiRadialMenu.active) {
                    if (wheelKeyPressed) {
                        if (mc.currentScreen == null || mc.currentScreen instanceof GuiRadialMenu) {
                            RadialMenu.resetCategory();
                            RadialMenu.resetTimer();
                            GuiRadialMenu.activate();
                        }
                    } else {
                        if (ConfigHandler.releaseToSelect) {
                            GuiRadialMenu.INSTANCE.mouseClicked(Mouse.getX(), Mouse.getY(), 0);
                        }
                        GuiRadialMenu.deactivate();
                    }
                }
            }
        }
        lastWheelState = wheelKeyPressed;

        if (ignoreNextTick) {
            ignoreNextTick = false;
            return;
        }

        Iterator<KeyBinding> iterator = firedKeys.iterator();
        while (iterator.hasNext()) {
            KeyBinding keyBinding = iterator.next();
            KeyReflectionHelper.unpressKey(keyBinding);
            iterator.remove();
        }

        iterator = toggledKeys.iterator();
        while (iterator.hasNext()) {
            KeyBinding keyBinding = iterator.next();
            if ((keyBinding.getKeyCode() >= 0 ? Keyboard.isKeyDown(keyBinding.getKeyCode()) : Mouse.isButtonDown(keyBinding.getKeyCode() + 100))) {
                iterator.remove();
            }
        }

        for (KeyBinding keyBinding : toggledKeys) {
            KeyReflectionHelper.increasePressTime(keyBinding);
        }
    }
}