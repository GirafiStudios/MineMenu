package dmillerw.menu.gui.menu.list;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import dmillerw.menu.gui.ScreenStack;
import dmillerw.menu.gui.menu.ClickActionScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.client.gui.widget.list.KeyBindingList;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class GuiControlList extends AbstractOptionList<KeyBindingList.Entry> {
    public final Minecraft mc;
    private int maxWidth = 0;

    public GuiControlList(Screen parent, Minecraft mc) {
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
    public int getWidth() {
        return super.getWidth() + 32;
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 15;
    }

    @OnlyIn(Dist.CLIENT)
    public class CategoryEntry extends KeyBindingList.Entry {
        private final String category;
        private final int width;

        CategoryEntry(String category) {
            this.category = I18n.format(category);
            this.width = GuiControlList.this.mc.fontRenderer.getStringWidth(this.category);
        }

        @Override
        public void render(@Nonnull MatrixStack matrixStack, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
            GuiControlList.this.mc.fontRenderer.drawString(matrixStack, this.category, (float)(Objects.requireNonNull(GuiControlList.this.mc.currentScreen).width / 2 - this.width / 2), (float)(p_230432_3_ + p_230432_6_ - GuiControlList.this.mc.fontRenderer.FONT_HEIGHT - 1), 16777215);
        }

        @Override
        @Nonnull
        public List<? extends IGuiEventListener> getEventListeners() {
            return Collections.emptyList();
        }

        @Override
        public boolean changeFocus(boolean changeFocus) {
            return false;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class KeyEntry extends KeyBindingList.Entry {
        private final KeyBinding keyBinding;
        private final TranslationTextComponent description;
        private final Button buttonSelect;

        KeyEntry(KeyBinding keyBinding) {
            this.keyBinding = keyBinding;
            this.description = new TranslationTextComponent(keyBinding.getKeyDescription());
            this.buttonSelect = new Button(0, 0, 95, 18, description, (screen) -> {
                ClickActionScreen.keyBinding = keyBinding;
                ScreenStack.pop();
            });
        }

        @Override
        public void render(@Nonnull MatrixStack matrixStack, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
            GuiControlList.this.mc.fontRenderer.func_243246_a(matrixStack, this.description, (float) (p_230432_4_ + 90 - GuiControlList.this.maxWidth), (float) (p_230432_3_ + p_230432_6_ / 2 - GuiControlList.this.mc.fontRenderer.FONT_HEIGHT / 2), 16777215);
            this.buttonSelect.x = p_230432_4_ + 105;
            this.buttonSelect.y = p_230432_3_;
            this.buttonSelect.setMessage(this.keyBinding.func_238171_j_());
            this.buttonSelect.renderButton(matrixStack, p_230432_7_, p_230432_8_, p_230432_10_);
        }

        @Override
        @Nonnull
        public List<? extends IGuiEventListener> getEventListeners() {
            return ImmutableList.of(this.buttonSelect);
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