package dmillerw.menu.handler;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import dmillerw.menu.data.menu.RadialMenu;
import dmillerw.menu.gui.GuiRadialMenu;
import dmillerw.menu.helper.KeyReflectionHelper;
import dmillerw.menu.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
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

    public static final KeyBinding WHEEL = new KeyBinding("key.open_menu", Keyboard.KEY_R, "key.categories.gameplay");

    public static void register() {
        FMLCommonHandler.instance().bus().register(KeyboardHandler.INSTANCE);
        ClientRegistry.registerKeyBinding(WHEEL);
    }

    private static boolean lastWheelState = false;

    private List<KeyBinding> firedKeys = new ArrayList<KeyBinding>();
    private List<KeyBinding> toggledKeys = new ArrayList<KeyBinding>();

    private boolean ignoreNextTick = false;

    private KeyboardHandler() {

    }

    public void fireKey(KeyBinding key) {
        firedKeys.add(key);
        KeyReflectionHelper.pressKey(key);
        KeyReflectionHelper.increasePressTime(key);

        boolean old = Minecraft.getMinecraft().inGameHasFocus;
        Minecraft.getMinecraft().currentScreen = null;
        Minecraft.getMinecraft().inGameHasFocus = true;
        FMLCommonHandler.instance().bus().post(new InputEvent.KeyInputEvent());
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
        Minecraft.getMinecraft().currentScreen = null;
        Minecraft.getMinecraft().inGameHasFocus = true;
        FMLCommonHandler.instance().bus().post(new InputEvent.KeyInputEvent());
        Minecraft.getMinecraft().inGameHasFocus = old;

        ignoreNextTick = true;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.theWorld == null) {
            return;
        }

        boolean wheelKeyPressed = (WHEEL.getKeyCode() >= 0 ? Keyboard.isKeyDown(WHEEL.getKeyCode()) : Mouse.isButtonDown(WHEEL.getKeyCode() + 100));

        if (wheelKeyPressed != lastWheelState) {
            if (ClientProxy.toggle) {
                if (wheelKeyPressed) {
                    if (GuiRadialMenu.active) {
                        if (ClientProxy.releaseToSelect) {
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
                        if (ClientProxy.releaseToSelect) {
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
