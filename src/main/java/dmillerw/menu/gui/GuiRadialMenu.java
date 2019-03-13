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
    public void mouseClicked(int mouseX, int mouseY, int button) {
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
                            if (isShiftKeyDown() || (ConfigHandler.rightClickToEdit && button == 1)) {
                                deactivate();
                                GuiStack.push(new GuiMenuItem(i, menuItem));
                                return;
                            } else {
                                if (!disabled && button == 0) {
                                    if (menuItem.clickAction.deactivates()) {
                                	deactivate();
                                	menuItem.clickAction.onClicked();
                                	return;
                                    }
                                    
                                    menuItem.clickAction.onClicked();
                                }
                            }
                        } else {
                            if (button == 0) {
                                deactivate();
                                GuiStack.push(new GuiMenuItem(i, menuItem));
                                return;
                            }
                        }
                    }
                }
            }
        }
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