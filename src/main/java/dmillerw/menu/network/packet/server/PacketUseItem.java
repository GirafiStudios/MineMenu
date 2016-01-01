package dmillerw.menu.network.packet.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author dmillerw
 */
public class PacketUseItem implements IMessage, IMessageHandler<PacketUseItem, IMessage> {

    public int slot;

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
        ItemStack stack = player.inventory.getStackInSlot(message.slot);
        if (stack != null) {
            ItemStack backup = player.inventory.getCurrentItem();
            if (backup != null) {
                backup = backup.copy();
            }

            if (player.theItemInWorldManager.tryUseItem(player, player.worldObj, stack)) {
                // tryUseItem is hardcoded to set the CURRENT slot to the resulting item, so we reverse that
                ItemStack current = player.inventory.getCurrentItem();
                if (current != null) {
                    current = current.copy();
                }

                player.inventory.setInventorySlotContents(message.slot, current);
                player.inventory.setInventorySlotContents(player.inventory.currentItem, backup);

                // Send updated inventory
                player.sendContainerToPlayer(player.inventoryContainer);
            }
        }
        return null;
    }
}
