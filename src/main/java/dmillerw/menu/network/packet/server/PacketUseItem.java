package dmillerw.menu.network.packet.server;

import dmillerw.menu.network.packet.Packet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHand;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author dmillerw
 */
public class PacketUseItem extends Packet<PacketUseItem> {
    private int slot;
    private ItemStack stack;

    public PacketUseItem() {
    }

    public PacketUseItem(int slot, @Nonnull ItemStack stack) {
        this.slot = slot;
        this.stack = stack;
    }

    @Override
    protected void handleClientSide(EntityPlayer player) {
    }

    @Override
    protected void handleServerSide(EntityPlayer player) {
        ItemStack stack = player.inventory.getStackInSlot(slot);

        for (EnumHand hand : EnumHand.values()) {
            stack.useItemRightClick(player.world, player, hand);
        }

        if (!player.isHandActive()) {
            ((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
        }
    }

    @Override
    public void toBytes(PacketBuffer buffer) {
        buffer.writeInt(slot);
        buffer.writeItemStack(stack);
    }

    @Override
    public void fromBytes(PacketBuffer buffer) throws IOException {
        slot = buffer.readInt();
        stack = buffer.readItemStack();
    }
}