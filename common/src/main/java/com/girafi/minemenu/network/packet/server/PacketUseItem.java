package com.girafi.minemenu.network.packet.server;

import commonnetwork.Constants;
import commonnetwork.networking.data.PacketContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class PacketUseItem {
    public static final ResourceLocation CHANNEL = new ResourceLocation(Constants.MOD_ID, "packet_use_item");
    private int slot;

    public PacketUseItem(int slot) {
        this.slot = slot;
    }

    public static void encode(PacketUseItem pingPacket, FriendlyByteBuf buf) {
        buf.writeInt(pingPacket.slot);
    }

    public static PacketUseItem decode(FriendlyByteBuf buf) {
        return new PacketUseItem(buf.readInt());
    }

    public static void handle(PacketContext<PacketUseItem> ctx) {
        ServerPlayer player = ctx.sender();
        if (player != null) {
            ItemStack slotStack = player.getInventory().getItem(ctx.message().slot);
            ItemStack heldSaved = player.getMainHandItem();
            InteractionHand hand = InteractionHand.MAIN_HAND;
            EquipmentSlot slot = getSlotFromHand(hand);

            player.setItemSlot(slot, slotStack);
            ItemStack heldItem = player.getItemInHand(hand);
            InteractionResultHolder<ItemStack> useStack = heldItem.use(player.level(), player, hand);
            if (useStack.getResult() == InteractionResult.SUCCESS) {
                player.getInventory().items.set(ctx.message().slot, useStack.getObject());
            }
            player.setItemSlot(slot, heldSaved);

            player.inventoryMenu.sendAllDataToRemote();
        }
    }

    private static EquipmentSlot getSlotFromHand(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
    }
}