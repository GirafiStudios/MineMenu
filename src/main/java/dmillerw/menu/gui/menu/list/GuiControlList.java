package dmillerw.menu.gui.menu.list;

import dmillerw.menu.gui.GuiStack;
import dmillerw.menu.gui.menu.GuiClickAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiControlList extends GuiListExtended {
    private final Minecraft mc;
    private final List<IGuiListEntry> list;
    private int maxWidth = 0;

    public GuiControlList(GuiScreen parent, Minecraft mc) {
        super(mc, parent.width, parent.height, 25, parent.height - 20, 20);
        this.mc = mc;
        this.list = new ArrayList<>();

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

                int width = mc.fontRenderer.getStringWidth(I18n.format(keybinding.getKeyDescription()));

                if (width > this.maxWidth) {
                    this.maxWidth = width;
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
    @Nonnull
    public GuiListExtended.IGuiListEntry getListEntry(int index) {
        return list.get(index);
    }

    @Override
    protected int getScrollBarX() {
        return super.getScrollBarX() + 15;
    }

    @Override
    protected void drawContainerBackground(Tessellator tessellator) {
    }

    @SideOnly(Side.CLIENT)
    public class CategoryEntry implements GuiListExtended.IGuiListEntry {
        private final String category;
        private final int width;

        CategoryEntry(String category) {
            this.category = I18n.format(category);
            this.width = GuiControlList.this.mc.fontRenderer.getStringWidth(this.category);
        }

        @Override
        public void updatePosition(int x, int y, int z, float partial) {
        }

        @Override
        public void drawEntry(int x, int y, int z, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
            if (GuiControlList.this.mc.currentScreen != null) {
                GuiControlList.this.mc.fontRenderer.drawString(this.category, GuiControlList.this.mc.currentScreen.width / 2 - this.width / 2, z + slotHeight - GuiControlList.this.mc.fontRenderer.FONT_HEIGHT - 1, 16777215);
            }
        }

        @Override
        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
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

        KeyEntry(KeyBinding keyBinding) {
            this.keyBinding = keyBinding;
            this.description = I18n.format(keyBinding.getKeyDescription());
            this.buttonSelect = new GuiButton(0, 0, 0, 95, 18, description);
        }

        @Override
        public void updatePosition(int x, int y, int z, float partial) {
        }

        @Override
        public void drawEntry(int x, int y, int z, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
            GuiControlList.this.mc.fontRenderer.drawString(this.description, y + 90 - GuiControlList.this.maxWidth, z + slotHeight / 2 - GuiControlList.this.mc.fontRenderer.FONT_HEIGHT / 2, 16777215);
            this.buttonSelect.x = y + 105;
            this.buttonSelect.y = z;
            this.buttonSelect.displayString = this.keyBinding.getDisplayName();
            this.buttonSelect.drawButton(GuiControlList.this.mc, mouseX, mouseY, partialTicks);
        }

        @Override
        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
            if (buttonSelect.mousePressed(GuiControlList.this.mc, mouseX, mouseY)) {
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