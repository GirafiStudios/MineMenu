package dmillerw.menu.gui.menu;

import com.mojang.blaze3d.matrix.MatrixStack;
import dmillerw.menu.MineMenu;
import dmillerw.menu.data.click.ClickActionCommand;
import dmillerw.menu.data.click.ClickActionKey;
import dmillerw.menu.data.json.MenuLoader;
import dmillerw.menu.data.menu.MenuItem;
import dmillerw.menu.data.menu.RadialMenu;
import dmillerw.menu.data.session.EditSessionData;
import dmillerw.menu.gui.ScreenStack;
import dmillerw.menu.gui.menu.button.ItemButton;
import dmillerw.menu.helper.GuiRenderHelper;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MenuItemScreen extends Screen {
    private final int slot;
    private TextFieldWidget textTitle;
    private Button buttonCancel;
    private Button buttonConfirm;
    private Button buttonDelete;
    private ItemButton buttonPickIcon;
    private Button buttonClickAction;

    public MenuItemScreen(int slot, MenuItem menuItem) {
        super(new TranslationTextComponent("minemenu.itemScreen.title"));
        this.slot = slot;
        EditSessionData.fromMenuItem(menuItem);
    }

    @Override
    public void func_231023_e_() {
        this.textTitle.tick();

        this.buttonConfirm.field_230694_p_ = EditSessionData.clickAction != null;
        this.buttonDelete.field_230694_p_ = RadialMenu.getActiveArray()[slot] != null;
    }

    @Override
    @Nullable
    public IGuiEventListener func_241217_q_() {
        return this.textTitle;
    }

    @Override
    public void func_231160_c_() {
        this.getMinecraft().keyboardListener.enableRepeatEvents(true);

        func_230480_a_(this.buttonConfirm = new Button(this.field_230708_k_ / 2 - 4 - 150, this.field_230709_l_ - 60, 100, 20, new TranslationTextComponent("gui.done"), (screen) -> {
            if (EditSessionData.title.isEmpty()) {
                EditSessionData.title = "Menu Item #" + slot;
            }

            if (EditSessionData.clickAction != null) {
                if (RadialMenu.getActiveArray()[slot] != null) {
                    RadialMenu.getActiveArray()[slot].onRemoved();
                }
                RadialMenu.getActiveArray()[slot] = EditSessionData.toMenuItem();
            }
            MenuLoader.save(MineMenu.menuFile);
            Minecraft.getInstance().displayGuiScreen(null);
        }));
        func_230480_a_(this.buttonCancel = new Button(this.field_230708_k_ / 2 + 4 + 50, this.field_230709_l_ - 60, 100, 20, new TranslationTextComponent("gui.cancel"), (screen) -> Minecraft.getInstance().displayGuiScreen(null)));
        func_230480_a_(this.buttonDelete = new Button(this.field_230708_k_ / 2 - 50, this.field_230709_l_ - 60, 100, 20, new StringTextComponent("Delete"), (screen) -> {
            if (RadialMenu.getActiveArray()[slot] != null) {
                RadialMenu.getActiveArray()[slot].onRemoved();
                RadialMenu.getActiveArray()[slot] = null;
                MenuLoader.save(MineMenu.menuFile);
                Minecraft.getInstance().displayGuiScreen(null);
            }
        }));
        func_230480_a_(this.buttonPickIcon = new ItemButton(this.field_230708_k_ / 2 - 4 - 40, this.field_230709_l_ / 2, 20, 20, new ItemStack(Blocks.STONE), (screen) -> ScreenStack.push(new PickIconScreen())));

        ITextComponent string = new StringTextComponent("Action");
        if (EditSessionData.clickAction != null) {
            if (EditSessionData.clickAction instanceof ClickActionCommand) {
                string = new StringTextComponent("Command");
            } else if (EditSessionData.clickAction instanceof ClickActionKey) {
                string = new StringTextComponent("Keybind");
            }
        }
        func_230480_a_(this.buttonClickAction = new Button(this.field_230708_k_ / 2 - 20, this.field_230709_l_ / 2, 100, 20, string, (screen) -> ScreenStack.push(new ClickActionScreen())));

        this.textTitle = new TextFieldWidget(this.field_230712_o_, this.field_230708_k_ / 2 - 150, 50, 300, 20, new TranslationTextComponent("minemenu.menuItem.title"));
        this.textTitle.setMaxStringLength(32767);
        this.textTitle.setText(EditSessionData.title != null && !EditSessionData.title.isEmpty() ? EditSessionData.title : "");

        this.buttonPickIcon.icon = EditSessionData.icon;
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
        if (this.textTitle.func_231042_a_(key, keycode)) {
            EditSessionData.title = textTitle.getText().trim();
            return true;
        }
        return false;
    }

    @Override
    public boolean func_231044_a_(double mx, double my, int button) {
        super.func_231044_a_(mx, my, button);

        this.textTitle.func_231044_a_(mx, my, button);
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
        this.textTitle.func_230430_a_(matrixStack, mouseX, mouseY, partial);
        this.func_238471_a_(matrixStack, this.field_230712_o_, "Enter a title, then configure using the options below", this.field_230708_k_ / 2, 80, 16777215);
        super.func_230430_a_(matrixStack, mouseX, mouseY, partial);
        GuiRenderHelper.renderHeaderAndFooter(matrixStack, this, 25, 20, 5, "Modifying Menu Item #" + slot);
    }
}