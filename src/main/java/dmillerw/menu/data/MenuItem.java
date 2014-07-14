package dmillerw.menu.data;

import dmillerw.menu.data.click.IClickAction;
import net.minecraft.item.ItemStack;

/**
 * @author dmillerw
 */
public class MenuItem {

	public final String title;

	public final ItemStack icon;

	public final IClickAction clickAction;

	public MenuItem(String title, ItemStack icon, IClickAction clickAction) {
		this.title = title;
		this.icon = icon;
		this.clickAction = clickAction;
	}
}
