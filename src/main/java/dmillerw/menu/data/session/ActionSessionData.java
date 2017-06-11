package dmillerw.menu.data.session;

import dmillerw.menu.data.click.ClickAction;
import dmillerw.menu.handler.LogHandler;

import java.util.EnumSet;

public class ActionSessionData {
    public static final EnumSet<ClickAction> AVAILABLE_ACTIONS = EnumSet.noneOf(ClickAction.class);

    public static void activateAll() {
        AVAILABLE_ACTIONS.clear();
        AVAILABLE_ACTIONS.addAll(ClickAction.getValues());
    }

    public static void activateClientValues() {
        AVAILABLE_ACTIONS.clear();
        AVAILABLE_ACTIONS.addAll(ClickAction.getClientValues());
    }

    public static void restoreDefaults() {
        LogHandler.info("CLIENT: Disconnected from server, restoring default security settings");
        activateAll();
    }
}