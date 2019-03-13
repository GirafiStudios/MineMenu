package dmillerw.menu.helper;

import dmillerw.menu.handler.LogHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;

public class KeyReflectionHelper {
    private static Field pressTimeField;

    public static void gatherFields() {
        pressTimeField = ReflectionHelper.findField(KeyBinding.class, "pressTime", "field_151474_i");
        pressTimeField.setAccessible(true);
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