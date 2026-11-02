package com.girafi.minemenu.menu;

import com.girafi.minemenu.MineMenuCommon;
import com.girafi.minemenu.data.json.MenuLoader;
import com.girafi.minemenu.data.menu.Category;
import com.girafi.minemenu.data.menu.RadialMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class CategoryColorScreen extends Screen {
    private static final int SLIDER_WIDTH = 72;
    private static final int SLIDER_GAP = 4;
    private static final int ROW_WIDTH = SLIDER_WIDTH * 4 + SLIDER_GAP * 3;
    private static final int SWATCH_SIZE = 12;
    private static final String[] CHANNELS = {"red", "green", "blue", "alpha"};
    private static final int[] CHANNEL_SHIFTS = {16, 8, 0, 24};

    private final Category category;
    @Nullable
    private Integer menuColor;
    @Nullable
    private Integer selectColor;
    private Button menuDefaultButton;
    private Button selectDefaultButton;

    public CategoryColorScreen(String categoryName) {
        super(Component.translatable("mine_menu.categoryScreen.title", categoryName));
        this.category = RadialMenu.getCategory(categoryName);
        this.menuColor = this.category.menuColor;
        this.selectColor = this.category.selectColor;
    }

    private int getSectionY(int section) {
        return this.height / 2 - 70 + section * 65;
    }

    @Override
    protected void init() {
        int left = this.width / 2 - ROW_WIDTH / 2;

        this.menuDefaultButton = addColorSection(left, getSectionY(0),
                () -> this.menuColor != null ? this.menuColor : Category.getDefaultMenuColor(),
                color -> this.menuColor = color,
                () -> this.menuColor = null);
        this.selectDefaultButton = addColorSection(left, getSectionY(1),
                () -> this.selectColor != null ? this.selectColor : Category.getDefaultSelectColor(),
                color -> this.selectColor = color,
                () -> this.selectColor = null);
        this.tick();

        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> {
            this.category.menuColor = this.menuColor;
            this.category.selectColor = this.selectColor;
            MenuLoader.save(MineMenuCommon.menuFile);
            this.onClose();
        }).bounds(this.width / 2 - 4 - 150, this.height - 40, 150, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> this.onClose())
                .bounds(this.width / 2 + 4, this.height - 40, 150, 20).build());
    }

    private Button addColorSection(int left, int y, IntSupplier getColor, IntConsumer setColor, Runnable reset) {
        for (int i = 0; i < CHANNELS.length; i++) {
            addRenderableWidget(new ChannelSlider(left + i * (SLIDER_WIDTH + SLIDER_GAP), y + 16, CHANNELS[i], CHANNEL_SHIFTS[i], getColor, setColor));
        }
        return addRenderableWidget(Button.builder(Component.translatable("mine_menu.categoryScreen.useDefault"), button -> {
            reset.run();
            this.rebuildWidgets(); // Moves the sliders back to the config color
        }).bounds(this.width / 2 - 50, y + 40, 100, 20).build());
    }

    @Override
    public void tick() {
        this.menuDefaultButton.active = this.menuColor != null;
        this.selectDefaultButton.active = this.selectColor != null;
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFFFF);
        renderSectionHeader(guiGraphics, getSectionY(0), "mine_menu.categoryScreen.background", this.menuColor, Category.getDefaultMenuColor());
        renderSectionHeader(guiGraphics, getSectionY(1), "mine_menu.categoryScreen.selected", this.selectColor, Category.getDefaultSelectColor());
    }

    private void renderSectionHeader(GuiGraphics guiGraphics, int y, String labelKey, @Nullable Integer color, int defaultColor) {
        int left = this.width / 2 - ROW_WIDTH / 2;
        int shown = color != null ? color : defaultColor;
        guiGraphics.fill(left - 1, y - 1, left + SWATCH_SIZE + 1, y + SWATCH_SIZE + 1, 0xFFFFFFFF);
        guiGraphics.fill(left, y, left + SWATCH_SIZE, y + SWATCH_SIZE, 0xFF000000);
        guiGraphics.fill(left, y, left + SWATCH_SIZE, y + SWATCH_SIZE, shown);

        Component label = Component.translatable(labelKey);
        guiGraphics.drawString(this.font, label, left + SWATCH_SIZE + 6, y + 2, 0xFFFFFFFF);
        if (color == null) {
            Component state = Component.translatable("mine_menu.categoryScreen.usingDefault");
            guiGraphics.drawString(this.font, state, left + SWATCH_SIZE + 6 + this.font.width(label) + 6, y + 2, 0xFFA0A0A0);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static class ChannelSlider extends AbstractSliderButton {
        private final String channel;
        private final int shift;
        private final IntSupplier getColor;
        private final IntConsumer setColor;

        ChannelSlider(int x, int y, String channel, int shift, IntSupplier getColor, IntConsumer setColor) {
            super(x, y, SLIDER_WIDTH, 20, Component.empty(), ((getColor.getAsInt() >>> shift) & 0xFF) / 255D);
            this.channel = channel;
            this.shift = shift;
            this.getColor = getColor;
            this.setColor = setColor;
            this.updateMessage();
        }

        private int getChannelValue() {
            return (int) Math.round(this.value * 255);
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.translatable("mine_menu.color." + this.channel, getChannelValue()));
        }

        @Override
        protected void applyValue() {
            int color = this.getColor.getAsInt();
            color = (color & ~(0xFF << this.shift)) | (getChannelValue() << this.shift);
            this.setColor.accept(color);
        }
    }
}
