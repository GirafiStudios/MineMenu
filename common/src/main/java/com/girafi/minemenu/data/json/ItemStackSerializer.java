package com.girafi.minemenu.data.json;

import com.girafi.minemenu.Constants;
import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DataResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.Map;

public class ItemStackSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    @Override
    public JsonElement serialize(ItemStack stack, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        DataResult<Tag> dataResult = ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, stack);

        dataResult.result().ifPresent(tag -> {
            object.add("stack", new JsonPrimitive(String.valueOf(tag)));
        });

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
                    stackTag = TagParser.parseCompoundFully (element.getAsString());
                } catch (CommandSyntaxException e) {
                    Constants.LOG.error(e);
                }
            }
        }

        return stackTag == null ? ItemStack.EMPTY : ItemStack.CODEC.parse(NbtOps.INSTANCE, stackTag).result().orElse(ItemStack.EMPTY);
    }
}