package dmillerw.menu.data.session;

import dmillerw.menu.data.click.ClickAction;
import dmillerw.menu.handler.LogHandler;

import java.util.EnumSet;

/**
 * @author dmillerw
 */
public class ActionSessionData {

    public static EnumSet<ClickAction> availableActions = EnumSet.noneOf(ClickAction.class);

    public static void activateAll() {
        availableActions.clear();
        availableActions.addAll(ClickAction.getValues());
    }

    public static void activateClientValues() {
        availableActions.clear();
        availableActions.addAll(ClickAction.getClientValues());
    }

    public static void restoreDefaults() {
        LogHandler.info("CLIENT: Disconnected from server, restoring default security settings");
    }
}
