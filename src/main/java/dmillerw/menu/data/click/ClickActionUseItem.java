package dmillerw.menu.data.click;

import dmillerw.menu.network.PacketHandler;
import dmillerw.menu.network.packet.server.PacketUseItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

/**
 * @author dmillerw
 */
public class ClickActionUseItem implements ClickAction.IClickAction {
    public final ItemStack stack;

    public ClickActionUseItem(ItemStack item) {
        this.stack = item;
    }

    @Override
    public ClickAction getClickAction() {
        return ClickAction.ITEM_USE;
    }

    @Override
    public boolean onClicked() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;

        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);

            if (this.stack.isItemEqual(stack)) {
                for (EnumHand hand : EnumHand.values()) {
                    stack.useItemRightClick(player.worldObj, player, hand);
                }
                PacketHandler.INSTANCE.sendToServer(new PacketUseItem(i, stack));
            }
        }
        return false;
    }

    @Override
    public void onRemoved() {
    }
}