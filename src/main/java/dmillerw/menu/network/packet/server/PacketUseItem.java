package dmillerw.menu.network.packet.server;

import dmillerw.menu.helper.HeldHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUseItem {
    private int slot;

    public PacketUseItem(int slot) {
        this.slot = slot;
    }

    public static void encode(PacketUseItem pingPacket, PacketBuffer buf) {
        buf.writeInt(pingPacket.slot);
    }

    public static PacketUseItem decode(PacketBuffer buf) {
        return new PacketUseItem(buf.readInt());
    }

    public static class Handler {
        public static void handle(PacketUseItem message, Supplier<NetworkEvent.Context> ctx) {
            EntityPlayerMP player = ctx.get().getSender();
            if (player != null) {
                ItemStack slotStack = player.inventory.getStackInSlot(message.slot);
                ItemStack held = player.getHeldItemMainhand();
                EnumHand hand = EnumHand.MAIN_HAND;
                EntityEquipmentSlot slot = HeldHelper.getSlotFromHand(hand);

                player.setItemStackToSlot(slot, slotStack);
                ItemStack heldItem = player.getHeldItem(hand);
                if (!heldItem.isEmpty()) {
                    heldItem.useItemRightClick(player.world, player, hand).getResult();
                }
                player.setItemStackToSlot(slot, held);

                player.sendContainerToPlayer(player.inventoryContainer);

                ctx.get().setPacketHandled(true);
            }
        }
    }
}