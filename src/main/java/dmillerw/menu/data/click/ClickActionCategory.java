package dmillerw.menu.data.click;

import dmillerw.menu.data.menu.RadialMenu;
import dmillerw.menu.gui.GuiRadialMenu;
import net.minecraft.client.Minecraft;

public class ClickActionCategory implements ClickAction.IClickAction {

    public final String category;

    public ClickActionCategory(String category) {
        this.category = category;
    }

    @Override
    public ClickAction getClickAction() {
        return ClickAction.CATEGORY;
    }
    
    @Override
    public boolean deactivates() {
	return false;
    }
    
    @Override
    public void onClicked() {
        RadialMenu.currentCategory = category;
        RadialMenu.resetTimer();
    }

    @Override
    public void onRemoved() {

    }
}