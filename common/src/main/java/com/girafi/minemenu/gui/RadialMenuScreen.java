package com.girafi.minemenu.gui;

import com.girafi.minemenu.data.menu.MenuItem;
import com.girafi.minemenu.data.menu.RadialMenu;
import com.girafi.minemenu.handler.ClientTickHelper;
import com.girafi.minemenu.helper.AngleHelper;
import com.girafi.minemenu.menu.MenuItemScreen;
import com.girafi.minemenu.util.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;

public class RadialMenuScreen extends Screen {
    public static final RadialMenuScreen INSTANCE = new RadialMenuScreen();
    public static boolean active = false;

    public RadialMenuScreen() {
        super(Component.translatable("mine_menu.radialMenu.title"));
    }

    public static void activate() {
        if (Minecraft.getInstance().screen == null) {
            active = true;
            Minecraft.getInstance().setScreen(INSTANCE);
        }
    }

    public static void deactivate() {
        active = false;
        if (Minecraft.getInstance().screen == INSTANCE) {
            Minecraft.getInstance().setScreen(null);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (active && RadialMenu.animationTimer == 0) {
            double mouseAngle = AngleHelper.getMouseAngle();
            Minecraft mc = this.minecraft;
            mouseAngle -= ClientTickHelper.ANGLE_PER_ITEM / 2;
            mouseAngle = 360 - mouseAngle;
            mouseAngle = AngleHelper.correctAngle(mouseAngle);

            if (mc != null && !mc.options.hideGui) {
                for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
                    double currAngle = ClientTickHelper.ANGLE_PER_ITEM * i;
                    double nextAngle = currAngle + ClientTickHelper.ANGLE_PER_ITEM;
                    currAngle = AngleHelper.correctAngle(currAngle);
                    nextAngle = AngleHelper.correctAngle(nextAngle);

                    boolean mouseIn = mouseAngle > currAngle && mouseAngle < nextAngle;

                    if (mouseIn) {
                        MenuItem menuItem = RadialMenu.getActiveArray()[i];

                        if (menuItem != null) {
                            if (hasShiftDown() || (Config.GENERAL.rightClickToEdit.get() && button == 1)) {
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
    public void removed() {
        super.removed();
        active = false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(@Nonnull GuiGraphics guiGraphics, int i, int i1, float i2) {
    }
}