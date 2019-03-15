package dmillerw.menu.gui.menu;

import dmillerw.menu.data.click.*;
import dmillerw.menu.data.session.EditSessionData;
import dmillerw.menu.gui.GuiStack;
import dmillerw.menu.gui.menu.button.GuiItemButton;
import dmillerw.menu.helper.GuiRenderHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;

public class GuiClickAction extends GuiScreen {
    @Nonnull
    public static ItemStack item;
    public static KeyBinding keyBinding;
    private static boolean toggle = false;
    private static boolean clipboard = false;
    private GuiTextField textCommand;
    private GuiTextField textCategory;
    private GuiButtonExt modeCommand;
    private GuiButtonExt modeKeybinding;
    private GuiButtonExt modeUseItem;
    private GuiButtonExt modeCategory;
    private GuiButton commandClipboardButton;
    private GuiButton keybindButton;
    private GuiButton keybindToggleButton;
    private GuiButton selectItemButton;
    private GuiButton buttonCancel;
    private GuiButton buttonConfirm;
    private int mode = 0;

    public GuiClickAction() {
        GuiClickAction.keyBinding = null;
        GuiClickAction.item = ItemStack.EMPTY;
    }

    @Override
    public void tick() {
        this.textCommand.tick();
        this.textCategory.tick();
    }

    @Override
    @Nullable
    public IGuiEventListener getFocused() {
        if (mode == 0) {
            return this.textCommand;
        } else if (mode == 3) {
            return this.textCategory;
        } else {
            return super.getFocused();
        }
    }

    @Override
    public void initGui() {
        if (GuiClickAction.keyBinding != null) {
            mode = ClickAction.KEYBIND.ordinal();
        } else if (!GuiClickAction.item.isEmpty()) {
            mode = ClickAction.ITEM_USE.ordinal();
        } else {
            mode = EditSessionData.clickAction != null ? EditSessionData.clickAction.getClickAction().ordinal() : 0;
        }

        this.mc.keyboardListener.enableRepeatEvents(true);

        addButton(this.buttonConfirm = new GuiButton(0, this.width / 2 - 4 - 150, this.height - 60, 150, 20, I18n.format("gui.done")) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                if (mode == 0) {
                    EditSessionData.clickAction = !textCommand.getText().trim().isEmpty() ? new ClickActionCommand(textCommand.getText().trim(), clipboard) : null;
                } else if (mode == 1 && GuiClickAction.keyBinding != null) {
                    EditSessionData.clickAction = new ClickActionKey(keyBinding.getKeyDescription(), toggle);
                } else if (mode == 2 && !GuiClickAction.item.isEmpty()) {
                    EditSessionData.clickAction = new ClickActionUseItem(item);
                } else if (mode == 3) {
                    EditSessionData.clickAction = !(textCategory.getText().trim().isEmpty()) ? new ClickActionCategory(textCategory.getText().trim()) : null;
                }
                GuiStack.pop();
            }
        });
        addButton(this.buttonCancel = new GuiButton(1, this.width / 2 + 4, this.height - 60, 150, 20, I18n.format("gui.cancel")) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                GuiStack.pop();
            }
        });

        String commandString;
        if (EditSessionData.clickAction instanceof ClickActionCommand) {
            commandString = ((ClickActionCommand) EditSessionData.clickAction).clipboard ? "Clipboard" : "Send";
        } else {
            commandString = clipboard ? "Clipboard" : "Send";
        }
        addButton(this.commandClipboardButton = new GuiButton(9, this.width / 2 - 75, 80, 150, 20, commandString) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                clipboard = !clipboard;
                commandClipboardButton.displayString = clipboard ? "Clipboard" : "Send";
            }
        });

        String keyString;
        if (GuiClickAction.keyBinding != null) {
            keyString = I18n.format(keyBinding.getKeyDescription());
        } else {
            if (EditSessionData.clickAction instanceof ClickActionKey) {
                keyString = I18n.format(((ClickActionKey) EditSessionData.clickAction).key);
            } else {
                keyString = "Select a key";
            }
        }
        addButton(this.keybindButton = new GuiButton(2, this.width / 2 - 75, 50, 150, 20, keyString) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                GuiStack.push(new GuiPickKey());
            }
        });

        String keyToggleString;
        if (EditSessionData.clickAction instanceof ClickActionKey) {
            keyToggleString = ((ClickActionKey) EditSessionData.clickAction).toggle ? "Toggle" : "Press";
        } else {
            keyToggleString = toggle ? "Toggle" : "Press";
        }
        addButton(this.keybindToggleButton = new GuiButton(3, this.width / 2 - 75, 80, 150, 20, keyToggleString) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                toggle = !toggle;
                keybindToggleButton.displayString = toggle ? "Toggle" : "Press";
            }
        });

        String itemString;
        if (!GuiClickAction.item.isEmpty()) {
            itemString = "Item: " + item.getDisplayName().getString();
        } else {
            if (EditSessionData.clickAction != null && EditSessionData.clickAction.getClickAction() == ClickAction.ITEM_USE) {
                itemString = "Item: " + ((ClickActionUseItem) EditSessionData.clickAction).stack.getItem().getName().getString();
            } else {
                itemString = "Select a Slot";
            }
        }
        addButton(this.selectItemButton = new GuiButton(4, this.width / 2 - 75, 50, 150, 20, itemString) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                GuiStack.push(new GuiPickItem());
            }
        });

        addButton(this.modeCommand = new GuiItemButton(5, this.width / 2 - 55, this.height - 90, 20, 20, new ItemStack(Items.PAPER)) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                // Command
                mode = 0;

                modeCategory.enabled = true;
                modeUseItem.enabled = true;
                modeKeybinding.enabled = true;
                modeCommand.enabled = false;

                textCategory.setVisible(false);
                selectItemButton.visible = false;
                textCommand.setVisible(true);
                commandClipboardButton.visible = true;
                keybindButton.visible = false;
                keybindToggleButton.visible = false;
            }
        });
        addButton(this.modeKeybinding = new GuiItemButton(6, this.width / 2 - 25, this.height - 90, 20, 20, new ItemStack(Blocks.OAK_BUTTON)) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                // Keybinding
                mode = 1;

                modeCategory.enabled = true;
                modeUseItem.enabled = true;
                modeKeybinding.enabled = false;
                modeCommand.enabled = true;

                textCategory.setVisible(false);
                selectItemButton.visible = false;
                textCommand.setVisible(false);
                commandClipboardButton.visible = false;
                keybindButton.visible = true;
                keybindToggleButton.visible = true;
            }
        });
        addButton(this.modeUseItem = new GuiItemButton(7, this.width / 2 + 5, this.height - 90, 20, 20, new ItemStack(Items.DIAMOND_SWORD)) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                // Select item
                mode = 2;

                modeCategory.enabled = true;
                modeUseItem.enabled = false;
                modeKeybinding.enabled = true;
                modeCommand.enabled = true;

                textCategory.setVisible(false);
                selectItemButton.visible = true;
                textCommand.setVisible(false);
                commandClipboardButton.visible = false;
                keybindButton.visible = false;
                keybindToggleButton.visible = false;
            }
        });
        addButton(this.modeCategory = new GuiItemButton(8, this.width / 2 + 35, this.height - 90, 20, 20, new ItemStack(Blocks.CHEST)) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                // Category
                mode = 3;

                modeCategory.enabled = false;
                modeUseItem.enabled = true;
                modeKeybinding.enabled = true;
                modeCommand.enabled = true;

                textCategory.setVisible(true);
                selectItemButton.visible = false;
                textCommand.setVisible(false);
                commandClipboardButton.visible = false;
                keybindButton.visible = false;
                keybindToggleButton.visible = false;
            }
        });

        this.textCommand = new GuiTextField(0, this.fontRenderer, this.width / 2 - 150, 50, 300, 20);
        this.textCommand.setMaxStringLength(32767);
        this.textCommand.setFocused(true);
        this.textCommand.setText((EditSessionData.clickAction instanceof ClickActionCommand) ? ((ClickActionCommand) EditSessionData.clickAction).command : "");

        this.textCategory = new GuiTextField(0, this.fontRenderer, this.width / 2 - 150, 50, 300, 20);
        this.textCategory.setMaxStringLength(32767);
        this.textCategory.setFocused(true);
        this.textCategory.setText((EditSessionData.clickAction instanceof ClickActionCategory) ? ((ClickActionCategory) EditSessionData.clickAction).category : "");

        this.modeCommand.enabled = mode != 0;
        this.modeKeybinding.enabled = mode != 1;
        this.modeUseItem.enabled = mode != 2;
        this.modeCategory.enabled = mode != 3;

        textCommand.setVisible(mode == 0);
        commandClipboardButton.visible = mode == 0;
        keybindButton.visible = mode == 1;
        keybindToggleButton.visible = mode == 1;
        selectItemButton.visible = mode == 2;
        textCategory.setVisible(mode == 3);
    }

    @Override
    public void onGuiClosed() {
        this.mc.keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public boolean charTyped(char key, int keycode) {
        this.textCommand.charTyped(key, keycode);
        this.textCategory.charTyped(key, keycode);
        return true;
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        super.mouseClicked(mx, my, button);

        this.textCommand.mouseClicked(mx, my, button);
        return true;
    }

    @Override
    public void render(int mouseX, int mouseY, float partial) {
        this.drawDefaultBackground();
        this.textCommand.drawTextField(mouseX, mouseY, partial);
        this.textCategory.drawTextField(mouseX, mouseY, partial);
        super.render(mouseX, mouseY, partial);
        String header = "";
        switch (mode) {
            case 0:
                header = "Enter a command";
                break;
            case 1:
                header = "Select a key";
                break;
            case 2:
                header = "Pick an item";
                break;
            case 3:
                header = "Enter a category";
                break;
        }
        GuiRenderHelper.renderHeaderAndFooter(this, 25, 20, 5, header);
        if (mouseX > modeCommand.x && mouseX < modeCommand.x + modeCommand.width && mouseY > modeCommand.y && mouseY < modeCommand.y + modeCommand.width) {
            this.drawHoveringText(Collections.singletonList("Click Action: Command"), mouseX, mouseY);
        } else if (mouseX > modeKeybinding.x && mouseX < modeKeybinding.x + modeKeybinding.width && mouseY > modeKeybinding.y && mouseY < modeKeybinding.y + modeKeybinding.width) {
            this.drawHoveringText(Collections.singletonList("Click Action: KeyBinding"), mouseX, mouseY);
        } else if (mouseX > modeUseItem.x && mouseX < modeUseItem.x + modeUseItem.width && mouseY > modeUseItem.y && mouseY < modeUseItem.y + modeUseItem.width) {
            this.drawHoveringText(Collections.singletonList("Click Action: Use Item"), mouseX, mouseY);
        } else if (mouseX > modeCategory.x && mouseX < modeCategory.x + modeCategory.width && mouseY > modeCategory.y && mouseY < modeCategory.y + modeCategory.width) {
            this.drawHoveringText(Collections.singletonList("Click Action: Category"), mouseX, mouseY);
        }
    }
}