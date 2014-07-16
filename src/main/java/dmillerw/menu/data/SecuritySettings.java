package dmillerw.menu.data;

import dmillerw.menu.handler.LogHandler;

/**
 * @author dmillerw
 */
public class SecuritySettings {

	public static boolean allowCommands = true;
	public static boolean allowKeybinds = true;
	public static boolean allowItemUsage = true;

	public static void restoreDefaults() {
		LogHandler.info("CLIENT: Disconnected from server, restoring default security settings");

		allowCommands = true;
		allowKeybinds = true;
		allowItemUsage = true;
	}
}
