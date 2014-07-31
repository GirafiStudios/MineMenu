package dmillerw.menu.data.click;

import dmillerw.menu.network.PacketHandler;
import dmillerw.menu.network.packet.server.PacketUseItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * @author dmillerw
 */
public class ClickActionUseItem implements ClickAction.IClickAction {

    public final ItemStack item;

    public ClickActionUseItem(ItemStack item) {
        this.item = item;
    }

    @Override
    public ClickAction getClickAction() {
        return ClickAction.ITEM_USE;
    }

    @Override
    public boolean onClicked() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        for (int i=0; i<player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);

            if (stack != null && this.item.isItemEqual(stack)) {
                stack.useItemRightClick(player.worldObj, player);
                PacketHandler.INSTANCE.sendToServer(new PacketUseItem(i));
            }
        }

        return false;
    }

    @Override
    public void onRemoved() {

    }
}
