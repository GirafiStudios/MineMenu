package com.girafi.minemenu.data.json;

import com.girafi.minemenu.Constants;
import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.Map;

public class ItemStackSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.add("stack", new JsonPrimitive(String.valueOf(src.save(Minecraft.getInstance().player.registryAccess()))));

        return object;
    }

    @Override
    @Nonnull
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) {
            return ItemStack.EMPTY;
        }
        CompoundTag stackTag = null;

        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
            String key = entry.getKey();
            JsonElement element = entry.getValue();

            if (key.equals("stack")) {
                try {
                    stackTag = TagParser.parseTag(element.getAsString());
                } catch (CommandSyntaxException e) {
                    Constants.LOG.error(e);
                }
            }
        }

        return stackTag == null ? ItemStack.EMPTY : ItemStack.parse(Minecraft.getInstance().player.registryAccess(), stackTag).orElse(ItemStack.EMPTY);
    }
}