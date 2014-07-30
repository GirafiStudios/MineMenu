package dmillerw.menu.data.menu;

import com.google.gson.annotations.SerializedName;
import dmillerw.menu.data.click.ClickAction;
import net.minecraft.item.ItemStack;

/**
 * @author dmillerw
 */
public class MenuItem {

    public final String title;

    public final ItemStack icon;

    @SerializedName("action")
    public final ClickAction.IClickAction clickAction;

    public MenuItem(String title, ItemStack icon, ClickAction.IClickAction clickAction) {
        this.title = title;
        this.icon = icon;
        this.clickAction = clickAction;
    }
}
