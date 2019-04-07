package dmillerw.menu.helper;

import cpw.mods.modlauncher.api.INameMappingService;
import dmillerw.menu.handler.LogHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

public class KeyReflectionHelper {
    private static Field pressTimeField;

    public static void gatherFields() {
        try {
            pressTimeField = KeyBinding.class.getDeclaredField(ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "field_151474_i"));
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