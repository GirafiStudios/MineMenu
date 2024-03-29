package com.girafi.minemenu.data.click;

import com.girafi.minemenu.network.packet.server.PacketUseItem;
import commonnetwork.api.Dispatcher;
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

                if (!stack.isEmpty() && ItemStack.isSameItem(this.stack, stack)) {
                    stack.use(player.level(), player, InteractionHand.MAIN_HAND);
                    Dispatcher.sendToServer(new PacketUseItem(i));
                }
            }
        }
    }

    @Override
    public void onRemoved() {
    }
}