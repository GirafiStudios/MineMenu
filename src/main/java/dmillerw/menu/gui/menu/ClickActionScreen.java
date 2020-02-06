package dmillerw.menu.gui.menu;

import dmillerw.menu.data.click.*;
import dmillerw.menu.data.session.EditSessionData;
import dmillerw.menu.gui.ScreenStack;
import dmillerw.menu.gui.menu.button.ItemButton;
import dmillerw.menu.helper.GuiRenderHelper;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;

public class ClickActionScreen extends Screen {
    @Nonnull
    public static ItemStack item;
    public static KeyBinding keyBinding;
    private static boolean toggle = false;
    private static boolean clipboard = false;
    private TextFieldWidget textCommand;
    private TextFieldWidget textCategory;
    private ExtendedButton modeCommand;
    private ExtendedButton modeKeybinding;
    private ExtendedButton modeUseItem;
    private ExtendedButton modeCategory;
    private Button commandClipboardButton;
    private Button keybindButton;
    private Button keybindToggleButton;
    private Button selectItemButton;
    private Button buttonCancel;
    private Button buttonConfirm;
    private int mode = 0;

    public ClickActionScreen() {
        super(new TranslationTextComponent("minemenu.actionScreen.title"));
        ClickActionScreen.keyBinding = null;
        ClickActionScreen.item = ItemStack.EMPTY;
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
    public void init() {
        if (ClickActionScreen.keyBinding != null) {
            mode = ClickAction.KEYBIND.ordinal();
        } else if (!ClickActionScreen.item.isEmpty()) {
            mode = ClickAction.ITEM_USE.ordinal();
        } else {
            mode = EditSessionData.clickAction != null ? EditSessionData.clickAction.getClickAction().ordinal() : 0;
        }

        this.getMinecraft().keyboardListener.enableRepeatEvents(true);

        addButton(this.buttonConfirm = new Button(this.width / 2 - 4 - 150, this.height - 60, 150, 20, I18n.format("gui.done"), (screen) -> {
            if (mode == 0) {
                EditSessionData.clickAction = !textCommand.getText().trim().isEmpty() ? new ClickActionCommand(textCommand.getText().trim(), clipboard) : null;
            } else if (mode == 1 && ClickActionScreen.keyBinding != null) {
                EditSessionData.clickAction = new ClickActionKey(keyBinding.getKeyDescription(), toggle);
            } else if (mode == 2 && !ClickActionScreen.item.isEmpty()) {
                EditSessionData.clickAction = new ClickActionUseItem(item);
            } else if (mode == 3) {
                EditSessionData.clickAction = !(textCategory.getText().trim().isEmpty()) ? new ClickActionCategory(textCategory.getText().trim()) : null;
            }
            ScreenStack.pop();
        }));

        addButton(this.buttonCancel = new Button(this.width / 2 + 4, this.height - 60, 150, 20, I18n.format("gui.cancel"), (screen) -> ScreenStack.pop()));

        String commandString;
        if (EditSessionData.clickAction instanceof ClickActionCommand) {
            commandString = ((ClickActionCommand) EditSessionData.clickAction).clipboard ? "Clipboard" : "Send";
        } else {
            commandString = clipboard ? "Clipboard" : "Send";
        }
        addButton(this.commandClipboardButton = new Button(this.width / 2 - 75, 80, 150, 20, commandString, (screen) -> {
            clipboard = !clipboard;
            commandClipboardButton.setMessage(clipboard ? "Clipboard" : "Send");
        }));

        String keyString;
        if (ClickActionScreen.keyBinding != null) {
            keyString = I18n.format(keyBinding.getKeyDescription());
        } else {
            if (EditSessionData.clickAction instanceof ClickActionKey) {
                keyString = I18n.format(((ClickActionKey) EditSessionData.clickAction).key);
            } else {
                keyString = "Select a key";
            }
        }
        addButton(this.keybindButton = new Button(this.width / 2 - 75, 50, 150, 20, keyString, (screen) -> ScreenStack.push(new PickKeyScreen())));

        String keyToggleString;
        if (EditSessionData.clickAction instanceof ClickActionKey) {
            keyToggleString = ((ClickActionKey) EditSessionData.clickAction).toggle ? "Toggle" : "Press";
        } else {
            keyToggleString = toggle ? "Toggle" : "Press";
        }
        addButton(this.keybindToggleButton = new Button(this.width / 2 - 75, 80, 150, 20, keyToggleString, (screen) -> {
            toggle = !toggle;
            keybindToggleButton.setMessage(toggle ? "Toggle" : "Press");
        }));

        String itemString;
        if (!ClickActionScreen.item.isEmpty()) {
            itemString = "Item: " + item.getDisplayName().getString();
        } else {
            if (EditSessionData.clickAction != null && EditSessionData.clickAction.getClickAction() == ClickAction.ITEM_USE) {
                itemString = "Item: " + ((ClickActionUseItem) EditSessionData.clickAction).stack.getItem().getName().getString();
            } else {
                itemString = "Select a Slot";
            }
        }
        addButton(this.selectItemButton = new Button(this.width / 2 - 75, 50, 150, 20, itemString, (screen) -> ScreenStack.push(new PickItemScreen())));

        addButton(this.modeCommand = new ItemButton(this.width / 2 - 55, this.height - 90, 20, 20, new ItemStack(Items.PAPER), (screen) -> {
            // Command
            mode = 0;

            modeCategory.active = true;
            modeUseItem.active = true;
            modeKeybinding.active = true;
            modeCommand.active = false;

            textCategory.setVisible(false);
            selectItemButton.visible = false;
            textCommand.setVisible(true);
            commandClipboardButton.visible = true;
            keybindButton.visible = false;
            keybindToggleButton.visible = false;
        }));

        addButton(this.modeKeybinding = new ItemButton(this.width / 2 - 25, this.height - 90, 20, 20, new ItemStack(Blocks.OAK_BUTTON), (screen) -> {
            // Keybinding
            mode = 1;

            modeCategory.active = true;
            modeUseItem.active = true;
            modeKeybinding.active = false;
            modeCommand.active = true;

            textCategory.setVisible(false);
            selectItemButton.visible = false;
            textCommand.setVisible(false);
            commandClipboardButton.visible = false;
            keybindButton.visible = true;
            keybindToggleButton.visible = true;
        }));

        addButton(this.modeUseItem = new ItemButton(this.width / 2 + 5, this.height - 90, 20, 20, new ItemStack(Items.DIAMOND_SWORD), (screen) -> {
            // Select item
            mode = 2;

            modeCategory.active = true;
            modeUseItem.active = false;
            modeKeybinding.active = true;
            modeCommand.active = true;

            textCategory.setVisible(false);
            selectItemButton.visible = true;
            textCommand.setVisible(false);
            commandClipboardButton.visible = false;
            keybindButton.visible = false;
            keybindToggleButton.visible = false;
        }));

        addButton(this.modeCategory = new ItemButton(this.width / 2 + 35, this.height - 90, 20, 20, new ItemStack(Blocks.CHEST), (screen) -> {
            // Category
            mode = 3;

            modeCategory.active = false;
            modeUseItem.active = true;
            modeKeybinding.active = true;
            modeCommand.active = true;

            textCategory.setVisible(true);
            selectItemButton.visible = false;
            textCommand.setVisible(false);
            commandClipboardButton.visible = false;
            keybindButton.visible = false;
            keybindToggleButton.visible = false;
        }));

        this.textCommand = new TextFieldWidget(this.font, this.width / 2 - 150, 50, 300, 20, "minemenu.action.command");
        this.textCommand.setMaxStringLength(32767);
        this.textCommand.changeFocus(true);
        this.textCommand.setText((EditSessionData.clickAction instanceof ClickActionCommand) ? ((ClickActionCommand) EditSessionData.clickAction).command : "");

        this.textCategory = new TextFieldWidget(this.font, this.width / 2 - 150, 50, 300, 20, "minemenu.action.category");
        this.textCategory.setMaxStringLength(32767);
        this.textCategory.changeFocus(true);
        this.textCategory.setText((EditSessionData.clickAction instanceof ClickActionCategory) ? ((ClickActionCategory) EditSessionData.clickAction).category : "");

        this.modeCommand.active = mode != 0;
        this.modeKeybinding.active = mode != 1;
        this.modeUseItem.active = mode != 2;
        this.modeCategory.active = mode != 3;

        textCommand.setVisible(mode == 0);
        commandClipboardButton.visible = mode == 0;
        keybindButton.visible = mode == 1;
        keybindToggleButton.visible = mode == 1;
        selectItemButton.visible = mode == 2;
        textCategory.setVisible(mode == 3);
    }

    @Override
    public void removed() {
        this.getMinecraft().keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public boolean isPauseScreen() {
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
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (p_keyPressed_1_ == GLFW.GLFW_KEY_ESCAPE) {
            ScreenStack.pop();
            return true;
        } else {
            return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partial) {
        this.renderBackground();
        this.textCommand.render(mouseX, mouseY, partial);
        this.textCategory.render(mouseX, mouseY, partial);
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
        if (mouseX > modeCommand.x && mouseX < modeCommand.x + modeCommand.getWidth() && mouseY > modeCommand.y && mouseY < modeCommand.y + modeCommand.getWidth()) {
            this.renderTooltip(Collections.singletonList("Click Action: Command"), mouseX, mouseY);
        } else if (mouseX > modeKeybinding.x && mouseX < modeKeybinding.x + modeKeybinding.getWidth() && mouseY > modeKeybinding.y && mouseY < modeKeybinding.y + modeKeybinding.getWidth()) {
            this.renderTooltip(Collections.singletonList("Click Action: KeyBinding"), mouseX, mouseY);
        } else if (mouseX > modeUseItem.x && mouseX < modeUseItem.x + modeUseItem.getWidth() && mouseY > modeUseItem.y && mouseY < modeUseItem.y + modeUseItem.getWidth()) {
            this.renderTooltip(Collections.singletonList("Click Action: Use Item"), mouseX, mouseY);
        } else if (mouseX > modeCategory.x && mouseX < modeCategory.x + modeCategory.getWidth() && mouseY > modeCategory.y && mouseY < modeCategory.y + modeCategory.getWidth()) {
            this.renderTooltip(Collections.singletonList("Click Action: Category"), mouseX, mouseY);
        }
    }
}