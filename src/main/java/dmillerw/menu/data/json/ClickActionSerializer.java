package dmillerw.menu.data.json;

import com.google.gson.*;
import dmillerw.menu.data.click.*;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Type;
import java.util.Map;

public class ClickActionSerializer implements JsonSerializer<ClickAction.IClickAction>, JsonDeserializer<ClickAction.IClickAction> {

    @Override
    public JsonElement serialize(ClickAction.IClickAction src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        if (src instanceof ClickActionCommand) {
            JsonObject commandObject = new JsonObject();
            commandObject.add("command", new JsonPrimitive(((ClickActionCommand) src).command));
            commandObject.add("clipboard", new JsonPrimitive(((ClickActionCommand) src).clipboard));
            object.add("command", commandObject);
        } else if (src instanceof ClickActionKey) {
            JsonObject keyObject = new JsonObject();
            keyObject.add("key", new JsonPrimitive(((ClickActionKey) src).key));
            keyObject.add("toggle", new JsonPrimitive(((ClickActionKey) src).toggle));
            object.add("key", keyObject);
        } else if (src instanceof ClickActionUseItem) {
            object.add("item", context.serialize(((ClickActionUseItem) src).stack));
        } else if (src instanceof ClickActionCategory) {
            object.add("category", new JsonPrimitive(((ClickActionCategory) src).category));
        }
        return object;
    }

    @Override
    public ClickAction.IClickAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) {
            return null;
        }

        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
            String key = entry.getKey();
            JsonElement element = entry.getValue();

            switch (key) {
                case "command":
                    if (element.isJsonPrimitive()) {
                        return new ClickActionCommand(element.getAsString(), false);
                    } else {
                        String command = "";
                        boolean clipboard = false;

                        for (Map.Entry<String, JsonElement> entry1 : element.getAsJsonObject().entrySet()) {
                            if (entry1.getKey().equals("command")) {
                                command = entry1.getValue().getAsString();
                            } else if (entry1.getKey().equals("clipboard")) {
                                clipboard = entry1.getValue().getAsBoolean();
                            }
                        }
                        return new ClickActionCommand(command, clipboard);
                    }
                case "key":
                    if (element.isJsonPrimitive()) {
                        return new ClickActionKey(element.getAsString(), false);
                    } else {
                        String keybind = "";
                        boolean toggle = false;

                        for (Map.Entry<String, JsonElement> entry1 : element.getAsJsonObject().entrySet()) {
                            if (entry1.getKey().equals("key")) {
                                keybind = entry1.getValue().getAsString();
                            } else if (entry1.getKey().equals("toggle")) {
                                toggle = entry1.getValue().getAsBoolean();
                            }
                        }
                        return new ClickActionKey(keybind, toggle);
                    }
                case "item":
                    return new ClickActionUseItem(context.deserialize(element, ItemStack.class));
                case "category":
                    return new ClickActionCategory(element.getAsString());
            }
        }
        return null;
    }
}