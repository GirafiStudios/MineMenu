package com.girafi.minemenu.gui.controlling;

import com.blamejared.controlling.ControllingConstants;
import com.blamejared.controlling.api.DisplayMode;
import com.blamejared.controlling.api.SortOrder;
import com.blamejared.searchables.api.autcomplete.AutoCompletingEditBox;
import com.girafi.minemenu.gui.ScreenStack;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

/**
 * Mostly a copy of Controlling by Jaredlll08's NewKeyBindsScreen.
 */
public class ControllingPickKeyScreen extends Screen {
    private ControllingGuiControlList controlList;
    private AutoCompletingEditBox<KeyBindsList.Entry> search;
    private DisplayMode displayMode;
    private SortOrder sortOrder = SortOrder.NONE;
    private Button buttonNone;
    private Button buttonSort;

    public ControllingPickKeyScreen() {
        super(Component.translatable("mine_menu.keyScreen.title"));
    }

    @Override
    protected void init() {
        super.init();
        this.controlList = new ControllingGuiControlList(this, this.minecraft);
        int searchX = this.controlList.getRowWidth();
        int btnWidth = Button.DEFAULT_WIDTH / 2 - 1;
        int groupPadding = 5;
        int centerX = this.width / 2;
        int leftX = centerX - Button.DEFAULT_WIDTH - groupPadding;
        int rightX = centerX + groupPadding;

        int bottomY = this.height + 1;
        int rowSpacing = 24;
        int topRowY = bottomY - rowSpacing;

        Supplier<List<KeyBindsList.Entry>> listSupplier = () -> this.controlList.getAllEntries();
        this.search = addRenderableWidget(new AutoCompletingEditBox<>(font, centerX - searchX / 2, 22, searchX, Button.DEFAULT_HEIGHT, search, Component.translatable("selectWorld.search"), ControllingConstants.SEARCHABLE_KEYBINDINGS, listSupplier));
        this.search.addResponder(this::filterKeys);
        this.addRenderableOnly(this.search.autoComplete());

        this.addWidget(this.controlList);

        this.buttonSort = addRenderableWidget(Button.builder(sortOrder.getDisplay(), PRESS_SORT)
                .bounds(leftX + btnWidth + 2, topRowY, btnWidth, Button.DEFAULT_HEIGHT)
                .build());

        this.buttonNone = addRenderableWidget(Button.builder(ControllingConstants.COMPONENT_OPTIONS_SHOW_NONE, PRESS_NONE)
                .bounds(rightX, topRowY, btnWidth, Button.DEFAULT_HEIGHT)
                .build());

        displayMode = DisplayMode.ALL;
        setInitialFocus(this.search);
        // Trigger an initial auto complete
        this.search.moveCursor(0, false);
        // This is so dumb, but it works.
        // The only reason this is needed is that we don't replace the vanilla "Done" button.
        this.children()
                .sort(Comparator.comparingInt((ToIntFunction<GuiEventListener>) value -> value.getRectangle().top())
                        .thenComparingInt(listener -> listener.getRectangle().left()));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void filterKeys() {
        filterKeys(search.getValue());
    }

    public void filterKeys(String lastSearch) {
        this.controlList.children().clear();
        this.controlList.setScrollAmount(0);
        if(lastSearch.isEmpty() && displayMode == DisplayMode.ALL && sortOrder == SortOrder.NONE) {
            this.controlList.children().addAll(this.controlList.getAllEntries());
            return;
        }

        Predicate<KeyBindsList.Entry> extraPredicate = entry -> true;
        Consumer<List<KeyBindsList.Entry>> postConsumer = entries -> {};
        ControllingGuiControlList list = this.controlList;

        if(list != null) {
            extraPredicate = displayMode.getPredicate();
            postConsumer = entries -> sortOrder.sort(entries);
        }

        list.children().addAll(ControllingConstants.SEARCHABLE_KEYBINDINGS.filterEntries(list.getAllEntries(), lastSearch, extraPredicate));
        postConsumer.accept(list.children());
    }

    @Override
    public boolean mouseScrolled(double xpos, double ypos, double xDelta, double yDelta) {
        if(search.autoComplete().mouseScrolled(xpos, ypos, xDelta, yDelta)) {
            return true;
        }
        return super.mouseScrolled(xpos, ypos, xDelta, yDelta);
    }

    @Override
    public boolean keyPressed(int key, int scancode, int mods) {
        if (!search.isFocused() /*&& this.selectedKey == null*/) {
            if (hasControlDown()) {
                if (InputConstants.isKeyDown(Minecraft.getInstance()
                        .getWindow()
                        .getWindow(), GLFW.GLFW_KEY_F)) {
                    search.setFocused(true);
                    return true;
                }
            }
        }
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            ScreenStack.pop();
            search.setFocused(false);
            return true;
        }
        /*if(this.selectedKey != null) {
            if(key == 256) {
                Services.PLATFORM.setKey(options, this.selectedKey, InputConstants.UNKNOWN);
            } else {
                Services.PLATFORM.setKey(options, this.selectedKey, InputConstants.getKey(key, scancode));
            }
            if(!Services.PLATFORM.isKeyCodeModifier(((AccessKeyMapping) this.selectedKey).controlling$getKey())) {
                this.selectedKey = null;
            }
            this.lastKeySelection = Util.getMillis();
            this.this.controlList.resetMappingAndUpdateButtons();
            return true;
        } else {*/
        return super.keyPressed(key, scancode, mods);
        //}
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.controlList.render(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.drawCenteredString(this.font, "Select a Key:", this.width / 2, 8, 16777215);
    }

    private final Button.OnPress PRESS_NONE = btn -> {
        if(displayMode == DisplayMode.NONE) {
            buttonNone.setMessage(ControllingConstants.COMPONENT_OPTIONS_SHOW_NONE);
            displayMode = DisplayMode.ALL;
        } else {
            displayMode = DisplayMode.NONE;
            buttonNone.setMessage(ControllingConstants.COMPONENT_OPTIONS_SHOW_ALL);
        }
        filterKeys();
    };

    private final Button.OnPress PRESS_SORT = btn -> {
        sortOrder = sortOrder.cycle();
        btn.setMessage(sortOrder.getDisplay());
        filterKeys();
    };
}