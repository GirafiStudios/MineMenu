package dmillerw.menu.network.packet.server;

import dmillerw.menu.helper.HeldHelper;
import dmillerw.menu.network.packet.Packet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;

public class PacketUseItem extends Packet<PacketUseItem> {
    private int slot;

    public PacketUseItem() {
    }

    public PacketUseItem(int slot) {
        this.slot = slot;
    }

    @Override
    protected void handleClientSide(EntityPlayer player) {
    }

    @Override
    protected void handleServerSide(EntityPlayerMP player) {
        ItemStack slotStack = player.inventory.getStackInSlot(this.slot);
        ItemStack heldSaved = player.getHeldItemMainhand();
        EnumHand hand = EnumHand.MAIN_HAND;
        EntityEquipmentSlot slot = HeldHelper.getSlotFromHand(hand);

        player.setItemStackToSlot(slot, slotStack);
        ItemStack heldItem = player.getHeldItem(hand);
        ActionResult<ItemStack> useStack = heldItem.useItemRightClick(player.world, player, hand);
        if (useStack.getType() == EnumActionResult.SUCCESS) {
            player.inventory.mainInventory.set(this.slot, useStack.getResult());
        }
        player.setItemStackToSlot(slot, heldSaved);

        player.sendContainerToPlayer(player.inventoryContainer);
    }

    @Override
    public void toBytes(PacketBuffer buffer) {
        buffer.writeInt(slot);
    }

    @Override
    public void fromBytes(PacketBuffer buffer) {
        slot = buffer.readInt();
    }
}