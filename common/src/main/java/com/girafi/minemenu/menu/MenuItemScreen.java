package com.girafi.minemenu.menu;

import com.girafi.minemenu.MineMenuCommon;
import com.girafi.minemenu.data.click.ClickActionCategory;
import com.girafi.minemenu.data.click.ClickActionCommand;
import com.girafi.minemenu.data.click.ClickActionKey;
import com.girafi.minemenu.data.click.ClickActionUseItem;
import com.girafi.minemenu.data.json.MenuLoader;
import com.girafi.minemenu.data.menu.MenuItem;
import com.girafi.minemenu.data.menu.RadialMenu;
import com.girafi.minemenu.data.session.EditSessionData;
import com.girafi.minemenu.gui.ScreenStack;
import com.girafi.minemenu.menu.button.ItemButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;

public class MenuItemScreen extends Screen {
    private final int slot;
    private EditBox textTitle;
    private Button buttonCancel;
    private Button buttonConfirm;
    private Button buttonDelete;
    private ItemButton buttonPickIcon;
    private Button buttonClickAction;

    public MenuItemScreen(int slot, MenuItem menuItem) {
        super(Component.translatable("mine_menu.itemScreen.title"));
        this.slot = slot;
        EditSessionData.fromMenuItem(menuItem);
    }

    @Override
    public void tick() {
        this.buttonConfirm.visible = EditSessionData.clickAction != null;
        this.buttonDelete.visible = RadialMenu.getActiveArray()[slot] != null;
    }

    @Override
    public void init() {
        addRenderableWidget(this.buttonConfirm = Button.builder(Component.translatable("gui.done"), (screen) -> {
            if (EditSessionData.title.isEmpty()) {
                EditSessionData.title = "Menu Item #" + slot;
            } else {
                EditSessionData.title = this.textTitle.getValue();
            }

            if (EditSessionData.clickAction != null) {
                if (RadialMenu.getActiveArray()[slot] != null) {
                    RadialMenu.getActiveArray()[slot].onRemoved();
                }
                RadialMenu.getActiveArray()[slot] = EditSessionData.toMenuItem();
            }
            MenuLoader.save(MineMenuCommon.menuFile);
            Minecraft.getInstance().setScreen(null);
        }).bounds(this.width / 2 - 4 - 150, this.height - 60, 100, 20).build());
        addRenderableWidget(this.buttonCancel = Button.builder(Component.translatable("gui.cancel"), (screen) -> Minecraft.getInstance().setScreen(null)).bounds(this.width / 2 + 4 + 50, this.height - 60, 100, 20).build());
        addRenderableWidget(this.buttonDelete = Button.builder(Component.translatable("gui.delete"), (screen) -> {
            if (RadialMenu.getActiveArray()[slot] != null) {
                RadialMenu.getActiveArray()[slot].onRemoved();
                RadialMenu.getActiveArray()[slot] = null;
                MenuLoader.save(MineMenuCommon.menuFile);
                Minecraft.getInstance().setScreen(null);
            }
        }).bounds(this.width / 2 - 50, this.height - 60, 100, 20).build());
        addRenderableWidget(this.buttonPickIcon = new ItemButton(this.width / 2 - 4 - 40, this.height / 2, 20, 20, new ItemStack(Blocks.STONE), (screen) -> ScreenStack.push(new PickIconScreen())));

        Component string = Component.translatable("mine_menu.action");
        if (EditSessionData.clickAction != null) {
            if (EditSessionData.clickAction instanceof ClickActionCommand) {
                string = Component.translatable("mine_menu.command");
            } else if (EditSessionData.clickAction instanceof ClickActionKey) {
                string = Component.translatable("mine_menu.keybind");
            } else if (EditSessionData.clickAction instanceof ClickActionUseItem) {
                string = Component.translatable("mine_menu.useItem");
            } else if (EditSessionData.clickAction instanceof ClickActionCategory) {
                string = Component.translatable("mine_menu.category");
            }
        }
        addRenderableWidget(this.buttonClickAction = Button.builder(string, (screen) -> ScreenStack.push(new ClickActionScreen())).bounds(this.width / 2 - 20, this.height / 2, 100, 20).build());

        this.textTitle = new EditBox(this.font, this.width / 2 - 150, 50, 300, 20, Component.translatable("mine_menu.menuItem.title"));
        this.textTitle.setMaxLength(32767);
        this.textTitle.setValue(EditSessionData.title != null && !EditSessionData.title.isEmpty() ? EditSessionData.title : "");
        this.addRenderableWidget(this.textTitle);

        this.buttonPickIcon.icon = EditSessionData.icon;
    }

    @Override
    public void removed() {
        if (this.textTitle != null) {
            EditSessionData.title = this.textTitle.getValue(); //Remember title when going to another screen
        }
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
        } else {
            return super.keyPressed(event);
        }
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        super.render(guiGraphics, mouseX, mouseY, partial);
        this.textTitle.render(guiGraphics, mouseX, mouseY, partial);
        guiGraphics.drawCenteredString(this.font, "Modifying Menu Item #" + slot, this.width / 2, 8, -1);
        guiGraphics.drawCenteredString(this.font, "Enter a title, then configure using the options below", this.width / 2, 80, -1);
    }
}