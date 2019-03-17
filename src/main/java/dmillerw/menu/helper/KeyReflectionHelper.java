package dmillerw.menu.helper;

import dmillerw.menu.handler.LogHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

import java.lang.reflect.Field;

public class KeyReflectionHelper {
    private static Field pressTimeField;

    public static void gatherFields() {
        try {
            pressTimeField = KeyBinding.class.getDeclaredField(/*FMLLaunchHandler.isDeobfuscatedEnvironment() ? "pressTime" :*/ "field_151474_i"); //DO NOT USE isDeobfuscatedEnvironment, WILL BREAK OLDER VERSIONS OF FORGE (And Sponge Forge)
            pressTimeField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throwReflectionError("pressTime", KeyBinding.class);
        }
    }

    public static void setPressTime(KeyBinding keyBinding, int pressTime) {
        try {
            pressTimeField.set(keyBinding, pressTime == 0 ? 0 : pressTimeField.getInt(keyBinding) + pressTime);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throwReflectionError("pressTime", KeyBinding.class);
        }
    }

    private static void throwReflectionError(String field, Class<?> clazz) {
        String error = String.format("Ran into an issue regarding reflection with field %s from %s. REPORT THIS TO THE MOD AUTHOR!", field, clazz.getName());
        LogHandler.fatal(error);
        throw new RuntimeException(error);
    }
}