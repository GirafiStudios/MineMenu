package dmillerw.menu.data.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dmillerw.menu.data.click.ClickAction;
import dmillerw.menu.data.click.ClickActionCommand;
import dmillerw.menu.data.click.ClickActionKey;
import dmillerw.menu.data.menu.MenuItem;
import dmillerw.menu.data.menu.RadialMenu;
import dmillerw.menu.handler.LogHandler;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.io.*;
import java.util.Map;

/**
 * @author dmillerw
 */
public class MenuLoader {
    private static final Gson gson;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.registerTypeAdapter(ItemStack.class, new ItemStackSerializer());
        builder.registerTypeAdapter(ClickAction.IClickAction.class, new ClickActionSerializer());
        gson = builder.create();
    }

    public static void load(File file) {
        try {
            JsonElement element = gson.fromJson(new FileReader(file), JsonElement.class);

            if (!element.isJsonObject()) {
                LogHandler.error(String.format("Failed to load menu.json! Improperly formatted file!"));
                return;
            }

            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                MenuItem[] array = RadialMenu.getArray(entry.getKey());

                if (!entry.getValue().isJsonObject()) {
                    LogHandler.error(String.format("Failed to load %s category! Improperly formatted!", entry.getKey()));
                    continue;
                }

                for (Map.Entry<String, JsonElement> entry1 : entry.getValue().getAsJsonObject().entrySet()) {
                    String key = entry1.getKey();
                    JsonElement data = entry1.getValue();

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
                                if ((item.clickAction instanceof ClickActionCommand && ((ClickActionCommand) item.clickAction).command.isEmpty())) {
                                    LogHandler.warn(String.format("Menu item in slot %s is defined as a command action, but is missing a command. It will be reset!", String.valueOf(id)));
                                    array[id] = null;
                                } else if (item.clickAction instanceof ClickActionKey && ((ClickActionKey) item.clickAction).getKeyBinding() == null) {
                                    LogHandler.warn(String.format("Menu item in slot %s is defined as a key action, but is missing a keybinding. It will be reset!", String.valueOf(id)));
                                    array[id] = null;
                                }
                            }
                        }
                    } catch (NumberFormatException ex) {
                        LogHandler.warn(String.format("Menu item found with invalid key. Ignoring."));
                    }
                }

                RadialMenu.replaceArray(entry.getKey(), array);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void save(File file) {
        if (file.exists()) {
            file.delete();
        }

        JsonObject object = new JsonObject();

        for (String category : RadialMenu.getCategories()) {
            JsonObject object1 = new JsonObject();
            MenuItem[] array = RadialMenu.getArray(category);

            for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
                if (array[i] != null) {
                    object1.add(String.valueOf(i), gson.toJsonTree(array[i]));
                }
            }

            object.add(category, object1);
        }

        try {
            FileWriter writer = new FileWriter(file);
            writer.append(gson.toJson(object));
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}