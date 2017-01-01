package dmillerw.menu.network.packet.server;

import dmillerw.menu.helper.HeldHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author dmillerw
 */
public class PacketUseItem implements IMessage, IMessageHandler<PacketUseItem, IMessage> {
    private int slot;

    public PacketUseItem() {
    }

    public PacketUseItem(int slot) {
        this.slot = slot;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slot);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slot = buf.readInt();
    }

    @Override
    public IMessage onMessage(PacketUseItem message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        ItemStack slotStack = player.inventory.getStackInSlot(message.slot);

        ItemStack held = null;
        EnumHand hand = EnumHand.MAIN_HAND;
        EntityEquipmentSlot slot = HeldHelper.getSlotFromHand(hand);
        if (HeldHelper.getStackFromHand(player, hand) != null) {
            held = HeldHelper.getStackFromHand(player, hand);
        }
        player.setItemStackToSlot(slot, slotStack);
        ItemStack heldItem = player.getHeldItem(hand);
        if (heldItem != null) {
            heldItem.useItemRightClick(player.worldObj, player, hand).getResult();
        }
        if (held != null && held.stackSize <= 0) {
            held = null;
        }
        player.setItemStackToSlot(slot, held);

        return null;
    }
}