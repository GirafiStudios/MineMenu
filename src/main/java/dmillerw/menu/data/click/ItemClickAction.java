package dmillerw.menu.data.click;

import dmillerw.menu.network.PacketHandler;
import dmillerw.menu.network.packet.server.PacketUseItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * @author dmillerw
 */
public class ItemClickAction implements IClickAction {

    public final ItemStack item;

    public ItemClickAction(ItemStack item) {
        this.item = item;
    }

    @Override
    public ClickAction getClickAction() {
        return ClickAction.ITEM_USE;
    }

    @Override
    public void onClicked() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        for (int i=0; i<player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);

            if (stack != null && this.item.isItemEqual(stack)) {
                boolean client = true;

                // Basic hack for allowing activatable sigils to be used without issue
                // DAMN YOU WayOfTime >:(
                Class<?> bloodMagic = null;
                try {
                    bloodMagic = Class.forName("WayofTime.alchemicalWizardry.common.items.EnergyItems");
                } catch (ClassNotFoundException e) {}
                if (bloodMagic != null && bloodMagic.isAssignableFrom(stack.getItem().getClass())) {
                    client = false;
                }

                if (client) {
                    stack.useItemRightClick(player.worldObj, player);
                }

                PacketHandler.INSTANCE.sendToServer(new PacketUseItem(i));
            }
        }
    }
}
