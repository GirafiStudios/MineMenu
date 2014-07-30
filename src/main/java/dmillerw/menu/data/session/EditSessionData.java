package dmillerw.menu.data.session;

import dmillerw.menu.data.click.ClickAction;
import dmillerw.menu.data.menu.MenuItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**
 * @author dmillerw
 */
public class EditSessionData {

    public static String title;

    public static ItemStack icon;

    public static ClickAction.IClickAction clickAction;

    public static void clear() {
        title = "";
        icon = new ItemStack(Blocks.stone);
        clickAction = null;
    }

    public static void fromMenuItem(MenuItem item) {
        title = item != null && item.title != null ? item.title : "";
        icon = item != null && item.icon != null ? item.icon : new ItemStack(Blocks.stone);
        clickAction = item != null && item.clickAction != null ? item.clickAction : null;
    }

    public static MenuItem toMenuItem() {
        return new MenuItem(title, icon, clickAction);
    }
}
