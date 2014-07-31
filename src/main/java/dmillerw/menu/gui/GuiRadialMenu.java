package dmillerw.menu.gui;

import dmillerw.menu.data.menu.MenuItem;
import dmillerw.menu.data.menu.RadialMenu;
import dmillerw.menu.data.session.ActionSessionData;
import dmillerw.menu.gui.menu.GuiMenuItem;
import dmillerw.menu.handler.ClientTickHandler;
import dmillerw.menu.helper.AngleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

/**
 * @author dmillerw
 */
public class GuiRadialMenu extends GuiScreen {

    public static final GuiRadialMenu INSTANCE = new GuiRadialMenu();

    public static boolean active = false;

    public static void activate() {
        if (Minecraft.getMinecraft().currentScreen == null) {
            active = true;
            Minecraft.getMinecraft().displayGuiScreen(INSTANCE);
        }
    }

    public static void deactivate() {
        active = false;
        if (Minecraft.getMinecraft().currentScreen == INSTANCE) {
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (active && RadialMenu.animationTimer == 0) {
            double mouseAngle = AngleHelper.getMouseAngle();
            mouseAngle -= ClientTickHandler.ANGLE_PER_ITEM / 2;
            mouseAngle = 360 - mouseAngle;
            mouseAngle = AngleHelper.correctAngle(mouseAngle);

            if (!mc.gameSettings.hideGUI) {
                for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
                    double currAngle = ClientTickHandler.ANGLE_PER_ITEM * i;
                    double nextAngle = currAngle + ClientTickHandler.ANGLE_PER_ITEM;
                    currAngle = AngleHelper.correctAngle(currAngle);
                    nextAngle = AngleHelper.correctAngle(nextAngle);

                    boolean mouseIn = mouseAngle > currAngle && mouseAngle < nextAngle;

                    if (mouseIn) {
                        MenuItem item = RadialMenu.getActiveArray()[i];
                        boolean disabled = item != null && !ActionSessionData.availableActions.contains(item.clickAction.getClickAction());

                        if (item != null && item.clickAction != null) {
                            if (isShiftKeyDown()) {
                                deactivate();
                                GuiStack.push(new GuiMenuItem(i, item));
                                return;
                            } else {
                                if (!disabled) {
                                    if (item.clickAction.onClicked()) {
                                        deactivate();
                                        return;
                                    }
                                }
                            }
                        } else {
                            deactivate();
                            GuiStack.push(new GuiMenuItem(i, item));
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
