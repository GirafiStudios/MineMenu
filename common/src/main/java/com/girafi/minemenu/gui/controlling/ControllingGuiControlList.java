package com.girafi.minemenu.gui.controlling;

import com.blamejared.controlling.api.entries.ICategoryEntry;
import com.blamejared.controlling.api.entries.IKeyEntry;
import com.blamejared.controlling.api.events.IKeyEntryMouseClickedEvent;
import com.blamejared.controlling.api.events.IKeyEntryMouseReleasedEvent;
import com.blamejared.controlling.platform.Services;
import com.girafi.minemenu.gui.ScreenStack;
import com.girafi.minemenu.menu.ClickActionScreen;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.CommonColors;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

public class ControllingGuiControlList extends ControllingCustomList {
    private int maxListLabelWidth;

    public ControllingGuiControlList(Screen parent, Minecraft mc) {
        super(mc, parent.width + 20, parent.height - 70, 45, 20);
        this.clearEntries();
        allEntries = new ArrayList<>();
        KeyMapping[] bindings = ArrayUtils.clone(minecraft.options.keyMappings);
        Arrays.sort(bindings);
        KeyMapping.Category lastCategory = null;

        for (KeyMapping keyMapping : bindings) {
            KeyMapping.Category category = keyMapping.getCategory();
            if (category != lastCategory) {
                lastCategory = category;
                if(!isHidden(category.label())) {
                    addEntry(new CategoryEntry(category));
                }
            }

            Component component = Services.PLATFORM.getKeyName(keyMapping);
            int width = mc.font.width(component);
            if (width > this.maxListLabelWidth) {
                this.maxListLabelWidth = width;
            }
            if(!isHidden(category.label())) {
                addEntry(new KeyEntry(keyMapping, component));
            }
        }
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 32;
    }

    private boolean isHidden(Component component) {
        if(component.getContents() instanceof TranslatableContents tc) {
            return tc.getKey().endsWith(".hidden");
        }
        return false;
    }

    public class CategoryEntry extends KeyBindsList.Entry implements ICategoryEntry {
        private final FocusableTextWidget categoryName;
        private KeyMapping.Category category;

        public CategoryEntry(KeyMapping.Category category) {
            this.categoryName = FocusableTextWidget.builder(category.label(), ControllingGuiControlList.this.minecraft.font)
                    .alwaysShowBorder(false)
                    .backgroundFill(FocusableTextWidget.BackgroundFill.ON_FOCUS)
                    .build();
            this.category = category;
        }

        @Override
        public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
            this.categoryName.setPosition(ControllingGuiControlList.this.width / 2 - this.categoryName.getWidth() / 2, this.getContentBottom() - 9 - 1);
            this.categoryName.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        @Override
        @Nonnull
        public List<? extends GuiEventListener> children() {
            return List.of(this.categoryName);
        }

        @Override
        @Nonnull
        public List<? extends NarratableEntry> narratables() {
            return List.of(this.categoryName);
        }

        @Override
        protected void refreshEntry() {}

        @Override
        public KeyMapping.Category category() {
            return this.category;
        }
    }

    public class KeyEntry extends KeyBindsList.Entry implements IKeyEntry {
        private final KeyMapping key;
        private final Component keyDesc;
        private final Component categoryName;
        private boolean hasCollision;
        private final Button buttonSelect;

        private KeyEntry(final KeyMapping key, final Component keyDesc) {
            this.key = key;
            this.keyDesc = keyDesc;
            this.buttonSelect = Button.builder(key.getTranslatedKeyMessage(), (screen) -> {
                        ClickActionScreen.keyBinding = key;
                        ScreenStack.pop();
                    }).bounds(0, 0, 95, 18)
                    .createNarration(
                            p_346090_ -> key.isUnbound()
                                    ? Component.translatable("narrator.controls.unbound", keyDesc)
                                    : Component.translatable("narrator.controls.bound", keyDesc, p_346090_.get())
                    ).build();
            this.categoryName = this.key.getCategory().label();
            this.refreshEntry();
        }
        @Override
        public void renderContent(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
            int x = ControllingGuiControlList.this.scrollBarX() - 10;
            int y = this.getContentY() - 2;
            int k = x - 5 - this.buttonSelect.getWidth();
            this.buttonSelect.setPosition(k, y);
            this.buttonSelect.render(guiGraphics, mouseX, mouseY, partialTick);
            guiGraphics.drawString(ControllingGuiControlList.this.minecraft.font, this.keyDesc, this.getContentX() - this.buttonSelect.getWidth() / 2, this.getContentYMiddle() - 9 / 2, -1);

            if(this.hasCollision) {
                int markerWidth = 3;
                int minX = this.buttonSelect.getX() - 6;
                guiGraphics.fill(minX, this.getContentY() - 1, minX + markerWidth, this.getContentBottom(), CommonColors.YELLOW);
            }
        }

        @Override
        @Nonnull
        public List<GuiEventListener> children() {
            return List.of(this.buttonSelect);
        }

        @Override
        @Nonnull
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(this.buttonSelect);
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
            if (Services.EVENT.fireKeyEntryMouseClickedEvent(this, event, isDoubleClick).map(IKeyEntryMouseClickedEvent::isHandled, UnaryOperator.identity())) {
                return true;
            }
            return this.buttonSelect.mouseClicked(event, isDoubleClick);
        }

        @Override
        public boolean mouseReleased(@Nonnull MouseButtonEvent event) {
            if (Services.EVENT.fireKeyEntryMouseReleasedEvent(this, event).map(IKeyEntryMouseReleasedEvent::isHandled, UnaryOperator.identity())) {
                return true;
            }
            return this.buttonSelect.mouseReleased(event);
        }

        @Override
        protected void refreshEntry() {
            this.hasCollision = false;
            MutableComponent duplicates = Component.empty();
            if (!this.key.isUnbound()) {
                KeyMapping[] mappings = ControllingGuiControlList.this.minecraft.options.keyMappings;

                for (KeyMapping mapping : mappings) {
                    if (mapping != this.key && this.key.same(mapping) || Services.PLATFORM.hasConflictingModifier(key, mapping)) {
                        if (this.hasCollision) {
                            duplicates.append(", ");
                        }

                        this.hasCollision = true;
                        duplicates.append(Services.PLATFORM.getKeyName(mapping));
                    }
                }
            }
            MutableComponent tooltip = this.categoryName().copy();
            if (this.hasCollision) {
                tooltip.append(CommonComponents.NEW_LINE);
                tooltip.append(Component.translatable("controls.keybinds.duplicateKeybinds", duplicates));
            }
        }

        @Override
        public Component categoryName() {
            return this.categoryName;
        }

        @Override
        public KeyMapping getKey() {
            return this.key;
        }

        @Override
        public Component getKeyDesc() {
            return this.keyDesc;
        }

        @Override
        public Button getBtnResetKeyBinding() {
            return null;
        }

        @Override
        public Button getBtnChangeKeyBinding() {
            return null;
        }

    }
}