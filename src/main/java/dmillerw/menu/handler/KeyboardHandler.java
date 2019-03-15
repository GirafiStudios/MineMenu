package dmillerw.menu.handler;

import dmillerw.menu.data.menu.RadialMenu;
import dmillerw.menu.gui.GuiRadialMenu;
import dmillerw.menu.helper.KeyReflectionHelper;
import dmillerw.menu.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
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

        this.setFocus();
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
        this.setFocus();
    }

    private void setFocus() { //TODO Test
        Minecraft mc = Minecraft.getInstance();
        boolean old = mc.isGameFocused();
        mc.focusChanged(true);
        MinecraftForge.EVENT_BUS.post(new InputEvent.KeyInputEvent());
        mc.focusChanged(old);

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

        boolean wheelKeyPressed = WHEEL.getKey().getKeyCode() >= 0 ? InputMappings.isKeyDown(WHEEL.getKey().getKeyCode()) : InputMappings.isKeyDown(WHEEL.getKey().getKeyCode() + 100);

        if (wheelKeyPressed != lastWheelState) {
            if (ConfigHandler.GENERAL.toggle.get()) {
                if (wheelKeyPressed) {
                    if (GuiRadialMenu.active) {
                        if (ConfigHandler.GENERAL.releaseToSelect.get()) {
                            GuiRadialMenu.INSTANCE.mouseClicked(mc.mouseHelper.getMouseX(), mc.mouseHelper.getMouseY(), 0);
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
                        if (ConfigHandler.GENERAL.releaseToSelect.get()) {
                            GuiRadialMenu.INSTANCE.mouseClicked(mc.mouseHelper.getMouseX(), mc.mouseHelper.getMouseY(), 0);
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

        Iterator<KeyBinding> iterator = FIRED_KEYS.iterator();
        while (iterator.hasNext()) {
            KeyBinding keyBinding = iterator.next();
            KeyBinding.setKeyBindState(keyBinding.getKey(), false);
            iterator.remove();
        }

        iterator = TOGGLED_KEYS.iterator();
        while (iterator.hasNext()) {
            KeyBinding keyBinding = iterator.next();
            if ((keyBinding.getKey().getKeyCode() >= 0 ? keyBinding.isPressed() : InputMappings.isKeyDown(keyBinding.getKey().getKeyCode() + 100)) || mc.currentScreen != null) {
                iterator.remove();
            }
        }

        for (KeyBinding keyBinding : TOGGLED_KEYS) {
            KeyReflectionHelper.setPressTime(keyBinding, 1);
        }
    }
}