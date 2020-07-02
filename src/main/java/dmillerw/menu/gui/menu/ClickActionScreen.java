package dmillerw.menu.gui.menu;

import com.mojang.blaze3d.matrix.MatrixStack;
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
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
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
    public void func_231023_e_() {
        this.textCommand.tick();
        this.textCategory.tick();
    }

    @Override
    @Nullable
    public IGuiEventListener func_241217_q_() {
        if (mode == 0) {
            return this.textCommand;
        } else if (mode == 3) {
            return this.textCategory;
        } else {
            return super.func_241217_q_();
        }
    }

    @Override
    public void func_231160_c_() {
        if (ClickActionScreen.keyBinding != null) {
            mode = ClickAction.KEYBIND.ordinal();
        } else if (!ClickActionScreen.item.isEmpty()) {
            mode = ClickAction.ITEM_USE.ordinal();
        } else {
            mode = EditSessionData.clickAction != null ? EditSessionData.clickAction.getClickAction().ordinal() : 0;
        }

        this.getMinecraft().keyboardListener.enableRepeatEvents(true);

        func_230480_a_(this.buttonConfirm = new Button(this.field_230708_k_ / 2 - 4 - 150, this.field_230709_l_ - 60, 150, 20, new TranslationTextComponent("gui.done"), (screen) -> {
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

        func_230480_a_(this.buttonCancel = new Button(this.field_230708_k_ / 2 + 4, this.field_230709_l_ - 60, 150, 20, new TranslationTextComponent("gui.cancel"), (screen) -> ScreenStack.pop()));

        ITextComponent commandString;
        if (EditSessionData.clickAction instanceof ClickActionCommand) {
            commandString = new StringTextComponent(((ClickActionCommand) EditSessionData.clickAction).clipboard ? "Clipboard" : "Send");
        } else {
            commandString = new StringTextComponent(clipboard ? "Clipboard" : "Send");
        }
        func_230480_a_(this.commandClipboardButton = new Button(this.field_230708_k_ / 2 - 75, 80, 150, 20, commandString, (screen) -> {
            clipboard = !clipboard;
            commandClipboardButton.func_238482_a_(new StringTextComponent(clipboard ? "Clipboard" : "Send"));
        }));

        ITextComponent keyString;
        if (ClickActionScreen.keyBinding != null) {
            keyString = new TranslationTextComponent(keyBinding.getKeyDescription());
        } else {
            if (EditSessionData.clickAction instanceof ClickActionKey) {
                keyString = new TranslationTextComponent(((ClickActionKey) EditSessionData.clickAction).key);
            } else {
                keyString = new StringTextComponent("Select a key");
            }
        }
        func_230480_a_(this.keybindButton = new Button(this.field_230708_k_ / 2 - 75, 50, 150, 20, keyString, (screen) -> ScreenStack.push(new PickKeyScreen())));

        ITextComponent keyToggleString;
        if (EditSessionData.clickAction instanceof ClickActionKey) {
            keyToggleString = new StringTextComponent(((ClickActionKey) EditSessionData.clickAction).toggle ? "Toggle" : "Press");
        } else {
            keyToggleString = new StringTextComponent(toggle ? "Toggle" : "Press");
        }
        func_230480_a_(this.keybindToggleButton = new Button(this.field_230708_k_ / 2 - 75, 80, 150, 20, keyToggleString, (screen) -> {
            toggle = !toggle;
            keybindToggleButton.func_238482_a_(new StringTextComponent(toggle ? "Toggle" : "Press"));
        }));

        ITextComponent itemString;
        if (!ClickActionScreen.item.isEmpty()) {
            itemString = new StringTextComponent("Item: " + item.getDisplayName().getString());
        } else {
            if (EditSessionData.clickAction != null && EditSessionData.clickAction.getClickAction() == ClickAction.ITEM_USE) {
                itemString = new StringTextComponent("Item: " + ((ClickActionUseItem) EditSessionData.clickAction).stack.getItem().getName().getString());
            } else {
                itemString = new StringTextComponent("Select a Slot");
            }
        }
        func_230480_a_(this.selectItemButton = new Button(this.field_230708_k_ / 2 - 75, 50, 150, 20, itemString, (screen) -> ScreenStack.push(new PickItemScreen())));

        func_230480_a_(this.modeCommand = new ItemButton(this.field_230708_k_ / 2 - 55, this.field_230709_l_ - 90, 20, 20, new ItemStack(Items.PAPER), (screen) -> {
            // Command
            mode = 0;

            modeCategory.field_230693_o_ = true;
            modeUseItem.field_230693_o_ = true;
            modeKeybinding.field_230693_o_ = true;
            modeCommand.field_230693_o_ = false;

            textCategory.setVisible(false);
            selectItemButton.field_230694_p_ = false;
            textCommand.setVisible(true);
            commandClipboardButton.field_230694_p_ = true;
            keybindButton.field_230694_p_ = false;
            keybindToggleButton.field_230694_p_ = false;
        }));

        func_230480_a_(this.modeKeybinding = new ItemButton(this.field_230708_k_ / 2 - 25, this.field_230709_l_ - 90, 20, 20, new ItemStack(Blocks.OAK_BUTTON), (screen) -> {
            // Keybinding
            mode = 1;

            modeCategory.field_230693_o_ = true;
            modeUseItem.field_230693_o_ = true;
            modeKeybinding.field_230693_o_ = false;
            modeCommand.field_230693_o_ = true;

            textCategory.setVisible(false);
            selectItemButton.field_230694_p_ = false;
            textCommand.setVisible(false);
            commandClipboardButton.field_230694_p_ = false;
            keybindButton.field_230694_p_ = true;
            keybindToggleButton.field_230694_p_ = true;
        }));

        func_230480_a_(this.modeUseItem = new ItemButton(this.field_230708_k_ / 2 + 5, this.field_230709_l_ - 90, 20, 20, new ItemStack(Items.DIAMOND_SWORD), (screen) -> {
            // Select item
            mode = 2;

            modeCategory.field_230693_o_ = true;
            modeUseItem.field_230693_o_ = false;
            modeKeybinding.field_230693_o_ = true;
            modeCommand.field_230693_o_ = true;

            textCategory.setVisible(false);
            selectItemButton.field_230694_p_ = true;
            textCommand.setVisible(false);
            commandClipboardButton.field_230694_p_ = false;
            keybindButton.field_230694_p_ = false;
            keybindToggleButton.field_230694_p_ = false;
        }));

        func_230480_a_(this.modeCategory = new ItemButton(this.field_230708_k_ / 2 + 35, this.field_230709_l_ - 90, 20, 20, new ItemStack(Blocks.CHEST), (screen) -> {
            // Category
            mode = 3;

            modeCategory.field_230693_o_ = false;
            modeUseItem.field_230693_o_ = true;
            modeKeybinding.field_230693_o_ = true;
            modeCommand.field_230693_o_ = true;

            textCategory.setVisible(true);
            selectItemButton.field_230694_p_ = false;
            textCommand.setVisible(false);
            commandClipboardButton.field_230694_p_ = false;
            keybindButton.field_230694_p_ = false;
            keybindToggleButton.field_230694_p_ = false;
        }));

        this.textCommand = new TextFieldWidget(this.field_230712_o_, this.field_230708_k_ / 2 - 150, 50, 300, 20, new TranslationTextComponent("minemenu.action.command"));
        this.textCommand.setMaxStringLength(32767);
        this.textCommand.func_231049_c__(true);
        this.textCommand.setText((EditSessionData.clickAction instanceof ClickActionCommand) ? ((ClickActionCommand) EditSessionData.clickAction).command : "");

        this.textCategory = new TextFieldWidget(this.field_230712_o_, this.field_230708_k_ / 2 - 150, 50, 300, 20, new TranslationTextComponent( "minemenu.action.category"));
        this.textCategory.setMaxStringLength(32767);
        this.textCategory.func_231049_c__(true);
        this.textCategory.setText((EditSessionData.clickAction instanceof ClickActionCategory) ? ((ClickActionCategory) EditSessionData.clickAction).category : "");

        this.modeCommand.field_230693_o_ = mode != 0;
        this.modeKeybinding.field_230693_o_ = mode != 1;
        this.modeUseItem.field_230693_o_ = mode != 2;
        this.modeCategory.field_230693_o_ = mode != 3;

        textCommand.setVisible(mode == 0);
        commandClipboardButton.field_230694_p_ = mode == 0;
        keybindButton.field_230694_p_ = mode == 1;
        keybindToggleButton.field_230694_p_ = mode == 1;
        selectItemButton.field_230694_p_ = mode == 2;
        textCategory.setVisible(mode == 3);
    }

    @Override
    public void func_231164_f_() {
        this.getMinecraft().keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public boolean func_231177_au__() {
        return false;
    }

    @Override
    public boolean func_231042_a_(char key, int keycode) {
        this.textCommand.func_231042_a_(key, keycode);
        this.textCategory.func_231042_a_(key, keycode);
        return true;
    }

    @Override
    public boolean func_231044_a_(double mx, double my, int button) {
        super.func_231044_a_(mx, my, button);

        this.textCommand.func_231044_a_(mx, my, button);
        return true;
    }

    @Override
    public boolean func_231046_a_(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (p_keyPressed_1_ == GLFW.GLFW_KEY_ESCAPE) {
            ScreenStack.pop();
            return true;
        } else {
            return super.func_231046_a_(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        }
    }

    @Override
    public void func_230430_a_(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partial) {
        this.func_230446_a_(matrixStack);
        this.textCommand.func_230430_a_(matrixStack, mouseX, mouseY, partial);
        this.textCategory.func_230430_a_(matrixStack, mouseX, mouseY, partial);
        super.func_230430_a_(matrixStack, mouseX, mouseY, partial);
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
        GuiRenderHelper.renderHeaderAndFooter(matrixStack, this, 25, 20, 5, header);
        if (mouseX > modeCommand.field_230690_l_ && mouseX < modeCommand.field_230690_l_ + modeCommand.func_230998_h_() && mouseY > modeCommand.field_230691_m_ && mouseY < modeCommand.field_230691_m_ + modeCommand.func_230998_h_()) {
            this.func_238654_b_(matrixStack, Collections.singletonList(new StringTextComponent("Click Action: Command")), mouseX, mouseY);
        } else if (mouseX > modeKeybinding.field_230690_l_ && mouseX < modeKeybinding.field_230690_l_ + modeKeybinding.func_230998_h_() && mouseY > modeKeybinding.field_230691_m_ && mouseY < modeKeybinding.field_230691_m_ + modeKeybinding.func_230998_h_()) {
            this.func_238654_b_(matrixStack, Collections.singletonList(new StringTextComponent("Click Action: KeyBinding")), mouseX, mouseY);
        } else if (mouseX > modeUseItem.field_230690_l_ && mouseX < modeUseItem.field_230690_l_ + modeUseItem.func_230998_h_() && mouseY > modeUseItem.field_230691_m_ && mouseY < modeUseItem.field_230691_m_ + modeUseItem.func_230998_h_()) {
            this.func_238654_b_(matrixStack, Collections.singletonList(new StringTextComponent("Click Action: Use Item")), mouseX, mouseY);
        } else if (mouseX > modeCategory.field_230690_l_ && mouseX < modeCategory.field_230690_l_ + modeCategory.func_230998_h_() && mouseY > modeCategory.field_230691_m_ && mouseY < modeCategory.field_230691_m_ + modeCategory.func_230998_h_()) {
            this.func_238654_b_(matrixStack, Collections.singletonList(new StringTextComponent("Click Action: Category")), mouseX, mouseY);
        }
    }
}