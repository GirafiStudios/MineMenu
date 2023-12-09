package com.girafi.minemenu.helper;

import com.girafi.minemenu.Constants;
import net.minecraft.client.KeyMapping;

import java.lang.reflect.Field;

public class KeyReflectionHelper {
    private static Field pressTimeField;

    public static void gatherFields() {
        try {
            pressTimeField = KeyMapping.class.getDeclaredField("clickCount");
            pressTimeField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throwReflectionError("clickCount", KeyMapping.class);
        }
    }

    public static void setClickCount(KeyMapping keyBinding, int clickCount) {
        try {
            pressTimeField.set(keyBinding, clickCount == 0 ? 0 : pressTimeField.getInt(keyBinding) + clickCount);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throwReflectionError("clickCount", KeyMapping.class);
        }
    }

    private static void throwReflectionError(String field, Class<?> clazz) {
        String error = String.format("Ran into an issue regarding reflection with field %s from %s. REPORT THIS TO THE MOD AUTHOR!", field, clazz.getName());
        Constants.LOG.fatal(error);
        throw new RuntimeException(error);
    }
}