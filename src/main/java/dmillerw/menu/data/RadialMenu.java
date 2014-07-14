package dmillerw.menu.data;

import dmillerw.menu.data.click.CommandClickAction;
import dmillerw.menu.data.click.KeyClickAction;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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
		MenuItem rain = new MenuItem("Toggle Rain", new ItemStack(Items.water_bucket), new CommandClickAction("/toggledownfall"));
		menuItems[2] = rain;
		MenuItem inventory = new MenuItem("Inventory", new ItemStack(Blocks.crafting_table), new KeyClickAction(Minecraft.getMinecraft().gameSettings.keyBindInventory));
		menuItems[3] = inventory;
	}
}
