package dmillerw.menu.gui.menu.list;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dmillerw.menu.gui.menu.GuiClickAction;
import dmillerw.menu.gui.GuiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author dmillerw
 */
public class GuiControlList extends GuiListExtended {

	private final Minecraft mc;
	
	private final List<IGuiListEntry> list;
	
	private int maxWidth = 0;

	public GuiControlList(GuiScreen parent, Minecraft mc) {
		super(mc, parent.width, parent.height, 25, parent.height - 20, 20);
		this.mc = mc;
		this.list = new ArrayList<IGuiListEntry>();

		KeyBinding[] keyBindings = Minecraft.getMinecraft().gameSettings.keyBindings;
		Arrays.sort(keyBindings);

		String lastCategory = "";

		for (KeyBinding keybinding : keyBindings) {
			String category = keybinding.getKeyCategory();

			if (keybinding.getKeyCodeDefault() >= 0 && !keybinding.getKeyDescription().equalsIgnoreCase("key.open_menu")) {
				if (!category.equals(lastCategory)) {
					lastCategory = category;
					list.add(new CategoryEntry(category));
				}

				int l = mc.fontRenderer.getStringWidth(I18n.format(keybinding.getKeyDescription()));

				if (l > this.maxWidth) {
					this.maxWidth = l;
				}

				list.add(new KeyEntry(keybinding));
			}
		}
	}

	@Override
	protected int getSize() {
		return list.size();
	}

	@Override
	public GuiListExtended.IGuiListEntry getListEntry(int index) {
		return list.get(index);
	}

	@Override
	protected int getScrollBarX() {
		return super.getScrollBarX() + 15;
	}

	@Override
	public int getListWidth() {
		return super.getListWidth();
	}

	@Override
	protected void drawContainerBackground(Tessellator tessellator) {

	}

	@SideOnly(Side.CLIENT)
	public class CategoryEntry implements GuiListExtended.IGuiListEntry {

		private final String category;

		private final int width;

		public CategoryEntry(String category) {
			this.category = I18n.format(category);
			this.width = GuiControlList.this.mc.fontRenderer.getStringWidth(this.category);
		}

		@Override
		public void drawEntry(int p_148279_1_, int p_148279_2_, int p_148279_3_, int p_148279_4_, int p_148279_5_, Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_, boolean p_148279_9_) {
			GuiControlList.this.mc.fontRenderer.drawString(this.category, GuiControlList.this.mc.currentScreen.width / 2 - this.width / 2, p_148279_3_ + p_148279_5_ - GuiControlList.this.mc.fontRenderer.FONT_HEIGHT - 1, 16777215);
		}

		@Override
		public boolean mousePressed(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
			return false;
		}

		@Override
		public void mouseReleased(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_, int p_148277_6_) {

		}
	}

	@SideOnly(Side.CLIENT)
	public class KeyEntry implements GuiListExtended.IGuiListEntry {

		private final KeyBinding keyBinding;

		private final String description;

		private final GuiButton buttonSelect;

		private KeyEntry(KeyBinding keyBinding) {
			this.keyBinding = keyBinding;
			this.description = I18n.format(keyBinding.getKeyDescription());
			this.buttonSelect = new GuiButton(0, 0, 0, 75, 18, description);
		}

		@Override
		public void drawEntry(int p_148279_1_, int p_148279_2_, int p_148279_3_, int p_148279_4_, int p_148279_5_, Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_, boolean p_148279_9_) {
			GuiControlList.this.mc.fontRenderer.drawString(this.description, p_148279_2_ + 90 - GuiControlList.this.maxWidth, p_148279_3_ + p_148279_5_ / 2 - GuiControlList.this.mc.fontRenderer.FONT_HEIGHT / 2, 16777215);
			this.buttonSelect.xPosition = p_148279_2_ + 105;
			this.buttonSelect.yPosition = p_148279_3_;
			this.buttonSelect.displayString = GameSettings.getKeyDisplayString(this.keyBinding.getKeyCode());
			this.buttonSelect.drawButton(GuiControlList.this.mc, p_148279_7_, p_148279_8_);
		}

		@Override
		public boolean mousePressed(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
			if (buttonSelect.mousePressed(GuiControlList.this.mc, p_148278_2_, p_148278_3_)) {
				GuiClickAction.keyBinding = keyBinding;
				GuiStack.pop();
				return true;
			}
			return false;
		}

		@Override
		public void mouseReleased(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_, int p_148277_6_) {

		}
	}
}
