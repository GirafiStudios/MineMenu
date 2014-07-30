package dmillerw.menu.data.click;

import dmillerw.menu.data.menu.RadialMenu;

/**
 * @author dmillerw
 */
public class CategoryClickAction implements IClickAction {

    public final String category;

    public CategoryClickAction(String category) {
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
