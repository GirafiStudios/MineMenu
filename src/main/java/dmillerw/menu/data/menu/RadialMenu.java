package dmillerw.menu.data.menu;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Set;

/**
 * @author dmillerw
 */
public class RadialMenu {

    public static final int MAX_ITEMS = 10;

    public static final String MAIN_TAG = "main";

    private static Map<String, MenuItem[]> menuMap = Maps.newHashMap();

    public static String currentCategory = MAIN_TAG;

    public static int animationTimer = 20;

    public static Set<String> getCategories() {
        return menuMap.keySet();
    }

    public static void resetCategory() {
        currentCategory = MAIN_TAG;
    }

    public static void tickTimer() {
        if (animationTimer > 0) {
            animationTimer -= 5;
        }
    }

    public static void resetTimer() {
        animationTimer = 20;
    }

    public static MenuItem[] getActiveArray() {
        return getArray(currentCategory);
    }

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
