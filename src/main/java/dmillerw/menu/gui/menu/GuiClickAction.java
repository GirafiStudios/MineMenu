package dmillerw.menu.gui.menu;

import cpw.mods.fml.client.config.GuiButtonExt;
import dmillerw.menu.data.click.ClickAction;
import dmillerw.menu.data.session.EditSessionData;
import dmillerw.menu.data.click.CommandClickAction;
import dmillerw.menu.data.click.ItemClickAction;
import dmillerw.menu.data.click.KeyClickAction;
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
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

/**
 * @author dmillerw
 */
public class GuiClickAction extends GuiScreen {

    public static ItemStack item;

    public static KeyBinding keyBinding;

    private GuiTextField textCommand;

    private GuiButtonExt modeCommand;
    private GuiButtonExt modeKeybinding;
    private GuiButtonExt modeUseItem;

    private GuiButton keybindButton;
    private GuiButton selectItemButton;

    private GuiButton buttonCancel;
    private GuiButton buttonConfirm;

    private int mode = 0;

    public GuiClickAction() {
        GuiClickAction.keyBinding = null;
    }

    @Override
    public void updateScreen() {
        this.textCommand.updateCursorCounter();
    }

    @Override
    public void initGui() {
        if (GuiClickAction.keyBinding != null) {
            mode = ClickAction.KEYBIND.ordinal();
        } else if (GuiClickAction.item != null) {
            mode = ClickAction.ITEM_USE.ordinal();
        } else {
            mode = EditSessionData.clickAction != null ? EditSessionData.clickAction.getClickAction().ordinal() : 0;
        }

        Keyboard.enableRepeatEvents(true);

        this.buttonList.clear();

        this.buttonList.add(this.buttonConfirm = new GuiButton(0, this.width / 2 - 4 - 150, this.height - 60, 150, 20, I18n.format("gui.done")));
        this.buttonList.add(this.buttonCancel = new GuiButton(1, this.width / 2 + 4, this.height - 60, 150, 20, I18n.format("gui.cancel")));

        String keyString = "";
        if (GuiClickAction.keyBinding != null) {
            keyString = I18n.format(keyBinding.getKeyDescription());
        } else {
            if (EditSessionData.clickAction != null && EditSessionData.clickAction instanceof KeyClickAction) {
                keyString = I18n.format(((KeyClickAction) EditSessionData.clickAction).key);
            } else {
                keyString = "Select a key";
            }
        }
        this.buttonList.add(this.keybindButton = new GuiButton(2, this.width / 2 - 75, 50, 150, 20, keyString));

        String itemString = "";
        if (GuiClickAction.item != null) {
            itemString = "Item: " + item.getDisplayName();
        } else {
            if (EditSessionData.clickAction != null && EditSessionData.clickAction.getClickAction() == ClickAction.ITEM_USE) {
                itemString = "Item: " + ((ItemClickAction) EditSessionData.clickAction).item.getDisplayName();
            } else {
                itemString = "Select a Slot";
            }
        }
        this.buttonList.add(this.selectItemButton = new GuiButton(3, this.width / 2 - 75, 50, 150, 20, itemString));

        this.buttonList.add(this.modeCommand = new GuiItemButton(4, this.width / 2 - 40, this.height - 90, 20, 20, new ItemStack(Items.paper)));
        this.buttonList.add(this.modeKeybinding = new GuiItemButton(5, this.width / 2 - 10, this.height - 90, 20, 20, new ItemStack(Blocks.wooden_button)));
        this.buttonList.add(this.modeUseItem = new GuiItemButton(6, this.width / 2 + 20, this.height - 90, 20, 20, new ItemStack(Items.diamond_sword)));

        this.textCommand = new GuiTextField(this.fontRendererObj, this.width / 2 - 150, 50, 300, 20);
        this.textCommand.setMaxStringLength(32767);
        this.textCommand.setFocused(true);
        this.textCommand.setText((EditSessionData.clickAction != null && EditSessionData.clickAction instanceof CommandClickAction) ? ((CommandClickAction) EditSessionData.clickAction).command : "");

        this.modeCommand.enabled = mode != 0;
        this.modeKeybinding.enabled = mode != 1;
        this.modeUseItem.enabled = mode != 2;

        textCommand.setVisible(mode == 0);
        keybindButton.visible = mode == 1;
        selectItemButton.visible = mode == 2;
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
            if (button.id == 6) {
                // Select item
                mode = 2;
				modeUseItem.enabled = false;
				modeKeybinding.enabled = true;
				modeCommand.enabled = true;

				selectItemButton.visible = true;
				textCommand.setVisible(false);
				keybindButton.visible = false;
            } else if (button.id == 5) {
                // Keybinding
                mode = 1;

                modeUseItem.enabled = true;
                modeKeybinding.enabled = false;
                modeCommand.enabled = true;

                selectItemButton.visible = false;
                textCommand.setVisible(false);
                keybindButton.visible = true;
            } else if (button.id == 4) {
                // Command
                mode = 0;

                modeUseItem.enabled = true;
                modeKeybinding.enabled = true;
                modeCommand.enabled = false;

                selectItemButton.visible = false;
                textCommand.setVisible(true);
                keybindButton.visible = false;
            } else if (button.id == 3) {
                GuiStack.push(new GuiPickItem());
            } else if (button.id == 2) {
                GuiStack.push(new GuiPickKey());
            } else if (button.id == 1) {
                GuiStack.pop();
            } else if (button.id == 0) {
                if (mode == 0) {
                    EditSessionData.clickAction = !(textCommand.getText().trim().isEmpty()) ? new CommandClickAction(textCommand.getText().trim()) : null;
                } else if (mode == 1 && GuiClickAction.keyBinding != null) {
                    EditSessionData.clickAction = new KeyClickAction(keyBinding.getKeyDescription());
                } else if (mode == 2 && GuiClickAction.item != null) {
                    EditSessionData.clickAction = new ItemClickAction(item);
                }
                GuiStack.pop();
            }
        }
    }

    @Override
    protected void keyTyped(char key, int keycode) {
        this.textCommand.textboxKeyTyped(key, keycode);

        if (keycode != 28 && keycode != 156) {
            if (keycode == 1) {
                this.actionPerformed(this.buttonCancel);
            }
        }
    }

    @Override
    protected void mouseClicked(int mx, int my, int button) {
        super.mouseClicked(mx, my, button);

        this.textCommand.mouseClicked(mx, my, button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partial) {
        this.drawDefaultBackground();
        this.textCommand.drawTextBox();
        super.drawScreen(mouseX, mouseY, partial);
        String header = "";
        switch (mode) {
            case 0: header = "Select a Key"; break;
            case 1: header = "Enter a Command"; break;
            case 2: header = "Pick an Item"; break;
        }
        GuiRenderHelper.renderHeaderAndFooter(this, 25, 20, 5, header);
        if (mouseX > modeCommand.xPosition && mouseX < modeCommand.xPosition + modeCommand.width && mouseY > modeCommand.yPosition && mouseY < modeCommand.yPosition + modeCommand.width) {
            this.func_146283_a(Arrays.asList("Click Action: Command"), mouseX, mouseY);
        } else if (mouseX > modeKeybinding.xPosition && mouseX < modeKeybinding.xPosition + modeKeybinding.width && mouseY > modeKeybinding.yPosition && mouseY < modeKeybinding.yPosition + modeKeybinding.width) {
            this.func_146283_a(Arrays.asList("Click Action: KeyBinding"), mouseX, mouseY);
        } else if (mouseX > modeUseItem.xPosition && mouseX < modeUseItem.xPosition + modeUseItem.width && mouseY > modeUseItem.yPosition && mouseY < modeUseItem.yPosition + modeUseItem.width) {
            this.func_146283_a(Arrays.asList("Click Action: Use Item"), mouseX, mouseY);
        }
    }
}
