package com.girafi.minemenu.gui;

import com.girafi.minemenu.data.menu.MenuItem;
import com.girafi.minemenu.data.menu.RadialMenu;
import com.girafi.minemenu.handler.RadialMenuInput;
import com.girafi.minemenu.menu.CategoryColorScreen;
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

    public static boolean shouldHideHud() {
        return active && Config.GENERAL.hideHud.get();
    }

    public static boolean isEditClick(int button) {
        return (Config.GENERAL.shiftClickToEdit.get() && hasShiftDown()) || (Config.GENERAL.rightClickToEdit.get() && button == 1);
    }

    public static boolean isShiftEditing() {
        return Config.GENERAL.shiftClickToEdit.get() && hasShiftDown();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isEditClick(button)) {
            return editHovered(button == 0);
        } else if (button == 0) {
            return selectHovered();
        }
        return false;
    }

    /**
     * Runs the click action of the hovered menu item, or opens the screen for adding an item if the hovered slot is empty.
     */
    public boolean selectHovered() {
        if (!canInteract()) {
            return false;
        }
        int index = RadialMenuInput.getHoveredIndex();
        if (index < 0) {
            return false;
        }
        MenuItem menuItem = RadialMenu.getActiveArray()[index];
        if (menuItem == null) {
            deactivate();
            ScreenStack.open(new MenuItemScreen(index, null));
        } else {
            if (menuItem.clickAction.deactivates()) {
                deactivate();
            }
            menuItem.clickAction.onClicked();
        }
        return true;
    }

    /**
     * Opens the edit screen for the hovered menu item, or the category color screen if the center is hovered.
     *
     * @param addIfEmpty Whether to open the screen for adding an item if the hovered slot is empty
     */
    public boolean editHovered(boolean addIfEmpty) {
        if (!active || RadialMenu.animationTimer != 0) {
            return false;
        }
        if (RadialMenuInput.isCenterHovered()) {
            return editCategory();
        }
        if (!canInteract()) {
            return false;
        }
        int index = RadialMenuInput.getHoveredIndex();
        if (index < 0) {
            return false;
        }
        MenuItem menuItem = RadialMenu.getActiveArray()[index];
        if (menuItem != null || addIfEmpty) {
            deactivate();
            ScreenStack.open(new MenuItemScreen(index, menuItem));
            return true;
        }
        return false;
    }

    /**
     * Opens the color screen for the current category.
     */
    public boolean editCategory() {
        if (!active || RadialMenu.animationTimer != 0) {
            return false;
        }
        String category = RadialMenu.currentCategory;
        deactivate();
        Minecraft.getInstance().setScreen(new CategoryColorScreen(category));
        return true;
    }

    private static boolean canInteract() {
        Minecraft mc = Minecraft.getInstance();
        return active && RadialMenu.animationTimer == 0 && !mc.options.hideGui;
    }

    @Override
    public void removed() {
        super.removed();
        active = false;
        RadialMenuInput.onMenuClosed();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(@Nonnull GuiGraphics guiGraphics, int i, int i1, float i2) {
    }
}