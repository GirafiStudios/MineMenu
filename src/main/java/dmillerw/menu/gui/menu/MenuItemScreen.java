package dmillerw.menu.gui.menu;

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
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

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
    public void tick() {
        this.textTitle.tick();

        this.buttonConfirm.visible = EditSessionData.clickAction != null;
        this.buttonDelete.visible = RadialMenu.getActiveArray()[slot] != null;
    }

    @Override
    @Nullable
    public IGuiEventListener getFocused() {
        return this.textTitle;
    }

    @Override
    public void init() {
        this.getMinecraft().keyboardListener.enableRepeatEvents(true);

        addButton(this.buttonConfirm = new Button(this.width / 2 - 4 - 150, this.height - 60, 100, 20, I18n.format("gui.done"), (screen) -> {
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
        addButton(this.buttonCancel = new Button(this.width / 2 + 4 + 50, this.height - 60, 100, 20, I18n.format("gui.cancel"), (screen) -> Minecraft.getInstance().displayGuiScreen(null)));
        addButton(this.buttonDelete = new Button(this.width / 2 - 50, this.height - 60, 100, 20, "Delete", (screen) -> {
            if (RadialMenu.getActiveArray()[slot] != null) {
                RadialMenu.getActiveArray()[slot].onRemoved();
                RadialMenu.getActiveArray()[slot] = null;
                MenuLoader.save(MineMenu.menuFile);
                Minecraft.getInstance().displayGuiScreen(null);
            }
        }));
        addButton(this.buttonPickIcon = new ItemButton(this.width / 2 - 4 - 40, this.height / 2, 20, 20, new ItemStack(Blocks.STONE), (screen) -> ScreenStack.push(new PickIconScreen())));

        String string = "Action";
        if (EditSessionData.clickAction != null) {
            if (EditSessionData.clickAction instanceof ClickActionCommand) {
                string = "Command";
            } else if (EditSessionData.clickAction instanceof ClickActionKey) {
                string = "Keybind";
            }
        }
        addButton(this.buttonClickAction = new Button(this.width / 2 - 20, this.height / 2, 100, 20, string, (screen) -> ScreenStack.push(new ClickActionScreen())));

        this.textTitle = new TextFieldWidget(this.font, this.width / 2 - 150, 50, 300, 20, "minemenu.menuItem.title");
        this.textTitle.setMaxStringLength(32767);
        this.textTitle.setText(EditSessionData.title != null && !EditSessionData.title.isEmpty() ? EditSessionData.title : "");

        this.buttonPickIcon.icon = EditSessionData.icon;
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
        if (this.textTitle.charTyped(key, keycode)) {
            EditSessionData.title = textTitle.getText().trim();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        super.mouseClicked(mx, my, button);

        this.textTitle.mouseClicked(mx, my, button);
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
        this.textTitle.render(mouseX, mouseY, partial);
        this.drawCenteredString(this.font, "Enter a title, then configure using the options below", this.width / 2, 80, 16777215);
        super.render(mouseX, mouseY, partial);
        GuiRenderHelper.renderHeaderAndFooter(this, 25, 20, 5, "Modifying Menu Item #" + slot);
    }
}