package dmillerw.menu.gui.menu.list;

import dmillerw.menu.gui.GuiStack;
import dmillerw.menu.gui.menu.GuiClickAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

                int l = mc.fontRendererObj.getStringWidth(I18n.format(keybinding.getKeyDescription()));

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
            this.width = GuiControlList.this.mc.fontRendererObj.getStringWidth(this.category);
        }

        @Override
        public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
        }

        @Override
        public void drawEntry(int x, int y, int z, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
            GuiControlList.this.mc.fontRendererObj.drawString(this.category, GuiControlList.this.mc.currentScreen.width / 2 - this.width / 2, z + slotHeight - GuiControlList.this.mc.fontRendererObj.FONT_HEIGHT - 1, 16777215);
        }

        @Override
        public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
            return false;
        }

        @Override
        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {

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
        public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
        }

        @Override
        public void drawEntry(int x, int y, int z, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
            GuiControlList.this.mc.fontRendererObj.drawString(this.description, y + 90 - GuiControlList.this.maxWidth, z + slotHeight / 2 - GuiControlList.this.mc.fontRendererObj.FONT_HEIGHT / 2, 16777215);
            this.buttonSelect.xPosition = y + 105;
            this.buttonSelect.yPosition = z;
            this.buttonSelect.displayString = GameSettings.getKeyDisplayString(this.keyBinding.getKeyCode());
            this.buttonSelect.drawButton(GuiControlList.this.mc, mouseX, mouseY);
        }

        @Override
        public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
            if (buttonSelect.mousePressed(GuiControlList.this.mc, p_148278_2_, p_148278_3_)) {
                GuiClickAction.keyBinding = keyBinding;
                GuiStack.pop();
                return true;
            }
            return false;
        }

        @Override
        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {

        }
    }
}
