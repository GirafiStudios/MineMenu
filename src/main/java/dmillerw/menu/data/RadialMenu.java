package dmillerw.menu.data;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author dmillerw
 */
public class RadialMenu {

	public static final int MAX_ITEMS = 10;

	public static final String MAIN_TAG = "main";

	public static Map<String, MenuItem[]> menuMap = Maps.newHashMap();

	public static MenuItem[] getArray(String tag) {
		if (!menuMap.containsKey(tag)) {
			menuMap.put(tag, new MenuItem[MAX_ITEMS]);
		}
		return menuMap.get(tag);
	}

	public static void replaceArray(String tag, MenuItem[] array) {
		if (array.length != MAX_ITEMS) {
			throw new RuntimeException();
		}
		menuMap.put(tag, array);
	}
}
