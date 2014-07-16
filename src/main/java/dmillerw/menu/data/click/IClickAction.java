package dmillerw.menu.data.click;

import dmillerw.menu.data.ClickAction;

/**
 * @author dmillerw
 */
public interface IClickAction {

	public ClickAction getClickAction();

	public void onClicked();
}
