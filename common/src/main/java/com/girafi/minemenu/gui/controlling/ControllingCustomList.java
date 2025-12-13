package com.girafi.minemenu.gui.controlling;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ControllingCustomList extends ContainerObjectSelectionList<KeyBindsList.Entry> {
    public List<KeyBindsList.Entry> allEntries;

    public ControllingCustomList(Minecraft mc, int width, int height, int y, int itemHeight) {
        super(mc, width, height, y, itemHeight);
    }

    public List<KeyBindsList.Entry> getAllEntries() {
        return allEntries;
    }

    @Override
    public void sort(Comparator<KeyBindsList.Entry> comp) {
        super.sort(comp);
    }

    @Override
    protected int addEntry(KeyBindsList.Entry ent) {
        if(allEntries == null) {
            allEntries = new ArrayList<>();
        }
        allEntries.add(ent);
        return addEntryInternal(ent);
    }

    public int addEntryInternal(KeyBindsList.Entry ent) {
        return super.addEntry(ent);
    }
}
