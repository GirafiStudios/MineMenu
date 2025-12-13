package com.girafi.minemenu.menu;

import com.girafi.minemenu.gui.ScreenStack;
import com.girafi.minemenu.menu.list.GuiControlList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;

public class PickKeyScreen extends Screen {
    private GuiControlList controlList;

    public PickKeyScreen() {
        super(Component.translatable("mine_menu.keyScreen.title"));
    }

    @Override
    public void init() {
        this.controlList = new GuiControlList(this, this.minecraft);
        this.addWidget(this.controlList);
        this.setFocused(this.controlList);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.input() == GLFW.GLFW_KEY_ESCAPE) {
            ScreenStack.pop();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseReleased(@Nonnull MouseButtonEvent event) {
        if (event.input() == 0 && this.controlList.mouseReleased(event)) {
            this.setDragging(false);
            return true;
        } else {
            return super.mouseReleased(event);
        }
    }

    @Override
    public boolean mouseClicked(@Nonnull MouseButtonEvent event, boolean isDoubleClick) {
        if (event.input() == 0 && this.controlList.mouseClicked(event, isDoubleClick)) {
            this.setDragging(true);
            this.setFocused(this.controlList);
            return true;
        } else {
            return this.controlList != null && this.controlList.mouseClicked(event, isDoubleClick);
        }
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int x, int y, float partialTicks) {
        super.render(guiGraphics, x, y, partialTicks);
        this.controlList.render(guiGraphics, x, y, partialTicks);
        guiGraphics.drawCenteredString(this.font, "Select a key", this.width / 2, 8, -1);
    }
}