package dmillerw.menu.data.json;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dmillerw.menu.handler.LogHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.Map;

public class ItemStackSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.add("stack", new JsonPrimitive(String.valueOf(src.write(new NBTTagCompound()))));

        return object;
    }

    @Override
    @Nonnull
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) {
            return ItemStack.EMPTY;
        }
        NBTTagCompound stackTag = null;

        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
            String key = entry.getKey();
            JsonElement element = entry.getValue();

            if (key.equals("stack")) {
                try {
                    stackTag = JsonToNBT.getTagFromJson(element.getAsString());
                } catch (CommandSyntaxException e) {
                    LogHandler.error(e);
                }
            }
        }

        return stackTag == null ? ItemStack.EMPTY : ItemStack.read(stackTag);
    }
}