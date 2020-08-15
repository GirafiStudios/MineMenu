package dmillerw.menu.gui;

import dmillerw.menu.data.menu.MenuItem;
import dmillerw.menu.data.menu.RadialMenu;
import dmillerw.menu.gui.menu.MenuItemScreen;
import dmillerw.menu.handler.ClientTickHandler;
import dmillerw.menu.handler.ConfigHandler;
import dmillerw.menu.helper.AngleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;

public class RadialMenuScreen extends Screen {
    public static final RadialMenuScreen INSTANCE = new RadialMenuScreen();
    public static boolean active = false;

    public RadialMenuScreen() {
        super(new TranslationTextComponent("minemenu.radialMenu.title"));
    }

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

            if (!getMinecraft().gameSettings.hideGUI) {
                for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
                    double currAngle = ClientTickHandler.ANGLE_PER_ITEM * i;
                    double nextAngle = currAngle + ClientTickHandler.ANGLE_PER_ITEM;
                    currAngle = AngleHelper.correctAngle(currAngle);
                    nextAngle = AngleHelper.correctAngle(nextAngle);

                    boolean mouseIn = mouseAngle > currAngle && mouseAngle < nextAngle;

                    if (mouseIn) {
                        MenuItem menuItem = RadialMenu.getActiveArray()[i];

                        if (menuItem != null) {
                            if (hasShiftDown() || (ConfigHandler.GENERAL.rightClickToEdit.get() && button == 1)) {
                                deactivate();
                                ScreenStack.push(new MenuItemScreen(i, menuItem));
                                return true;
                            } else {
                                if (button == 0) {
                                    if (menuItem.clickAction.deactivates()) {
                                        deactivate();
                                        menuItem.clickAction.onClicked();
                                        return true;
                                    } else {
                                        menuItem.clickAction.onClicked();
                                        return true;
                                    }
                                }
                            }
                        } else {
                            if (button == 0) {
                                deactivate();
                                ScreenStack.push(new MenuItemScreen(i, menuItem));
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
    public void onClose() {
        super.onClose();
        active = false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}