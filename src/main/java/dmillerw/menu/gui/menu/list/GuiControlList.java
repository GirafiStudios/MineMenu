package dmillerw.menu.gui.menu.list;

import dmillerw.menu.gui.GuiStack;
import dmillerw.menu.gui.menu.GuiClickAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiKeyBindingList;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
public class GuiControlList extends GuiListExtended<GuiKeyBindingList.Entry> {
    private final Minecraft mc;
    private int maxWidth = 0;

    public GuiControlList(GuiScreen parent, Minecraft mc) {
        super(mc, parent.width, parent.height, 25, parent.height - 20, 20);
        this.mc = mc;

        KeyBinding[] keyBindings = Minecraft.getInstance().gameSettings.keyBindings;
        Arrays.sort(keyBindings);
        String lastCategory = "";

        for (KeyBinding keybinding : keyBindings) {
            String category = keybinding.getKeyCategory();

            if (!keybinding.getKeyDescription().equalsIgnoreCase("key.open_menu")) {
                if (!category.equals(lastCategory)) {
                    lastCategory = category;
                    this.addEntry(new CategoryEntry(category));
                }

                int width = mc.fontRenderer.getStringWidth(I18n.format(keybinding.getKeyDescription()));

                if (width > this.maxWidth) {
                    this.maxWidth = width;
                }
                this.addEntry(new KeyEntry(keybinding));
            }
        }
    }

    @Override
    public int getListWidth() {
        return super.getListWidth() + 32;
    }

    @Override
    protected int getScrollBarX() {
        return super.getScrollBarX() + 15;
    }

    @Override
    protected void drawContainerBackground(Tessellator tessellator) {
    }

    @OnlyIn(Dist.CLIENT)
    public class CategoryEntry extends GuiKeyBindingList.Entry {
        private final String category;
        private final int width;

        CategoryEntry(String category) {
            this.category = I18n.format(category);
            this.width = GuiControlList.this.mc.fontRenderer.getStringWidth(this.category);
        }

        @Override
        public void drawEntry(int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks) {
            if (GuiControlList.this.mc.currentScreen != null) {
                GuiControlList.this.mc.fontRenderer.drawString(this.category, (float) (GuiControlList.this.mc.currentScreen.width / 2 - this.width / 2), this.getY() + slotHeight - GuiControlList.this.mc.fontRenderer.FONT_HEIGHT - 1, 16777215);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class KeyEntry extends GuiKeyBindingList.Entry {
        private final KeyBinding keyBinding;
        private final String description;
        private final GuiButton buttonSelect;

        KeyEntry(KeyBinding keyBinding) {
            this.keyBinding = keyBinding;
            this.description = I18n.format(keyBinding.getKeyDescription());
            this.buttonSelect = new GuiButton(0, 0, 0, 95, 18, description) {
                @Override
                public void onClick(double mouseX, double mouseY) {
                    GuiClickAction.keyBinding = keyBinding;
                    GuiStack.pop();
                }
            };
        }

        @Override
        public void drawEntry(int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks) {
            int y = this.getY();
            int x = this.getX();
            GuiControlList.this.mc.fontRenderer.drawString(this.description, (float) (x + 90 - GuiControlList.this.maxWidth), (float) (y + entryHeight / 2 - GuiControlList.this.mc.fontRenderer.FONT_HEIGHT / 2), 16777215);
            this.buttonSelect.x = x + 105;
            this.buttonSelect.y = y;
            this.buttonSelect.displayString = this.keyBinding.func_197978_k();
            this.buttonSelect.render(mouseX, mouseY, partialTicks);
        }

        @Override
        public boolean mouseClicked(double x, double y, int button) {
            return this.buttonSelect.mouseClicked(x, y, button);
        }

        @Override
        public boolean mouseReleased(double x, double y, int button) {
            return buttonSelect.mouseReleased(x, y, button);
        }
    }
}