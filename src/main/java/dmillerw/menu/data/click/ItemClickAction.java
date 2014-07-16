package dmillerw.menu.data.click;

import dmillerw.menu.data.ClickAction;
import dmillerw.menu.network.PacketHandler;
import dmillerw.menu.network.packet.server.PacketUseItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * @author dmillerw
 */
public class ItemClickAction implements IClickAction {

	public final int slot;

	public ItemClickAction(int slot) {
		this.slot = 0;
	}

	@Override
	public ClickAction getClickAction() {
		return ClickAction.ITEM_USE;
	}

	@Override
	public void onClicked() {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		ItemStack stack = player.inventory.getStackInSlot(slot);

		if (stack != null) {
			stack.useItemRightClick(player.worldObj, player);
			PacketHandler.INSTANCE.sendToServer(new PacketUseItem(slot));
		}
	}
}
