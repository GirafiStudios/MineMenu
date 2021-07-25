package dmillerw.menu.data.session;

import dmillerw.menu.data.click.ClickAction;
import dmillerw.menu.data.menu.MenuItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nonnull;

public class EditSessionData {
    public static String title;
    @Nonnull
    public static ItemStack icon;
    public static ClickAction.IClickAction clickAction;

    public static void clear() {
        title = "";
        icon = new ItemStack(Blocks.STONE);
        clickAction = null;
    }

    public static void fromMenuItem(MenuItem item) {
        title = item != null && item.title != null ? item.title : "";
        icon = item != null && !item.icon.isEmpty() ? item.icon : new ItemStack(Blocks.STONE);
        clickAction = item != null && item.clickAction != null ? item.clickAction : null;
    }

    public static MenuItem toMenuItem() {
        return new MenuItem(title, icon, clickAction);
    }
}