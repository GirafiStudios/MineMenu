package dmillerw.menu.data.json;

import com.google.gson.*;
import dmillerw.menu.data.click.*;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author dmillerw
 */
public class ClickActionSerializer implements JsonSerializer<ClickAction.IClickAction>, JsonDeserializer<ClickAction.IClickAction> {

    @Override
    public JsonElement serialize(ClickAction.IClickAction src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        if (src instanceof ClickActionCommand) {
            object.add("command", new JsonPrimitive(((ClickActionCommand) src).command));
        } else if (src instanceof ClickActionKey) {
            object.add("key", new JsonPrimitive(((ClickActionKey) src).key));
        } else if (src instanceof ClickActionUseItem) {
            object.add("item", context.serialize(((ClickActionUseItem) src).item));
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

            if (key.equals("command")) {
                return new ClickActionCommand(element.getAsString());
            } else if (key.equals("key")) {
                return new ClickActionKey(element.getAsString());
            } else if (key.equals("item")) {
                return new ClickActionUseItem((ItemStack) context.deserialize(element, ItemStack.class));
            } else if (key.equals("category")) {
                return new ClickActionCategory(element.getAsString());
            }
        }

        return null;
    }
}
