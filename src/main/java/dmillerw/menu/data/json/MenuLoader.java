package dmillerw.menu.data.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dmillerw.menu.MineMenu;
import dmillerw.menu.data.MenuItem;
import dmillerw.menu.data.RadialMenu;
import dmillerw.menu.data.click.CommandClickAction;
import dmillerw.menu.data.click.IClickAction;
import dmillerw.menu.data.click.KeyClickAction;
import dmillerw.menu.handler.LogHandler;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.io.*;
import java.util.Map;

/**
 * @author dmillerw
 */
public class MenuLoader {

	private static Gson gson;

	static {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(ItemStack.class, new ItemStackSerializer());
		builder.registerTypeAdapter(IClickAction.class, new ClickActionSerializer());
		gson = builder.create();
	}

	public static void load() {
		try {
			JsonElement element = gson.fromJson(new FileReader(new File(MineMenu.configFolder, "menu.json")), JsonElement.class);

			if (!element.isJsonObject()) {
				LogHandler.error(String.format("Failed to load menu.json! Improperly formatted file!"));
				return;
			}

			MenuItem[] array = RadialMenu.getArray(RadialMenu.MAIN_TAG);
			
			for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
				String key = entry.getKey();
				JsonElement data = entry.getValue();

				try {
					int id = Integer.valueOf(key);

					if (id < RadialMenu.MAX_ITEMS) {
						array[id] = gson.fromJson(data, MenuItem.class);

						MenuItem item = array[id];

						if (item.icon == null || item.icon.getItem() == null) {
							LogHandler.warn(String.format("Menu item in slot %s is looking for an item that no longer exists", String.valueOf(id)));
							MenuItem newItem = new MenuItem(item.title, new ItemStack(Blocks.stone), item.clickAction);
							array[id] = newItem;
						}

						if (item.clickAction == null) {
							LogHandler.error(String.format("Menu item in slot %s is missing a click action. It will be reset!", String.valueOf(id)));
							array[id] = null;
						} else {
							if ((item.clickAction instanceof CommandClickAction && ((CommandClickAction)item.clickAction).command.isEmpty())) {
								LogHandler.warn(String.format("Menu item in slot %s is defined as a command action, but is missing a command. It will be reset!", String.valueOf(id)));
								array[id] = null;
							} else if (item.clickAction instanceof KeyClickAction && ((KeyClickAction)item.clickAction).getKeyBinding() == null) {
								LogHandler.warn(String.format("Menu item in slot %s is defined as a key action, but is missing a keybinding. It will be reset!", String.valueOf(id)));
								array[id] = null;
							}
						}
					}
				} catch (NumberFormatException ex) {
					LogHandler.warn(String.format("Menu item found with invalid key. Ignoring."));
				}
			}

			RadialMenu.replaceArray(RadialMenu.MAIN_TAG, array);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void save() {
		File file = new File(MineMenu.configFolder, "menu.json");

		if (file.exists()) {
			file.delete();
		}

		MenuItem[] array = RadialMenu.getArray(RadialMenu.MAIN_TAG);

		JsonObject object = new JsonObject();
		for (int i=0; i< RadialMenu.MAX_ITEMS; i++) {
			if (array[i] != null) {
				object.add(String.valueOf(i), gson.toJsonTree(array[i]));
			}
		}

		try {
			FileWriter writer = new FileWriter(new File(MineMenu.configFolder, "menu.json"));
			writer.append(gson.toJson(object));
			writer.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
