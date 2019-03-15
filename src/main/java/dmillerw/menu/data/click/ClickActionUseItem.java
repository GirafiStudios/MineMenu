package dmillerw.menu.data.click;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import javax.annotation.Nonnull;

public class ClickActionUseItem implements ClickAction.IClickAction {
    @Nonnull
    public final ItemStack stack;

    public ClickActionUseItem(@Nonnull ItemStack item) {
        this.stack = item;
    }

    @Override
    public ClickAction getClickAction() {
        return ClickAction.ITEM_USE;
    }

    @Override
    public void onClicked() {
        Minecraft mc = Minecraft.getInstance();
        EntityPlayer player = mc.player;

        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);

            if (!stack.isEmpty() && this.stack.isItemEqual(stack)) {
                stack.useItemRightClick(player.world, player, EnumHand.MAIN_HAND);
                //PacketHandler.INSTANCE.sendToServer(new PacketUseItem(i)); //TODO
            }
        }
    }

    @Override
    public void onRemoved() {
    }
}