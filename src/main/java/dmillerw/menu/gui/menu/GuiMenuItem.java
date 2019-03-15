package dmillerw.menu.gui.menu;

import dmillerw.menu.MineMenu;
import dmillerw.menu.data.click.ClickActionCommand;
import dmillerw.menu.data.click.ClickActionKey;
import dmillerw.menu.data.json.MenuLoader;
import dmillerw.menu.data.menu.MenuItem;
import dmillerw.menu.data.menu.RadialMenu;
import dmillerw.menu.data.session.EditSessionData;
import dmillerw.menu.gui.GuiStack;
import dmillerw.menu.gui.menu.button.GuiItemButton;
import dmillerw.menu.helper.GuiRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

public class GuiMenuItem extends GuiScreen {
    private final int slot;
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
    public void tick() {
        this.textTitle.tick();

        this.buttonConfirm.enabled = EditSessionData.clickAction != null;
        this.buttonDelete.enabled = RadialMenu.getActiveArray()[slot] != null;
    }

    @Override
    @Nullable
    public IGuiEventListener getFocused() {
        return this.textTitle;
    }

    @Override
    public void initGui() {
        this.mc.keyboardListener.enableRepeatEvents(true);

        addButton(this.buttonConfirm = new GuiButton(0, this.width / 2 - 4 - 150, this.height - 60, 100, 20, I18n.format("gui.done")) {
            @Override
            public void onClick(double mouseX, double mouseY) {
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
            }
        });
        addButton(this.buttonCancel = new GuiButton(1, this.width / 2 + 4 + 50, this.height - 60, 100, 20, I18n.format("gui.cancel")) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                Minecraft.getInstance().displayGuiScreen(null);
            }
        });
        addButton(this.buttonDelete = new GuiButton(2, this.width / 2 - 50, this.height - 60, 100, 20, "Delete") {
            @Override
            public void onClick(double mouseX, double mouseY) {
                if (RadialMenu.getActiveArray()[slot] != null) {
                    RadialMenu.getActiveArray()[slot].onRemoved();
                    RadialMenu.getActiveArray()[slot] = null;
                    MenuLoader.save(MineMenu.menuFile);
                    Minecraft.getInstance().displayGuiScreen(null);
                }
            }
        });

        addButton(this.buttonPickIcon = new GuiItemButton(3, this.width / 2 - 4 - 40, this.height / 2, 20, 20, new ItemStack(Blocks.STONE)) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                GuiStack.push(new GuiPickIcon());
            }
        });
        String string = "Action";
        if (EditSessionData.clickAction != null) {
            if (EditSessionData.clickAction instanceof ClickActionCommand) {
                string = "Command";
            } else if (EditSessionData.clickAction instanceof ClickActionKey) {
                string = "Keybind";
            }
        }
        addButton(this.buttonClickAction = new GuiButton(4, this.width / 2 - 20, this.height / 2, 100, 20, string) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                GuiStack.push(new GuiClickAction());
            }
        });

        this.textTitle = new GuiTextField(9, this.fontRenderer, this.width / 2 - 150, 50, 300, 20);
        this.textTitle.setMaxStringLength(32767);
        this.textTitle.setFocused(false);
        this.textTitle.setText(EditSessionData.title != null && !EditSessionData.title.isEmpty() ? EditSessionData.title : "");

        this.buttonPickIcon.icon = EditSessionData.icon;
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
            GuiStack.pop();
            return true;
        } else {
            return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partial) {
        this.drawDefaultBackground();
        this.textTitle.drawTextField(mouseX, mouseY, partial);
        this.drawCenteredString(this.fontRenderer, "Enter a title, then configure using the options below", this.width / 2, 80, 16777215);
        super.render(mouseX, mouseY, partial);
        GuiRenderHelper.renderHeaderAndFooter(this, 25, 20, 5, "Modifying Menu Item #" + slot);
    }
}