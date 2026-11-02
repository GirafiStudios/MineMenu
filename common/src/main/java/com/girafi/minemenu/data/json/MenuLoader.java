package com.girafi.minemenu.data.json;

import com.girafi.minemenu.Constants;
import com.girafi.minemenu.data.click.ClickAction;
import com.girafi.minemenu.data.click.ClickActionCommand;
import com.girafi.minemenu.data.click.ClickActionKey;
import com.girafi.minemenu.data.menu.Category;
import com.girafi.minemenu.data.menu.MenuItem;
import com.girafi.minemenu.data.menu.RadialMenu;
import com.google.gson.*;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nullable;
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
    private static final String SETTINGS_KEY = "settings";
    private static final String MENU_COLOR_KEY = "menuColor";
    private static final String SELECT_COLOR_KEY = "selectColor";
    private static Gson GSON;

    public static void setGson(RegistryAccess registryAccess) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.registerTypeAdapter(ItemStack.class, new ItemStackSerializer(registryAccess));
        builder.registerTypeAdapter(ClickAction.IClickAction.class, new ClickActionSerializer());
        GSON = builder.create();
    }

    public static void load(RegistryAccess registryAccess, File file) {
        setGson(registryAccess);
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
            Category category = RadialMenu.getCategory(entry.getKey());
            MenuItem[] array = category.items;

            if (!entry.getValue().isJsonObject()) {
                Constants.LOG.error(String.format("Failed to load %s category! Improperly formatted!", entry.getKey()));
                continue;
            }

            category.menuColor = null;
            category.selectColor = null;

            for (Map.Entry<String, JsonElement> entry1 : entry.getValue().getAsJsonObject().entrySet()) {
                String key = entry1.getKey();
                JsonElement data = entry1.getValue();

                if (key.equals(SETTINGS_KEY)) {
                    loadSettings(entry.getKey(), category, data);
                    continue;
                }

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
        }
    }

    private static void loadSettings(String categoryName, Category category, JsonElement data) {
        if (!data.isJsonObject()) {
            Constants.LOG.warn("Settings for category {} are improperly formatted. Ignoring them.", categoryName);
            return;
        }
        JsonObject settings = data.getAsJsonObject();
        category.menuColor = parseColor(categoryName, settings, MENU_COLOR_KEY);
        category.selectColor = parseColor(categoryName, settings, SELECT_COLOR_KEY);
    }

    @Nullable
    private static Integer parseColor(String categoryName, JsonObject settings, String key) {
        if (!settings.has(key)) {
            return null;
        }
        try {
            String hex = settings.get(key).getAsString().trim();
            if (hex.startsWith("#")) {
                hex = hex.substring(1);
            }
            if (hex.length() == 6) {
                return 0xFF000000 | Integer.parseUnsignedInt(hex, 16);
            } else if (hex.length() == 8) {
                return Integer.parseUnsignedInt(hex, 16);
            }
        } catch (RuntimeException ignored) {
        }
        Constants.LOG.warn("Invalid {} for category {}. Expected #AARRGGBB. Using the default color.", key, categoryName);
        return null;
    }

    private static String formatColor(int color) {
        return String.format("#%08X", color);
    }


    private static void backupBrokenFile(File file) {
        Path backup = file.toPath().resolveSibling(file.getName() + ".broken");
        try {
            Files.copy(file.toPath(), backup, StandardCopyOption.REPLACE_EXISTING);
            Constants.LOG.error("A copy of the unreadable file was saved to {}", backup);
        } catch (IOException ex) {
            Constants.LOG.error("Failed to back up unreadable menu.json", ex);
        }
    }

    public static void save(RegistryAccess registryAccess, File file) {
        setGson(registryAccess);

        JsonObject object = new JsonObject();

        for (String categoryName : RadialMenu.getCategories()) {
            JsonObject object1 = new JsonObject();
            Category category = RadialMenu.getCategory(categoryName);
            MenuItem[] array = category.items;

            if (category.hasCustomColors()) {
                JsonObject settings = new JsonObject();
                if (category.menuColor != null) {
                    settings.addProperty(MENU_COLOR_KEY, formatColor(category.menuColor));
                }
                if (category.selectColor != null) {
                    settings.addProperty(SELECT_COLOR_KEY, formatColor(category.selectColor));
                }
                object1.add(SETTINGS_KEY, settings);
            }

            for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
                if (array[i] != null) {
                    object1.add(String.valueOf(i), GSON.toJsonTree(array[i]));
                }
            }

            object.add(categoryName, object1);
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