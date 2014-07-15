package dmillerw.menu.gui.menu;

import dmillerw.menu.data.MenuItem;
import dmillerw.menu.data.RadialMenu;
import dmillerw.menu.data.click.CommandClickAction;
import dmillerw.menu.data.click.KeyClickAction;
import dmillerw.menu.data.json.MenuLoader;
import dmillerw.menu.gui.menu.button.GuiItemButton;
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

	public MenuItem menuItem;

	private GuiPickIcon pickIcon;
	private GuiClickAction clickAction;

	private GuiTextField textTitle;

	private GuiButton buttonCancel;
	private GuiButton buttonConfirm;
	private GuiButton buttonDelete;
	private GuiItemButton buttonPickIcon;
	private GuiButton buttonClickAction;

	public GuiMenuItem(int slot, MenuItem menuItem) {
		this.slot = slot;
		this.menuItem = menuItem;
		this.pickIcon = new GuiPickIcon();
		this.pickIcon.icon = (this.menuItem != null ? menuItem.icon : new ItemStack(Blocks.stone));
		this.clickAction = new GuiClickAction();
		this.clickAction.clickAction = (this.menuItem != null ? this.menuItem.clickAction : null);
	}

	@Override
	public void updateScreen() {
		this.textTitle.updateCursorCounter();
	}

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);

		this.buttonList.clear();

		this.buttonList.add(this.buttonConfirm = new GuiButton(0, this.width / 2 - 4 - 150, this.height - 70, 100, 20, I18n.format("gui.done")));
		this.buttonList.add(this.buttonCancel = new GuiButton(1,  this.width / 2 + 4 + 50,  this.height - 70, 100, 20, I18n.format("gui.cancel")));
		this.buttonList.add(this.buttonDelete = new GuiButton(2,  this.width / 2 - 50,      this.height - 70, 100, 20, "Delete"));

		this.buttonList.add(this.buttonPickIcon = new GuiItemButton(3, this.width / 2 - 4 - 46, this.height / 2 - 3, 26, 26, new ItemStack(Blocks.stone)));
		String string = "Click Action";
		if (clickAction.clickAction != null) {
			if (clickAction.clickAction instanceof CommandClickAction) {
				string = "Command";
			} else if (clickAction.clickAction instanceof KeyClickAction) {
				string = "KeyBind";
			}
		}
		this.buttonList.add(this.buttonClickAction = new GuiButton(4, this.width / 2 - 20, this.height / 2, 100, 20, string));

		this.textTitle = new GuiTextField(this.fontRendererObj, this.width / 2 - 150, 50, 300, 20);
		this.textTitle.setMaxStringLength(32767);
		this.textTitle.setFocused(false);
		this.textTitle.setText(this.menuItem != null ? menuItem.title : "");

		this.buttonPickIcon.icon = this.pickIcon.icon;

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
				GuiStack.push(clickAction);
			} else if (button.id == 3) {
				GuiStack.push(pickIcon);
			} else if (button.id == 2) {
				RadialMenu.menuItems[slot] = null;
				MenuLoader.save();
				Minecraft.getMinecraft().displayGuiScreen(null);
			} else if (button.id == 1) {
				Minecraft.getMinecraft().displayGuiScreen(null);
			} else if (button.id == 0) {
				menuItem = new MenuItem(this.textTitle.getText(), pickIcon.icon, clickAction.clickAction);
				RadialMenu.menuItems[slot] = menuItem;

				MenuLoader.save();
				Minecraft.getMinecraft().displayGuiScreen(null);
			}
		}
	}

	@Override
	protected void keyTyped(char key, int keycode) {
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
		super.drawScreen(mouseX, mouseY, partial);
	}
}
