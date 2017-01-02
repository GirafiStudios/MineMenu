package dmillerw.menu.helper;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.EnumHand;

public class HeldHelper {

    public static EntityEquipmentSlot getSlotFromHand(EnumHand hand) {
        return hand == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND;
    }
}