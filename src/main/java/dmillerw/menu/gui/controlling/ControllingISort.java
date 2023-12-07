package dmillerw.menu.gui.controlling;

import net.minecraft.client.gui.screens.controls.KeyBindsList;

import java.util.List;

/**
 * Copy of Controlling by Jaredlll08's ISort, but with a different KeyEntry
 * Temporary workaround, until an interface for the CategoryEntry and KeyEntry in Controlling can be implemented in 1.20.3
 */
@Deprecated
public interface ControllingISort {
    void sort(List<KeyBindsList.Entry> entries);
}