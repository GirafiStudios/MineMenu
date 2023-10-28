package dmillerw.menu.data.click;

import dmillerw.menu.network.PacketHandler;
import dmillerw.menu.network.packet.server.PacketUseItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

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
        LocalPlayer player = mc.player;

        if (player != null) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);

                if (!stack.isEmpty() && this.stack.sameItem(stack)) {
                    stack.use(player.level, player, InteractionHand.MAIN_HAND);
                    PacketHandler.CHANNEL.sendToServer(new PacketUseItem(i));
                }
            }
        }
    }

    @Override
    public void onRemoved() {
    }
}