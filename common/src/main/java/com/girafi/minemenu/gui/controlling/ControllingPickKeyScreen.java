package com.girafi.minemenu.gui.controlling;

import com.blamejared.controlling.ControllingConstants;
import com.blamejared.controlling.api.DisplayMode;
import com.blamejared.controlling.api.SortOrder;
import com.blamejared.controlling.api.entries.IKeyEntry;
import com.blamejared.controlling.mixin.AccessAbstractSelectionList;
import com.blamejared.searchables.api.autcomplete.AutoCompletingEditBox;
import com.google.common.base.Suppliers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Mostly a copy of Controlling by Jaredlll08's NewKeyBindsScreen.
 */
public class ControllingPickKeyScreen extends OptionsSubScreen {
    private Supplier<ControllingGuiControlList> controlList;
    private AutoCompletingEditBox<KeyBindsList.Entry> search;
    private DisplayMode displayMode;
    private SortOrder sortOrder = SortOrder.NONE;
    private Button buttonNone;
    private Button buttonSort;

    public ControllingPickKeyScreen() {
        super(null, null, Component.translatable("mine_menu.selectKey"));
        this.layout.setHeaderHeight(48);
        this.layout.setFooterHeight(26);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        this.search.moveCursor(0, false);
    }

    @Override
    protected void addTitle() {
        int searchX = 340; // default net.minecraft.client.gui.screens.options.controls.KeyBindsList.getRowWidth
        int centerX = this.width / 2;
        Supplier<List<KeyBindsList.Entry>> listSupplier = () -> this.getCustomList().getAllEntries();
        this.search = new AutoCompletingEditBox<>(font, centerX - searchX / 2, 20, searchX, Button.DEFAULT_HEIGHT, search, Component.translatable("selectWorld.search"), ControllingConstants.SEARCHABLE_KEYBINDINGS, listSupplier);
        this.search.addResponder(this::filterKeys);

        LinearLayout header = this.layout.addToHeader(LinearLayout.vertical(), layoutSettings -> layoutSettings.paddingVertical(8));
        header.addChild(new StringWidget(this.title, this.font), LayoutSettings::alignHorizontallyCenter);
        header.addChild(this.search, layoutSettings -> layoutSettings.paddingVertical(4));
    }

    @Override
    protected void addContents() {
        this.controlList = Suppliers.memoize(() -> new ControllingGuiControlList(this, this.minecraft));
        this.layout.addToContents(this.getCustomList());
        displayMode = DisplayMode.ALL;
    }

    @Override
    protected void addOptions() {
        //Don't add any actual options, as we're just displaying them
    }

    @Override
    protected void addFooter() {
        int btnWidth = Button.DEFAULT_WIDTH / 2 - 1;
        this.buttonSort = Button.builder(sortOrder.getDisplay(), PRESS_SORT)
                .size(btnWidth, Button.DEFAULT_HEIGHT)
                .build();

        this.buttonNone = Button.builder(ControllingConstants.COMPONENT_OPTIONS_SHOW_NONE, PRESS_NONE)
                .size(btnWidth, Button.DEFAULT_HEIGHT)
                .build();

        GridLayout grid = this.layout.addToFooter(new GridLayout());
        grid.rowSpacing(4);
        grid.columnSpacing(8);
        GridLayout.RowHelper rowHelper = grid.createRowHelper(2);
        LinearLayout topLeft = rowHelper.addChild(LinearLayout.horizontal());
        topLeft.spacing(4);
        topLeft.addChild(this.buttonSort);

        LinearLayout topRight = rowHelper.addChild(LinearLayout.horizontal());
        topRight.spacing(4);
        topRight.addChild(this.buttonNone);
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        this.getCustomList().updateSize(this.width, this.layout);
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.search.autoComplete().render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    public void filterKeys() {
        filterKeys(search.getValue());
    }

    public void filterKeys(String lastSearch) {
        ControllingCustomList list = getCustomList();

        list.clearEntries();
        getCustomList().setScrollAmount(0);
        if (lastSearch.isEmpty() && displayMode == DisplayMode.ALL && sortOrder == SortOrder.NONE) {
            for (KeyBindsList.Entry allEntry : getCustomList().getAllEntries()) {
                list.addEntryInternal(allEntry);
            }
            return;
        }

        Predicate<KeyBindsList.Entry> extraPredicate = entry -> true;
        Consumer<List<IKeyEntry>> postConsumer = entries -> {
        };

        if (list instanceof ControllingGuiControlList) {
            extraPredicate = displayMode.getPredicate();
            postConsumer = entries -> {
                entries.removeIf(entry -> !(entry instanceof IKeyEntry));
                list.sort(sortOrder);
            };
        }
        List<KeyBindsList.Entry> entries = ControllingConstants.SEARCHABLE_KEYBINDINGS.filterEntries(list.getAllEntries(), lastSearch, extraPredicate);
        for (KeyBindsList.Entry entry : entries) {
            list.addEntryInternal(entry);
        }

        postConsumer.accept(((AccessAbstractSelectionList) this.controlList.get()).controlling$getChildren());
    }

    @Override
    public boolean mouseClicked(@Nonnull MouseButtonEvent event, boolean doubleClick) {
        boolean b = super.mouseClicked(event, doubleClick);
        if(!b && search.isFocused() && !search.autoComplete().mouseClicked(event, doubleClick)) {
            this.setFocused(null);
            clearFocus();
            b = true;
        }
        return b;
    }

    @Override
    public boolean mouseScrolled(double xpos, double ypos, double xDelta, double yDelta) {
        if(search.autoComplete().mouseScrolled(xpos, ypos, xDelta, yDelta)) {
            return true;
        }
        return super.mouseScrolled(xpos, ypos, xDelta, yDelta);
    }

    @Override
    public boolean keyPressed(@Nonnull KeyEvent event) {
        if (!search.isFocused()) {
            if (event.hasControlDown() && event.key() == GLFW.GLFW_KEY_F) {
                search.setFocused(true);
                return true;
            }
        }
        if (search.isFocused()) {
            if (event.isEscape()) {
                search.setFocused(false);
                return true;
            }
        }
        return super.keyPressed(event);
    }

    public ControllingCustomList getCustomList() {
        if (this.controlList.get() instanceof ControllingCustomList list) {
            return list;
        }
        throw new IllegalStateException("keyBindsList('%s') was not an instance of CustomList! You're either too early or another mod is messing with things.");
    }

    @Override
    public void removed() {
    }

    @Override
    public void onClose() {
        if (this.list != null) {
            this.list.applyUnsavedChanges();
        }
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