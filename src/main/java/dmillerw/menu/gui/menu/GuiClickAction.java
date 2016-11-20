package dmillerw.menu.gui.menu;

import dmillerw.menu.data.click.*;
import dmillerw.menu.data.session.EditSessionData;
import dmillerw.menu.gui.GuiStack;
import dmillerw.menu.gui.menu.button.GuiItemButton;
import dmillerw.menu.helper.GuiRenderHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collections;

/**
 * @author dmillerw
 */
public class GuiClickAction extends GuiScreen {
    @Nonnull
    public static ItemStack item;

    public static KeyBinding keyBinding;

    private static boolean toggle = false;

    private GuiTextField textCommand;
    private GuiTextField textCategory;

    private GuiButtonExt modeCommand;
    private GuiButtonExt modeKeybinding;
    private GuiButtonExt modeUseItem;
    private GuiButtonExt modeCategory;

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
    public void updateScreen() {
        this.textCommand.updateCursorCounter();
        this.textCategory.updateCursorCounter();
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

        Keyboard.enableRepeatEvents(true);

        this.buttonList.clear();

        this.buttonList.add(this.buttonConfirm = new GuiButton(0, this.width / 2 - 4 - 150, this.height - 60, 150, 20, I18n.format("gui.done")));
        this.buttonList.add(this.buttonCancel = new GuiButton(1, this.width / 2 + 4, this.height - 60, 150, 20, I18n.format("gui.cancel")));

        String keyString;
        if (GuiClickAction.keyBinding != null) {
            keyString = I18n.format(keyBinding.getKeyDescription());
        } else {
            if (EditSessionData.clickAction != null && EditSessionData.clickAction instanceof ClickActionKey) {
                keyString = I18n.format(((ClickActionKey) EditSessionData.clickAction).key);
            } else {
                keyString = "Select a key";
            }
        }
        this.buttonList.add(this.keybindButton = new GuiButton(2, this.width / 2 - 75, 50, 150, 20, keyString));

        String keyToggleString;
        if (EditSessionData.clickAction != null && EditSessionData.clickAction instanceof ClickActionKey) {
            keyToggleString = ((ClickActionKey)EditSessionData.clickAction).toggle ? "Toggle" : "Press";
        } else {
            keyToggleString = toggle ? "Toggle" : "Press";
        }
        this.buttonList.add(this.keybindToggleButton = new GuiButton(3, this.width / 2 - 75, 80, 150, 20, keyToggleString));

        String itemString;
        if (!GuiClickAction.item.isEmpty()) {
            itemString = "Item: " + item.getDisplayName();
        } else {
            if (EditSessionData.clickAction != null && EditSessionData.clickAction.getClickAction() == ClickAction.ITEM_USE) {
                itemString = "Item: " + ((ClickActionUseItem) EditSessionData.clickAction).stack.getDisplayName();
            } else {
                itemString = "Select a Slot";
            }
        }
        this.buttonList.add(this.selectItemButton = new GuiButton(4, this.width / 2 - 75, 50, 150, 20, itemString));

        this.buttonList.add(this.modeCommand = new GuiItemButton(5, this.width / 2 - 55, this.height - 90, 20, 20, new ItemStack(Items.PAPER)));
        this.buttonList.add(this.modeKeybinding = new GuiItemButton(6, this.width / 2 - 25, this.height - 90, 20, 20, new ItemStack(Blocks.WOODEN_BUTTON)));
        this.buttonList.add(this.modeUseItem = new GuiItemButton(7, this.width / 2 + 5, this.height - 90, 20, 20, new ItemStack(Items.DIAMOND_SWORD)));
        this.buttonList.add(this.modeCategory = new GuiItemButton(8, this.width / 2 + 35, this.height - 90, 20, 20, new ItemStack(Blocks.CHEST)));

        this.textCommand = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 150, 50, 300, 20);
        this.textCommand.setMaxStringLength(32767);
        this.textCommand.setFocused(true);
        this.textCommand.setText((EditSessionData.clickAction != null && EditSessionData.clickAction instanceof ClickActionCommand) ? ((ClickActionCommand) EditSessionData.clickAction).command : "");

        this.textCategory = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 150, 50, 300, 20);
        this.textCategory.setMaxStringLength(32767);
        this.textCategory.setFocused(true);
        this.textCategory.setText((EditSessionData.clickAction != null && EditSessionData.clickAction instanceof ClickActionCategory) ? ((ClickActionCategory) EditSessionData.clickAction).category : "");

        this.modeCommand.enabled = mode != 0;
        this.modeKeybinding.enabled = mode != 1;
        this.modeUseItem.enabled = mode != 2;
        this.modeCategory.enabled = mode != 3;

        textCommand.setVisible(mode == 0);
        keybindButton.visible = mode == 1;
        keybindToggleButton.visible = mode == 1;
        selectItemButton.visible = mode == 2;
        textCategory.setVisible(mode == 3);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button.id == 8) {
                // Category
                mode = 3;

                modeCategory.enabled = false;
                modeUseItem.enabled = true;
                modeKeybinding.enabled = true;
                modeCommand.enabled = true;

                textCategory.setVisible(true);
                selectItemButton.visible = false;
                textCommand.setVisible(false);
                keybindButton.visible = false;
                keybindToggleButton.visible = false;
            } else if (button.id == 7) {
                // Select item
                mode = 2;

                modeCategory.enabled = true;
				modeUseItem.enabled = false;
				modeKeybinding.enabled = true;
				modeCommand.enabled = true;

                textCategory.setVisible(false);
				selectItemButton.visible = true;
				textCommand.setVisible(false);
				keybindButton.visible = false;
                keybindToggleButton.visible = false;
            } else if (button.id == 6) {
                // Keybinding
                mode = 1;

                modeCategory.enabled = true;
                modeUseItem.enabled = true;
                modeKeybinding.enabled = false;
                modeCommand.enabled = true;

                textCategory.setVisible(false);
                selectItemButton.visible = false;
                textCommand.setVisible(false);
                keybindButton.visible = true;
                keybindToggleButton.visible = true;
            } else if (button.id == 5) {
                // Command
                mode = 0;

                modeCategory.enabled = true;
                modeUseItem.enabled = true;
                modeKeybinding.enabled = true;
                modeCommand.enabled = false;

                textCategory.setVisible(false);
                selectItemButton.visible = false;
                textCommand.setVisible(true);
                keybindButton.visible = false;
                keybindToggleButton.visible = false;
            } else if (button.id == 4) {
                GuiStack.push(new GuiPickItem());
            } else if (button.id == 3) {
                toggle = !toggle;
                keybindToggleButton.displayString = toggle ? "Toggle" : "Press";
            } else if (button.id == 2) {
                GuiStack.push(new GuiPickKey());
            } else if (button.id == 1) {
                GuiStack.pop();
            } else if (button.id == 0) {
                if (mode == 0) {
                    EditSessionData.clickAction = !(textCommand.getText().trim().isEmpty()) ? new ClickActionCommand(textCommand.getText().trim()) : null;
                } else if (mode == 1 && GuiClickAction.keyBinding != null) {
                    EditSessionData.clickAction = new ClickActionKey(keyBinding.getKeyDescription(), toggle);
                } else if (mode == 2 && !GuiClickAction.item.isEmpty()) {
                    EditSessionData.clickAction = new ClickActionUseItem(item);
                } else if (mode == 3) {
                    EditSessionData.clickAction = !(textCategory.getText().trim().isEmpty()) ? new ClickActionCategory(textCategory.getText().trim()) : null;
                }
                GuiStack.pop();
            }
        }
    }

    @Override
    protected void keyTyped(char key, int keycode) {
        this.textCommand.textboxKeyTyped(key, keycode);
        this.textCategory.textboxKeyTyped(key, keycode);

        if (keycode != 28 && keycode != 156) {
            if (keycode == 1) {
                this.actionPerformed(this.buttonCancel);
            }
        }
    }

    @Override
    protected void mouseClicked(int mx, int my, int button) throws IOException {
        super.mouseClicked(mx, my, button);

        this.textCommand.mouseClicked(mx, my, button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partial) {
        this.drawDefaultBackground();
        this.textCommand.drawTextBox();
        this.textCategory.drawTextBox();
        super.drawScreen(mouseX, mouseY, partial);
        String header = "";
        switch (mode) {
            case 0: header = "Enter a command"; break;
            case 1: header = "Select a key"; break;
            case 2: header = "Pick an item"; break;
            case 3: header = "Enter a category"; break;
        }
        GuiRenderHelper.renderHeaderAndFooter(this, 25, 20, 5, header);
        if (mouseX > modeCommand.xPosition && mouseX < modeCommand.xPosition + modeCommand.width && mouseY > modeCommand.yPosition && mouseY < modeCommand.yPosition + modeCommand.width) {
            this.drawHoveringText(Collections.singletonList("Click Action: Command"), mouseX, mouseY);
        } else if (mouseX > modeKeybinding.xPosition && mouseX < modeKeybinding.xPosition + modeKeybinding.width && mouseY > modeKeybinding.yPosition && mouseY < modeKeybinding.yPosition + modeKeybinding.width) {
            this.drawHoveringText(Collections.singletonList("Click Action: KeyBinding"), mouseX, mouseY);
        } else if (mouseX > modeUseItem.xPosition && mouseX < modeUseItem.xPosition + modeUseItem.width && mouseY > modeUseItem.yPosition && mouseY < modeUseItem.yPosition + modeUseItem.width) {
            this.drawHoveringText(Collections.singletonList("Click Action: Use Item"), mouseX, mouseY);
        } else if (mouseX > modeCategory.xPosition && mouseX < modeCategory.xPosition + modeCategory.width && mouseY > modeCategory.yPosition && mouseY < modeCategory.yPosition + modeCategory.width) {
            this.drawHoveringText(Collections.singletonList("Click Action: Category"), mouseX, mouseY);
        }
    }
}
