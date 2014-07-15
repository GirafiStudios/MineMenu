package dmillerw.menu.helper;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.settings.KeyBinding;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author dmillerw
 */
public class KeyReflectionHelper {

	private static Method unpressKeyMethod;

	private static Field pressedField;
	private static Field pressTimeField;

	public static void gatherFields() {
		unpressKeyMethod = ReflectionHelper.findMethod(KeyBinding.class, null, new String[] {"unpressKey", "func_74505_d", "j"});
		unpressKeyMethod.setAccessible(true);

		pressedField = ReflectionHelper.findField(KeyBinding.class, "pressed", "field_74513_e", "h");
		pressedField.setAccessible(true);
		pressTimeField = ReflectionHelper.findField(KeyBinding.class, "pressTime", "field_151474_i", "i");
		pressTimeField.setAccessible(true);
	}

	public static void unpressKey(KeyBinding keyBinding) {
		try {
			unpressKeyMethod.invoke(keyBinding);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static void pressKey(KeyBinding binding) {
		try {
			pressedField.set(binding, true);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static void increasePressTime(KeyBinding binding) {
		try {
			pressTimeField.set(binding, pressTimeField.getInt(binding) + 1);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
