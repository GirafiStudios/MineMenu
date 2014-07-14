package dmillerw.menu.data.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dmillerw.menu.MineMenu;
import dmillerw.menu.data.MenuItem;
import dmillerw.menu.data.RadialMenu;
import dmillerw.menu.data.click.IClickAction;
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
				// Woah, very bad
			}

			for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
				String key = entry.getKey();
				JsonElement data = entry.getValue();

				try {
					int id = Integer.valueOf(key);

					if (id < RadialMenu.MAX_ITEMS) {
						RadialMenu.menuItems[id] = gson.fromJson(data, MenuItem.class);
					}
				} catch (NumberFormatException ex) {
					// Bad key, log and skip
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void save() {
		JsonObject object = new JsonObject();
		for (int i=0; i< RadialMenu.MAX_ITEMS; i++) {
			if (RadialMenu.menuItems[i] != null) {
				object.add(String.valueOf(i), gson.toJsonTree(RadialMenu.menuItems[i]));
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
