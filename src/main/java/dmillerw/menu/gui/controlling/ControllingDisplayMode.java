package dmillerw.menu.gui.controlling;

import com.blamejared.controlling.mixin.AccessKeyMapping;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.controls.KeyBindsList;

import java.util.function.Predicate;

/**
 * Copy of Controlling by Jaredlll08's ControllingDisplayMode, but with a different KeyEntry
 * Temporary workaround, until an interface for the CategoryEntry and KeyEntry in Controlling can be implemented in 1.20.3
 */
@Deprecated
public enum ControllingDisplayMode {
    ALL(keyEntry -> true), NONE(keyEntry -> keyEntry.getKey().isUnbound()), CONFLICTING(keyEntry -> {

        for(KeyMapping key : Minecraft.getInstance().options.keyMappings) {
            if(!key.getName().equals(keyEntry.getKey().getName()) && !key.isUnbound()) {
                if(((AccessKeyMapping) key).controlling$getKey()
                        .getValue() == ((AccessKeyMapping) keyEntry.getKey()).controlling$getKey().getValue()) {
                    return true;
                }
            }
        }
        System.out.println("Hi");
        return false;
    });


    private final Predicate<ControllingGuiControlList.KeyEntry> predicate;

    ControllingDisplayMode(Predicate<ControllingGuiControlList.KeyEntry> predicate) {
        this.predicate = predicate;
    }

    public Predicate<KeyBindsList.Entry> getPredicate() {
        return entry -> entry instanceof ControllingGuiControlList.KeyEntry keyEntry && predicate.test(keyEntry);
    }
}
