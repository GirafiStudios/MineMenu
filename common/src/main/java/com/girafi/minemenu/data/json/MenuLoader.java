package com.girafi.minemenu.data.json;

import com.girafi.minemenu.Constants;
import com.girafi.minemenu.data.click.ClickAction;
import com.girafi.minemenu.data.click.ClickActionCommand;
import com.girafi.minemenu.data.click.ClickActionKey;
import com.girafi.minemenu.data.menu.MenuItem;
import com.girafi.minemenu.data.menu.RadialMenu;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class MenuLoader {
    private static Gson GSON;

    public static void setGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.registerTypeAdapter(ItemStack.class, new ItemStackSerializer());
        builder.registerTypeAdapter(ClickAction.IClickAction.class, new ClickActionSerializer());
        GSON = builder.create();
    }

    public static void load(File file) {
        setGson();
        JsonElement element;
        try (Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            element = GSON.fromJson(reader, JsonElement.class);
        } catch (IOException | JsonParseException ex) {
            Constants.LOG.error("Failed to read menu.json!", ex);
            backupBrokenFile(file);
            return;
        }

        if (element == null || !element.isJsonObject()) {
            Constants.LOG.error("Failed to load menu.json! Improperly formatted file!");
            return;
        }

        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            MenuItem[] array = RadialMenu.getArray(entry.getKey());

            if (!entry.getValue().isJsonObject()) {
                Constants.LOG.error(String.format("Failed to load %s category! Improperly formatted!", entry.getKey()));
                continue;
            }

            for (Map.Entry<String, JsonElement> entry1 : entry.getValue().getAsJsonObject().entrySet()) {
                String key = entry1.getKey();
                JsonElement data = entry1.getValue();

                try {
                    int id = Integer.parseInt(key);

                    if (id < RadialMenu.MAX_ITEMS) {
                        MenuItem item = GSON.fromJson(data, MenuItem.class);
                        array[id] = item;

                        if (item == null) {
                            continue;
                        }

                        if (item.icon.isEmpty()) {
                            Constants.LOG.warn(String.format("Menu item in slot %s is looking for an item that no longer exists", String.valueOf(id)));
                            MenuItem newItem = new MenuItem(item.title, new ItemStack(Blocks.STONE), item.clickAction);
                            array[id] = newItem;
                        }

                        if (item.clickAction == null) {
                            Constants.LOG.error(String.format("Menu item in slot %s is missing a click action. It will be reset!", String.valueOf(id)));
                            array[id] = null;
                        } else {
                            if ((item.clickAction instanceof ClickActionCommand && ((ClickActionCommand) item.clickAction).command.isEmpty())) {
                                Constants.LOG.warn(String.format("Menu item in slot %s is defined as a command action, but is missing a command. It will be reset!", String.valueOf(id)));
                                array[id] = null;
                            } else if (item.clickAction instanceof ClickActionKey && ((ClickActionKey) item.clickAction).getKeyBinding() == null) {
                                Constants.LOG.warn(String.format("Menu item in slot %s is defined as a key action, but is missing a keybinding. It will be reset!", String.valueOf(id)));
                                array[id] = null;
                            }
                        }
                    }
                } catch (NumberFormatException ex) {
                    Constants.LOG.warn("Menu item found with invalid key. Ignoring.");
                } catch (RuntimeException ex) {
                    Constants.LOG.error(String.format("Failed to load menu item %s in category %s. Ignoring it.", key, entry.getKey()), ex);
                }
            }

            RadialMenu.replaceArray(entry.getKey(), array);
        }
    }

    /**
     * Keeps a copy of an unreadable menu.json, so the user's configuration isn't lost when the menu is saved again.
     */
    private static void backupBrokenFile(File file) {
        Path backup = file.toPath().resolveSibling(file.getName() + ".broken");
        try {
            Files.copy(file.toPath(), backup, StandardCopyOption.REPLACE_EXISTING);
            Constants.LOG.error("A copy of the unreadable file was saved to {}", backup);
        } catch (IOException ex) {
            Constants.LOG.error("Failed to back up unreadable menu.json", ex);
        }
    }

    public static void save(File file) {
        setGson();

        JsonObject object = new JsonObject();

        for (String category : RadialMenu.getCategories()) {
            JsonObject object1 = new JsonObject();
            MenuItem[] array = RadialMenu.getArray(category);

            for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
                if (array[i] != null) {
                    object1.add(String.valueOf(i), GSON.toJsonTree(array[i]));
                }
            }

            object.add(category, object1);
        }

        // Write to a temporary file first, then replace menu.json, so a failed write never leaves it deleted or half-written
        Path target = file.toPath();
        Path temp = target.resolveSibling(file.getName() + ".tmp");
        try {
            Files.createDirectories(target.getParent());
            try (Writer writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
                GSON.toJson(object, writer);
            }
            try {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException atomicFailed) {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            Constants.LOG.error("Failed to save menu.json!", ex);
        }
    }
}