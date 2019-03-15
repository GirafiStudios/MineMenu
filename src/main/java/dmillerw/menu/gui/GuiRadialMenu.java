package dmillerw.menu.gui;

import dmillerw.menu.data.menu.MenuItem;
import dmillerw.menu.data.menu.RadialMenu;
import dmillerw.menu.data.session.ActionSessionData;
import dmillerw.menu.gui.menu.GuiMenuItem;
import dmillerw.menu.handler.ClientTickHandler;
import dmillerw.menu.handler.ConfigHandler;
import dmillerw.menu.helper.AngleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class GuiRadialMenu extends GuiScreen {
    public static final GuiRadialMenu INSTANCE = new GuiRadialMenu();
    public static boolean active = false;

    public static void activate() {
        if (Minecraft.getInstance().currentScreen == null) {
            active = true;
            Minecraft.getInstance().displayGuiScreen(INSTANCE);
        }
    }

    public static void deactivate() {
        active = false;
        if (Minecraft.getInstance().currentScreen == INSTANCE) {
            Minecraft.getInstance().displayGuiScreen(null);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
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
                        MenuItem menuItem = RadialMenu.getActiveArray()[i];
                        boolean disabled = menuItem != null && !ActionSessionData.AVAILABLE_ACTIONS.contains(menuItem.clickAction.getClickAction());

                        if (menuItem != null) {
                            if (isShiftKeyDown() || (ConfigHandler.GENERAL.rightClickToEdit.get() && button == 1)) {
                                deactivate();
                                GuiStack.push(new GuiMenuItem(i, menuItem));
                                return true;
                            } else {
                                if (!disabled && button == 0) {
                                    if (menuItem.clickAction.deactivates()) {
                                        deactivate();
                                        menuItem.clickAction.onClicked();
                                        return true;
                                    }
                                }
                            }
                        } else {
                            if (button == 0) {
                                deactivate();
                                GuiStack.push(new GuiMenuItem(i, menuItem));
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        active = false;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}