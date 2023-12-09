//package com.girafi.minemenu.gui.controlling;
//
//import com.blamejared.controlling.ControllingConstants;
//import net.minecraft.client.gui.screens.controls.KeyBindsList;
//import net.minecraft.network.chat.Component;
//
//import java.util.Comparator;
//import java.util.List;
//
///**
// * Copy of Controlling by Jaredlll08's SortOrder, but with a different KeyEntry
// * Temporary workaround, until an interface for the CategoryEntry and KeyEntry in Controlling can be implemented in 1.20.3
// */
//@Deprecated
//public enum ControllingSortOrder {
//    NONE("options.sortNone", entries -> {}),
//    AZ("options.sortAZ", entries -> entries.sort(Comparator.comparing(o -> ((ControllingGuiControlList.KeyEntry) o).getKeyDesc().getString()))),
//    ZA("options.sortZA", entries -> entries.sort(Comparator.comparing(o -> ((ControllingGuiControlList.KeyEntry) o).getKeyDesc().getString()).reversed()));
//
//    private final ControllingISort sorter;
//    private final Component display;
//
//    ControllingSortOrder(String key, ControllingISort sorter) {
//        this.sorter = sorter;
//        this.display = ControllingConstants.COMPONENT_OPTIONS_SORT.copy().append(": ").append(Component.translatable(key));
//    }
//
//    public ControllingSortOrder cycle() {
//        return ControllingSortOrder.values()[(this.ordinal() + 1) % ControllingSortOrder.values().length];
//    }
//
//    public void sort(List<KeyBindsList.Entry> list) {
//        list.removeIf(entry -> !(entry instanceof ControllingGuiControlList.KeyEntry));
//        this.sorter.sort(list);
//    }
//
//    public Component getDisplay() {
//        return this.display;
//    }
//}