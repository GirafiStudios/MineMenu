package dmillerw.menu.data.json;

import com.google.gson.*;
import dmillerw.menu.data.click.CommandClickAction;
import dmillerw.menu.data.click.IClickAction;
import dmillerw.menu.data.click.KeyClickAction;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author dmillerw
 */
public class ClickActionSerializer implements JsonSerializer<IClickAction>, JsonDeserializer<IClickAction> {

	@Override
	public JsonElement serialize(IClickAction src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();

		if (src instanceof CommandClickAction) {
			object.add("command", new JsonPrimitive(((CommandClickAction)src).command));
		} else if (src instanceof KeyClickAction) {
			object.add("key", new JsonPrimitive(((KeyClickAction)src).key));
		}

		return object;
	}

	@Override
	public IClickAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		if (!json.isJsonObject()) {
			return null;
		}

		for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
			String key = entry.getKey();
			JsonElement element = entry.getValue();

			if (key.equals("command")) {
				return new CommandClickAction(element.getAsString());
			} else if (key.equals("key")) {
				return new KeyClickAction(element.getAsString());
			}
		}

		return null;
	}
}
