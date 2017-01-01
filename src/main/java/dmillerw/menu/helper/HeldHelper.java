package dmillerw.menu.helper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class HeldHelper {
    public static EntityEquipmentSlot getSlotFromHand(EnumHand hand) {
        return hand == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND;
    }

    public static ItemStack getStackFromHand(EntityPlayer player, EnumHand hand) {
        if (hand == EnumHand.MAIN_HAND)
            return player.getHeldItemMainhand();
        if (hand == EnumHand.OFF_HAND)
            return player.getHeldItemOffhand();

        // Null yo
        return null;
    }
}