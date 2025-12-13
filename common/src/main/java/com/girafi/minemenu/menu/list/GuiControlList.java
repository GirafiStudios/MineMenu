package com.girafi.minemenu.menu.list;

import com.girafi.minemenu.gui.ScreenStack;
import com.girafi.minemenu.menu.ClickActionScreen;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class GuiControlList extends ContainerObjectSelectionList<KeyBindsList.Entry> {
    public final Minecraft mc;
    private int maxWidth = 0;

    public GuiControlList(Screen parent, Minecraft mc) {
        super(mc, parent.width + 32, parent.height - 20, 20, 20);
        this.mc = mc;
        this.setY(28);

        KeyMapping[] keyBindings = Minecraft.getInstance().options.keyMappings;
        Arrays.sort(keyBindings);
        KeyMapping.Category lastCategory = null;

        for (KeyMapping keybinding : keyBindings) {
            KeyMapping.Category category = keybinding.getCategory();

            if (!keybinding.getName().equalsIgnoreCase("key.open_menu")) {
                if (category != lastCategory) {
                    lastCategory = category;
                    this.addEntry(new CategoryEntry(category));
                }

                Component component = Component.translatable(keybinding.getName());
                int width = mc.font.width(component);
                if (width > this.maxWidth) {
                    this.maxWidth = width;
                }
                this.addEntry(new KeyEntry(keybinding));
            }
        }
    }

    public class CategoryEntry extends KeyBindsList.Entry {
        private final FocusableTextWidget categoryName;

        public CategoryEntry(KeyMapping.Category category) {
            this.categoryName = new FocusableTextWidget(GuiControlList.this.getRowWidth(), category.label(), GuiControlList.this.minecraft.font, false, FocusableTextWidget.BackgroundFill.ON_FOCUS, 4);
        }

        @Override
        public void renderContent(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
            this.categoryName.setPosition(GuiControlList.this.width / 2 - this.categoryName.getWidth() / 2, this.getContentBottom() - 9 - 1);
            this.categoryName.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(this.categoryName);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of(this.categoryName);
        }

        @Override
        protected void refreshEntry() {
        }
    }

    public class KeyEntry extends KeyBindsList.Entry {
        private final KeyMapping key;
        private final Component name;
        private final Button buttonSelect;

        KeyEntry(KeyMapping keyBinding) {
            super();
            this.key = keyBinding;
            this.name = Component.translatable(keyBinding.getName());
            this.buttonSelect = Button.builder(name, (screen) -> {
                        ClickActionScreen.keyBinding = keyBinding;
                        ScreenStack.pop();
                    }).bounds(0, 0, 95, 18)
                    .createNarration(
                            p_346090_ -> key.isUnbound()
                                    ? Component.translatable("narrator.controls.unbound", name)
                                    : Component.translatable("narrator.controls.bound", name, p_346090_.get())
                    ).build();
            this.refreshEntry();
        }

        @Override
        public void renderContent(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
            int x = GuiControlList.this.scrollBarX() - 10;
            int y = this.getContentY() - 2;
            int k = x - 5 - this.buttonSelect.getWidth();
            this.buttonSelect.setPosition(k, y);
            this.buttonSelect.render(guiGraphics, mouseX, mouseY, partialTick);
            guiGraphics.drawString(GuiControlList.this.mc.font, this.name, this.getContentX() - this.buttonSelect.getWidth() / 2, this.getContentYMiddle() - 9 / 2, -1);
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
        protected void refreshEntry() {
            this.buttonSelect.setMessage(this.key.getTranslatedKeyMessage());
            MutableComponent mutablecomponent = Component.empty();
            if (!this.key.isUnbound()) {
                for (KeyMapping keymapping : GuiControlList.this.minecraft.options.keyMappings) {
                    if ((keymapping != this.key && this.key.same(keymapping)) /*|| keymapping.hasKeyModifierConflict(this.key)*/) {
                        mutablecomponent.append(Component.translatable(keymapping.getName()));
                    }
                }
            }
        }

        public KeyMapping getKey() {
            return this.key;
        }
    }
}