package dmillerw.menu.gui.menu;

import dmillerw.menu.helper.ItemRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author dmillerw
 */
public class GuiPickIcon extends GuiScreen {

	public ItemStack icon;

	private GuiTextField textSearch;

	private GuiButton buttonCancel;

	private List<ItemStack> stacks;

	private int listScrollIndex = 0;

	@Override
	public void updateScreen() {
		this.textSearch.updateCursorCounter();
	}

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);

		stacks = new ArrayList<ItemStack>();

		Iterator iterator = Item.itemRegistry.iterator();
		while (iterator.hasNext()) {
			Item item = (Item) iterator.next();

			if (item != null && item.getCreativeTab() != null) {
				item.getSubItems(item, null, stacks);
			}
		}

		this.buttonList.clear();

		this.buttonList.add(this.buttonCancel = new GuiButton(0, this.width / 2 - 75, this.height / 4 + 120 + 12, 150, 20, I18n.format("gui.cancel")));

		this.textSearch = new GuiTextField(this.fontRendererObj, this.width / 2 - 150, 30, 300, 20);
		this.textSearch.setMaxStringLength(32767);
		this.textSearch.setFocused(true);
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
			if (button.id == 0) {
				GuiStack.pop();
			}
		}
	}

	@Override
	protected void keyTyped(char key, int keycode) {
		if (this.textSearch.textboxKeyTyped(key, keycode)) {
			listScrollIndex = 0;

			if (!textSearch.getText().trim().isEmpty()) {
				stacks.clear();

				ArrayList<ItemStack> temp = new ArrayList<ItemStack>();
				Iterator iterator = Item.itemRegistry.iterator();

				while (iterator.hasNext()) {
					Item item = (Item) iterator.next();

					if (item != null && item.getCreativeTab() != null) {
						item.getSubItems(item, null, temp);
					}
				}

				for (ItemStack stack : temp) {
					if (stack != null && stack.getDisplayName().toLowerCase().contains(textSearch.getText().toLowerCase())) {
						stacks.add(stack);
					}
				}
			} else {
				stacks.clear();

				Iterator iterator = Item.itemRegistry.iterator();

				while (iterator.hasNext()) {
					Item item = (Item) iterator.next();

					if (item != null && item.getCreativeTab() != null) {
						item.getSubItems(item, null, stacks);
					}
				}
			}
		}

		if (keycode != 28 && keycode != 156) {
			if (keycode == 1) {
				this.actionPerformed(this.buttonCancel);
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);

		if (!buttonCancel.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) {
			icon = getClickedStack(this.width / 2, textSearch.yPosition + 40, mouseX, mouseY);
		}

		if (icon != null || buttonCancel.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) {
			GuiStack.pop();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partial) {
		onWheelScrolled(mouseX, mouseY, Mouse.getDWheel());

		this.drawDefaultBackground();
		this.textSearch.drawTextBox();

		super.drawScreen(mouseX, mouseY, partial);

		drawList(this, this.width / 2, textSearch.yPosition + 40, mouseX, mouseY);
	}

	public void onWheelScrolled(int x, int y, int wheel) {
		wheel = -wheel;

		if (wheel < 0) {
			listScrollIndex -= 2;
			if (listScrollIndex < 0) {
				listScrollIndex = 0;
			}
		}

		if (wheel > 0) {
			listScrollIndex += 2;
			if (listScrollIndex > Math.max(0, (stacks.size() / 14)) - 5) {
				listScrollIndex = Math.max(0, (stacks.size() / 14) - 5);
			}
		}
	}

	private void drawList(GuiPickIcon gui, int x, int y, int mx, int my) {
		ItemStack highlighted = null;
		float highlightedX = 0;
		float highlightedY = 0;

		for (int i = 14 * listScrollIndex; i < stacks.size(); i++) {
			int drawX = i % 14;
			int drawY = i / 14;

			if (((i - 14 * listScrollIndex) / 14) <= 5) {
				GL11.glPushMatrix();

				boolean scaled = false;
				float actualDrawX = (x + drawX * 20) - (7 * 20) + 10;
				float actualDrawY = (y + drawY * 20);
				actualDrawY -= 20 * listScrollIndex;

				if (mx > (actualDrawX - 8) && mx < (actualDrawX + 20 - 8) && my > actualDrawY - 8 && my < actualDrawY + 20 - 8) {
					scaled = true;
					highlighted = stacks.get(i);
					highlightedX = actualDrawX / 2;
					highlightedY = actualDrawY / 2;
				}

				if (!scaled) {
					ItemRenderHelper.renderItem(actualDrawX, actualDrawY, gui.zLevel, stacks.get(i));
				}

				GL11.glPopMatrix();
			} else {
				break;
			}
		}

		if (highlighted != null) {
			GL11.glPushMatrix();
			GL11.glScaled(2, 2, 2);
			ItemRenderHelper.renderItem(highlightedX, highlightedY, gui.zLevel, highlighted);
			GL11.glPopMatrix();
		}
	}

	private ItemStack getClickedStack(int x, int y, int mx, int my) {
		for (int i = 14 * listScrollIndex; i < stacks.size(); i++) {
			int drawX = i % 14;
			int drawY = i / 14;

			if (((i - 14 * listScrollIndex) / 14) <= 5) {
				float actualDrawX = (x + drawX * 20) - (7 * 20) + 10;
				float actualDrawY = (y + drawY * 20);
				actualDrawY -= 20 * listScrollIndex;

				if (mx > (actualDrawX - 8) && mx < (actualDrawX + 20 - 8) && my > actualDrawY - 8 && my < actualDrawY + 20 - 8) {
					return stacks.get(i);
				}
			}
		}

		return null;
	}
}
