package dmillerw.menu.network.packet.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUseItem {
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

    public static class Handler {
        public static void handle(PacketUseItem message, Supplier<NetworkEvent.Context> ctx) {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                ItemStack slotStack = player.getInventory().getItem(message.slot);
                ItemStack heldSaved = player.getMainHandItem();
                InteractionHand hand = InteractionHand.MAIN_HAND;
                EquipmentSlot slot = getSlotFromHand(hand);

                player.setItemSlot(slot, slotStack);
                ItemStack heldItem = player.getItemInHand(hand);
                InteractionResultHolder<ItemStack> useStack = heldItem.use(player.level(), player, hand);
                if (useStack.getResult() == InteractionResult.SUCCESS) {
                    player.getInventory().items.set(message.slot, useStack.getObject());
                }
                player.setItemSlot(slot, heldSaved);

                player.inventoryMenu.sendAllDataToRemote();

                ctx.get().setPacketHandled(true);
            }
        }
    }

    private static EquipmentSlot getSlotFromHand(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
    }
}