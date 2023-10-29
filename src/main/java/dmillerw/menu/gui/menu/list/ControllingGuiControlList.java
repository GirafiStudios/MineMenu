package dmillerw.menu.gui.menu.list;

import com.blamejared.controlling.platform.Services;
import com.google.common.collect.ImmutableList;
import dmillerw.menu.gui.ScreenStack;
import dmillerw.menu.gui.menu.ClickActionScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ControllingGuiControlList extends ContainerObjectSelectionList<KeyBindsList.Entry> {
    private final Minecraft mc;
    private int maxListLabelWidth;
    public List<KeyBindsList.Entry> allEntries;

    public ControllingGuiControlList(Screen parent, Minecraft mc) {
        super(mc, parent.width, parent.height, 45, parent.height - 55, 20);
        this.mc = mc;
        children().clear();
        allEntries = new ArrayList<>();
        KeyMapping[] bindings = ArrayUtils.clone(mc.options.keyMappings);
        Arrays.sort(bindings);
        String lastCategory = null;

        for (KeyMapping keybinding : bindings) {
            String category = keybinding.getCategory();
            if (!category.equals(lastCategory)) {
                lastCategory = category;
                if (!category.endsWith(".hidden")) {
                    addEntry(new ControllingGuiControlList.CategoryEntry(Component.translatable(category)));
                }
            }

            Component component = Component.translatable(keybinding.getName());
            int width = mc.font.width(component);
            if (width > this.maxListLabelWidth) {
                this.maxListLabelWidth = width;
            }
            if (!category.endsWith(".hidden")) {
                addEntry(new ControllingGuiControlList.KeyEntry(keybinding, component));
            }
        }

    }

    public List<KeyBindsList.Entry> getAllEntries() {
        return allEntries;
    }

    @Override
    protected int addEntry(@Nonnull KeyBindsList.Entry ent) {
        if (allEntries == null) {
            allEntries = new ArrayList<>();
        }
        allEntries.add(ent);
        this.children().add(ent);
        return this.children().size() - 1;
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 15 + 20;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 32;
    }

    public class CategoryEntry extends KeyBindsList.Entry {
        private final Component name;
        private final int labelWidth;

        public CategoryEntry(Component name) {
            this.name = name;
            this.labelWidth = ControllingGuiControlList.this.mc.font.width(this.name);
        }

        @Override
        public void render(GuiGraphics guiGraphics, int slotIndex, int y, int x, int rowLeft, int rowWidth, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            guiGraphics.drawString(ControllingGuiControlList.this.mc.font, this.name, Objects.requireNonNull(minecraft.screen).width / 2 - this.labelWidth / 2, y + rowWidth - 9 - 1, 16777215);
        }

        @Override
        @Nonnull
        public List<? extends NarratableEntry> narratables() {

            return ImmutableList.of(new NarratableEntry() {
                @Override
                @Nonnull
                public NarrationPriority narrationPriority() {
                    return NarrationPriority.HOVERED;
                }

                @Override
                public void updateNarration(@Nonnull NarrationElementOutput neo) {
                    neo.add(NarratedElementType.TITLE, name);
                }
            });
        }

        @Override
        @Nonnull
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of();
        }

        @Override
        protected void refreshEntry() {
        }
    }

    public class KeyEntry extends KeyBindsList.Entry {
        /**
         * The keybinding specified for this KeyEntry
         */
        private final KeyMapping key;
        /**
         * The localized key description for this KeyEntry
         */
        private final Component keyDesc;
        private boolean hasCollision;
        private final Component categoryName;
        private final Button buttonSelect;

        private KeyEntry(final KeyMapping key, final Component keyDesc) {
            this.key = key;
            this.keyDesc = keyDesc;
            this.categoryName = Component.translatable(this.key.getCategory());
            this.buttonSelect = Button.builder(keyDesc, (screen) -> {
                ClickActionScreen.keyBinding = key;
                ScreenStack.pop();
            }).bounds(0, 0, 95, 18).build();
            refreshEntry();
        }

        @Override
        public void render(@Nonnull GuiGraphics guiGraphics, int slotIndex, int y, int x, int rowLeft, int rowWidth, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            //Services.EVENT.fireKeyEntryRenderEvent(this, guiGraphics, slotIndex, y, x, rowLeft, rowWidth, mouseX, mouseY, hovered, partialTicks);
            int length = Math.max(0, x + 90 - ControllingGuiControlList.this.maxListLabelWidth);
            guiGraphics.drawString(ControllingGuiControlList.this.mc.font, this.keyDesc, length, y + rowWidth / 2 - 9 / 2, 16777215);
            this.buttonSelect.setX(x + 105);
            this.buttonSelect.setY(y);
            this.buttonSelect.setMessage(this.key.getTranslatedKeyMessage());
            this.buttonSelect.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);

            if (this.hasCollision) { //TODO Test
                int markerWidth = 3;
                int minX = this.buttonSelect.getX() - 6;
                guiGraphics.fill(minX, y + 2, minX + markerWidth, y + rowWidth + 2, ChatFormatting.RED.getColor() | -16777216);
            }
        }

        @Override
        @Nonnull
        public List<GuiEventListener> children() {
            return ImmutableList.of(this.buttonSelect);
            //return Services.EVENT.fireKeyEntryListenersEvent(this).map(IKeyEntryListenersEvent::getListeners, UnaryOperator.identity());
        }

        @Override
        @Nonnull
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(this.buttonSelect);
            //return ImmutableList.of(this.buttonSelect);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int buttonId) {
            /*if (Services.EVENT.fireKeyEntryMouseClickedEvent(this, mouseX, mouseY, buttonId).map(IKeyEntryMouseClickedEvent::isHandled, UnaryOperator.identity())) {
                return true;
            }*/
            return this.buttonSelect.mouseClicked(mouseX, mouseY, buttonId);
        }


        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int buttonId) {
            /*if (Services.EVENT.fireKeyEntryMouseReleasedEvent(this, mouseX, mouseY, buttonId).map(IKeyEntryMouseReleasedEvent::isHandled, UnaryOperator.identity())) {
                return true;
            }*/
            return this.buttonSelect.mouseReleased(mouseX, mouseY, buttonId);
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
                        duplicates.append(Component.translatable(mapping.getName()));
                    }
                }
            }
            MutableComponent tooltip = Component.translatable(key.getCategory());
            if (this.hasCollision) {
                tooltip.append(CommonComponents.NEW_LINE);
                tooltip.append(Component.translatable("controls.keybinds.duplicateKeybinds", duplicates));
            }
        }
    }
}