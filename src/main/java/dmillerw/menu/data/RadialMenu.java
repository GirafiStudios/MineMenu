package dmillerw.menu.data;

import cpw.mods.fml.common.registry.GameData;
import dmillerw.menu.data.click.CommandClickAction;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.util.Random;

/**
 * @author dmillerw
 */
public class RadialMenu {

	public static final int MAX_ITEMS = 10;

	public static MenuItem[] menuItems = new MenuItem[MAX_ITEMS];

	public static void fillWithDummyData() {
		Random random = new Random();

		MenuItem night = new MenuItem("Set time to Night", new ItemStack(Blocks.obsidian), new CommandClickAction("/time set night"));
		menuItems[0] = night;
		MenuItem day = new MenuItem("Set time to Day", new ItemStack(Blocks.quartz_block), new CommandClickAction("/time set day"));
		menuItems[1] = day;
	}
}
