package com.girafi.minemenu.data.click;

import com.girafi.minemenu.data.menu.RadialMenu;

public class ClickActionCategory implements ClickAction.IClickAction {

    public final String category;

    public ClickActionCategory(String category) {
        this.category = category;
    }

    @Override
    public ClickAction getClickAction() {
        return ClickAction.CATEGORY;
    }

    @Override
    public void onClicked() {
        RadialMenu.currentCategory = category;
        RadialMenu.resetTimer();
    }

    @Override
    public void onRemoved() {
    }
}