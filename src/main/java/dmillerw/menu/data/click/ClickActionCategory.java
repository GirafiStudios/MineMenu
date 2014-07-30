package dmillerw.menu.data.click;

import dmillerw.menu.data.menu.RadialMenu;

/**
 * @author dmillerw
 */
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
    public boolean onClicked() {
        RadialMenu.currentCategory = category;
        return false;
    }
}
