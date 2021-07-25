package dmillerw.menu.gui.menu.list;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import dmillerw.menu.gui.ScreenStack;
import dmillerw.menu.gui.menu.ClickActionScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.ControlList;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class GuiControlList extends ContainerObjectSelectionList<ControlList.Entry> {
    public final Minecraft mc;
    private int maxWidth = 0;

    public GuiControlList(Screen parent, Minecraft mc) {
        super(mc, parent.width, parent.height, 25, parent.height - 20, 20);
        this.mc = mc;

        KeyMapping[] keyBindings = Minecraft.getInstance().options.keyMappings;
        Arrays.sort(keyBindings);
        String lastCategory = "";

        for (KeyMapping keybinding : keyBindings) {
            String category = keybinding.getCategory();

            if (!keybinding.getName().equalsIgnoreCase("key.open_menu")) {
                if (!category.equals(lastCategory)) {
                    lastCategory = category;
                    this.addEntry(new CategoryEntry(category));
                }

                int width = mc.font.width(I18n.get(keybinding.getName()));

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
    public class CategoryEntry extends ControlList.Entry {
        private final String category;
        private final int width;

        CategoryEntry(String category) {
            this.category = I18n.get(category);
            this.width = GuiControlList.this.mc.font.width(this.category);
        }

        @Override
        public void render(@Nonnull PoseStack matrixStack, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
            GuiControlList.this.mc.font.draw(matrixStack, this.category, (float) (Objects.requireNonNull(GuiControlList.this.mc.screen).width / 2 - this.width / 2), (float) (p_230432_3_ + p_230432_6_ - GuiControlList.this.mc.font.lineHeight - 1), 16777215);
        }

        @Override
        @Nonnull
        public List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }

        @Override
        public boolean changeFocus(boolean changeFocus) {
            return false;
        }

        @Override
        @Nonnull
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(new NarratableEntry() {
                @Nonnull
                public NarratableEntry.NarrationPriority narrationPriority() {
                    return NarratableEntry.NarrationPriority.HOVERED;
                }

                @Override
                public void updateNarration(@Nonnull NarrationElementOutput output) {
                    output.add(NarratedElementType.TITLE, CategoryEntry.this.category);
                }
            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class KeyEntry extends ControlList.Entry {
        private final KeyMapping keyBinding;
        private final TranslatableComponent description;
        private final Button buttonSelect;

        KeyEntry(KeyMapping keyBinding) {
            this.keyBinding = keyBinding;
            this.description = new TranslatableComponent(keyBinding.getName());
            this.buttonSelect = new Button(0, 0, 95, 18, description, (screen) -> {
                ClickActionScreen.keyBinding = keyBinding;
                ScreenStack.pop();
            });
        }

        @Override
        public void render(@Nonnull PoseStack matrixStack, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
            GuiControlList.this.mc.font.drawShadow(matrixStack, this.description, (float) (p_230432_4_ + 90 - GuiControlList.this.maxWidth), (float) (p_230432_3_ + p_230432_6_ / 2 - GuiControlList.this.mc.font.lineHeight / 2), 16777215);
            this.buttonSelect.x = p_230432_4_ + 105;
            this.buttonSelect.y = p_230432_3_;
            this.buttonSelect.setMessage(this.keyBinding.getTranslatedKeyMessage());
            this.buttonSelect.renderButton(matrixStack, p_230432_7_, p_230432_8_, p_230432_10_);
        }

        @Override
        @Nonnull
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.buttonSelect);
        }

        @Override
        @Nonnull
        public List<? extends NarratableEntry> narratables() {
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