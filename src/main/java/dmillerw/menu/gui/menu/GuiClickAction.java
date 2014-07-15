package dmillerw.menu.gui.menu;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiUnicodeGlyphButton;
import cpw.mods.fml.client.config.GuiUtils;
import dmillerw.menu.data.click.CommandClickAction;
import dmillerw.menu.data.click.KeyClickAction;
import dmillerw.menu.helper.GuiRenderHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

/**
 * @author dmillerw
 */
public class GuiClickAction extends GuiScreen {

	private static final String KEYBOARD = "\u0182";

	public static KeyBinding keyBinding;

	private GuiTextField textCommand;

	private GuiButtonExt modeCommand;
	private GuiButtonExt modeKeybinding;

	private GuiButton keybindButton;

	private GuiButton buttonCancel;
	private GuiButton buttonConfirm;

	private boolean listeningForKey = false;

	private byte mode = 0;

	public GuiClickAction() {
		GuiClickAction.keyBinding = null;
	}

	@Override
	public void updateScreen() {
		this.textCommand.updateCursorCounter();
	}

	@Override
	public void initGui() {
		mode = (SessionData.clickAction != null && SessionData.clickAction instanceof KeyClickAction) || GuiClickAction.keyBinding != null ? (byte)1 : (byte)0;

		Keyboard.enableRepeatEvents(true);

		this.buttonList.clear();

		this.buttonList.add(this.buttonConfirm = new GuiButton(0, this.width / 2 - 4 - 150, this.height - 60, 150, 20, I18n.format("gui.done")));
		this.buttonList.add(this.buttonCancel = new GuiButton(1, this.width / 2 + 4, this.height - 60, 150, 20, I18n.format("gui.cancel")));
		String keyString = "";
		if (GuiClickAction.keyBinding != null) {
			keyString = keyBinding.getKeyDescription();
		} else {
			if (SessionData.clickAction != null && SessionData.clickAction instanceof KeyClickAction) {
				keyString = ((KeyClickAction)SessionData.clickAction).key;
			} else {
				keyString = "Select a key";
			}
		}
		this.buttonList.add(this.keybindButton = new GuiButton(2, this.width / 2 - 75, 50, 150, 20, keyString));

		this.buttonList.add(this.modeCommand = new GuiUnicodeGlyphButton(3, this.width / 2 - 30, this.height - 90, 20, 20, "", GuiUtils.RESET_CHAR, 1));
		this.buttonList.add(this.modeKeybinding = new GuiUnicodeGlyphButton(4, this.width / 2 + 10, this.height - 90, 20, 20, "", KEYBOARD, 1));

		this.textCommand = new GuiTextField(this.fontRendererObj, this.width / 2 - 150, 50, 300, 20);
		this.textCommand.setMaxStringLength(32767);
		this.textCommand.setFocused(true);
		this.textCommand.setText((SessionData.clickAction != null && SessionData.clickAction instanceof CommandClickAction) ? ((CommandClickAction)SessionData.clickAction).command : "");

		this.modeCommand.enabled = mode == 1;
		this.modeKeybinding.enabled = mode == 0;

		textCommand.setVisible(mode == 0);
		keybindButton.visible = mode == 1;
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
				// Keybinding
				mode = 1;
				modeKeybinding.enabled = false;
				modeCommand.enabled = true;
				textCommand.setVisible(false);
				keybindButton.visible = true;
			} else if (button.id == 3) {
				// Command
				mode = 0;
				modeKeybinding.enabled = true;
				modeCommand.enabled = false;
				textCommand.setVisible(true);
				keybindButton.visible = false;
			} if (button.id == 2) {
				GuiStack.push(new GuiPickKey());
			} else if (button.id == 1) {
				GuiStack.pop();
			} else if (button.id == 0) {
				if (mode == 0) {
					SessionData.clickAction = !(textCommand.getText().trim().isEmpty()) ? new CommandClickAction(textCommand.getText().trim()) : null;
				} else if (mode == 1 && GuiClickAction.keyBinding != null) {
					SessionData.clickAction = new KeyClickAction(keyBinding.getKeyDescription());
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
		GuiRenderHelper.renderHeaderAndFooter(this, 25, 20, 5);
		if (modeCommand.enabled && mouseX > modeCommand.xPosition && mouseX < modeCommand.xPosition + modeCommand.width && mouseY > modeCommand.yPosition && mouseY < modeCommand.yPosition + modeCommand.width) {
			this.func_146283_a(Arrays.asList("Click Action: Command"), mouseX, mouseY);
		} else if (modeKeybinding.enabled && mouseX > modeKeybinding.xPosition && mouseX < modeKeybinding.xPosition + modeKeybinding.width && mouseY > modeKeybinding.yPosition && mouseY < modeKeybinding.yPosition + modeKeybinding.width) {
			this.func_146283_a(Arrays.asList("Click Action: KeyBinding"), mouseX, mouseY);
		}
	}
}
