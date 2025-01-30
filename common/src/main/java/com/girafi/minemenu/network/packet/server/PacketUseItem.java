package com.girafi.minemenu.network.packet.server;

import com.girafi.minemenu.Constants;
import commonnetwork.networking.data.PacketContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class PacketUseItem {
    public static final ResourceLocation CHANNEL = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "packet_use_item");
    private int slot;

    public PacketUseItem(int slot) {
        this.slot = slot;
    }

    public static void encode(PacketUseItem pingPacket, FriendlyByteBuf buf) {
        buf.writeInt(pingPacket.slot);
    }

    public static PacketUseItem decode(FriendlyByteBuf buf) {
        return new PacketUseItem(buf.readInt());
    }

    public static void handle(PacketContext<PacketUseItem> ctx) {
        ServerPlayer player = ctx.sender();
        ItemStack slotStack = player.getInventory().getItem(ctx.message().slot);
        ItemStack heldSaved = player.getMainHandItem();
        InteractionHand hand = InteractionHand.MAIN_HAND;
        EquipmentSlot slot = getSlotFromHand(hand);

        player.setItemSlot(slot, slotStack);
        ItemStack heldItem = player.getItemInHand(hand);

        UseOnContext useOnContext = new UseOnContext(player, hand, getPlayerPOVHitResult(player.level(), player, ClipContext.Fluid.SOURCE_ONLY));
        if (heldItem.useOn(useOnContext) == InteractionResult.PASS) { //useOn check here, also does the actual interaction
            InteractionResult use = heldItem.use(player.level(), player, hand);
            if (use.consumesAction()) {
                if (heldItem.getItem() instanceof BucketItem) {
                    player.getInventory().items.set(ctx.message().slot, new ItemStack(Items.BUCKET));
                }
            }
        }

        player.setItemSlot(slot, heldSaved); //Sets the heldStack to what it was before something was used, to prevent losing whatever the play have in their hand

        player.inventoryMenu.sendAllDataToRemote();
    }

    public static BlockHitResult getPlayerPOVHitResult(Level p_41436_, Player p_41437_, ClipContext.Fluid p_41438_) {
        float f = p_41437_.getXRot();
        float f1 = p_41437_.getYRot();
        Vec3 vec3 = p_41437_.getEyePosition();
        float f2 = Mth.cos(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f3 = Mth.sin(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f4 = -Mth.cos(-f * ((float) Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float) Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = 4.5D; //Workaround, since no getter for reach?
        Vec3 vec31 = vec3.add((double) f6 * d0, (double) f5 * d0, (double) f7 * d0);
        return p_41436_.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, p_41438_, p_41437_));
    }

    public static EquipmentSlot getSlotFromHand(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
    }
}