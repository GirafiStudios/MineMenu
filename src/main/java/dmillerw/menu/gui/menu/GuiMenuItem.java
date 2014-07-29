package dmillerw.menu.gui.menu;

import dmillerw.menu.data.menu.MenuItem;
import dmillerw.menu.data.menu.RadialMenu;
import dmillerw.menu.data.session.EditSessionData;
import dmillerw.menu.data.click.CommandClickAction;
import dmillerw.menu.data.click.KeyClickAction;
import dmillerw.menu.data.json.MenuLoader;
import dmillerw.menu.gui.GuiStack;
import dmillerw.menu.gui.menu.button.GuiItemButton;
import dmillerw.menu.helper.GuiRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

/**
 * @author dmillerw
 */
public class GuiMenuItem extends GuiScreen {

    public int slot;

    private GuiTextField textTitle;

    private GuiButton buttonCancel;
    private GuiButton buttonConfirm;
    private GuiButton buttonDelete;
    private GuiItemButton buttonPickIcon;
    private GuiButton buttonClickAction;

    public GuiMenuItem(int slot, MenuItem menuItem) {
        this.slot = slot;

        EditSessionData.fromMenuItem(menuItem);
    }

    @Override
    public void updateScreen() {
        this.textTitle.updateCursorCounter();
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        this.buttonList.clear();

        this.buttonList.add(this.buttonConfirm = new GuiButton(0, this.width / 2 - 4 - 150, this.height - 60, 100, 20, I18n.format("gui.done")));
        this.buttonList.add(this.buttonCancel = new GuiButton(1, this.width / 2 + 4 + 50, this.height - 60, 100, 20, I18n.format("gui.cancel")));
        this.buttonList.add(this.buttonDelete = new GuiButton(2, this.width / 2 - 50, this.height - 60, 100, 20, "Delete"));

        this.buttonList.add(this.buttonPickIcon = new GuiItemButton(3, this.width / 2 - 4 - 40, this.height / 2, 20, 20, new ItemStack(Blocks.stone)));
        String string = "Action";
        if (EditSessionData.clickAction != null) {
            if (EditSessionData.clickAction instanceof CommandClickAction) {
                string = "Command";
            } else if (EditSessionData.clickAction instanceof KeyClickAction) {
                string = "Keybind";
            }
        }
        this.buttonList.add(this.buttonClickAction = new GuiButton(4, this.width / 2 - 20, this.height / 2, 100, 20, string));

        this.textTitle = new GuiTextField(this.fontRendererObj, this.width / 2 - 150, 50, 300, 20);
        this.textTitle.setMaxStringLength(32767);
        this.textTitle.setFocused(false);
        this.textTitle.setText(EditSessionData.title != null && !EditSessionData.title.isEmpty() ? EditSessionData.title : "");

        this.buttonPickIcon.icon = EditSessionData.icon;

        this.buttonConfirm.enabled = !this.textTitle.getText().trim().isEmpty();
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
            if (button.id == 4) {
                GuiStack.push(new GuiClickAction());
            } else if (button.id == 3) {
                GuiStack.push(new GuiPickIcon());
            } else if (button.id == 2) {
                RadialMenu.getArray(RadialMenu.MAIN_TAG)[slot] = null;
                MenuLoader.save();
                Minecraft.getMinecraft().displayGuiScreen(null);
            } else if (button.id == 1) {
                Minecraft.getMinecraft().displayGuiScreen(null);
            } else if (button.id == 0) {
                if (!EditSessionData.title.trim().isEmpty() && EditSessionData.clickAction != null) {
                    RadialMenu.getArray(RadialMenu.MAIN_TAG)[slot] = EditSessionData.toMenuItem();
                }

                MenuLoader.save();
                Minecraft.getMinecraft().displayGuiScreen(null);
            }
        }
    }

    @Override
    protected void keyTyped(char key, int keycode) {
        if (this.textTitle.textboxKeyTyped(key, keycode)) {
            EditSessionData.title = textTitle.getText().trim();
        }

        this.buttonConfirm.enabled = this.textTitle.getText().trim().length() > 0;

        if (keycode != 28 && keycode != 156) {
            if (keycode == 1) {
                this.actionPerformed(this.buttonCancel);
            }
        }
    }

    @Override
    protected void mouseClicked(int mx, int my, int button) {
        super.mouseClicked(mx, my, button);

        this.textTitle.mouseClicked(mx, my, button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partial) {
        this.drawDefaultBackground();
        this.textTitle.drawTextBox();
        this.drawCenteredString(this.fontRendererObj, "Enter a title, then configure using the options below", this.width / 2, 80, 16777215);
        super.drawScreen(mouseX, mouseY, partial);
        GuiRenderHelper.renderHeaderAndFooter(this, 25, 20, 5, "Modifying Menu Item #" + slot);
    }
}
