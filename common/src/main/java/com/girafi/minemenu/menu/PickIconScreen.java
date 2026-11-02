package com.girafi.minemenu.menu;

import com.girafi.minemenu.MineMenuCommon;
import com.girafi.minemenu.data.session.EditSessionData;
import com.girafi.minemenu.gui.ScreenStack;
import com.girafi.minemenu.helper.ItemRenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;

public class PickIconScreen extends Screen {
    private static final int MAX_COLUMN = 14;
    private static final int CELL_SIZE = 20;
    private static final int GRID_TOP = 66;
    private static final int GRID_BOTTOM_MARGIN = 48 + 4;
    private EditBox textSearch;
    private Button buttonDone;
    private Button buttonCancel;
    private NonNullList<ItemStack> stacks;
    private int listScrollIndex = 0;

    public PickIconScreen() {
        super(Component.translatable("mine_menu.iconScreen.title"));
    }

    @Override
    public void tick() {
        if (textSearch.getValue().trim().isEmpty()) {
            this.reconstructList(stacks);
        }
    }

    private void reconstructList(NonNullList<ItemStack> list) {
        list.clear();

        for (Item registryItem : BuiltInRegistries.ITEM) {
            ItemStack stack = new ItemStack(registryItem);
            if (!stack.isEmpty() && stack != null) {
                list.add(stack);
            }
        }
    }

    @Override
    public void init() {
        stacks = NonNullList.create();
        this.reconstructList(stacks);

        RegistryAccess registryAccess = this.minecraft.level.registryAccess();

        addRenderableWidget(this.buttonDone = Button.builder(Component.translatable("gui.done"), (screen) -> {
            String inputText = this.textSearch.getValue();
            if (inputText.contains("{")) {
                CompoundTag tag = new CompoundTag();
                try {
                    tag = TagParser.parseTag(inputText);
                } catch (CommandSyntaxException e) {
                    MineMenuCommon.LOGGER.info("Invalid item NBT");
                    e.printStackTrace();
                }
                ItemStack tagStack = ItemStack.parseOptional(registryAccess, tag);

                if (!tagStack.isEmpty() && tagStack != null) {
                    EditSessionData.icon = tagStack;
                    ScreenStack.pop();
                }
            } else {
                Item textItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(inputText));
                if (textItem == Items.AIR && textItem != null) {
                    MineMenuCommon.LOGGER.warn("Invalid item");
                } else {
                    EditSessionData.icon = new ItemStack(textItem);
                    ScreenStack.pop();
                }
            }
        }).bounds(this.width / 2 - 150, this.height - 60 + 12, 150, 20).build());
        addRenderableWidget(this.buttonCancel = Button.builder(Component.translatable("gui.cancel"), (screen) -> ScreenStack.pop()).bounds(this.width / 2, this.height - 60 + 12, 150, 20).build());
        this.textSearch = new EditBox(this.font, this.width / 2 - 150, 40, 300, 20, Component.translatable("mine_menu.pickIcon.search"));
        this.textSearch.setMaxLength(32767);
        this.textSearch.setFocused(true);

        if (!EditSessionData.icon.isEmpty()) {
            this.textSearch.setValue(BuiltInRegistries.ITEM.getKey(EditSessionData.icon.getItem()).toString());
        }
    }

    @Override
    public void removed() {
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean charTyped(char key, int keyCode) {
        if (textSearch.charTyped(key, key)) {
            listScrollIndex = 0;

            if (!textSearch.getValue().trim().isEmpty()) {
                stacks.clear();

                NonNullList<ItemStack> temp = NonNullList.create();

                if (textSearch.getValue().equalsIgnoreCase(".inv")) {
                    Player player = Minecraft.getInstance().player;
                    if (player != null) {
                        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                            ItemStack stack = player.getInventory().getItem(i);
                            stacks.add(stack.copy());
                        }
                    }
                } else {
                    this.reconstructList(temp);
                    for (ItemStack stack : temp) {
                        if (stack.getHoverName().getString().toLowerCase().contains(textSearch.getValue().toLowerCase())) {
                            stacks.add(stack);
                        }
                    }
                }
                return true;
            }
            return super.charTyped(key, keyCode);
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keycode, int i2, int i3) {
        if (keycode == GLFW.GLFW_KEY_ESCAPE) {
            ScreenStack.pop();
            return true;
        } else if (this.textSearch.keyPressed(keycode, i2, i3)) {
            return true;
        } else {
            return super.keyPressed(keycode, i2, i3);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        ItemStack clicked = getClickedStack(mouseX, mouseY);

        if (!clicked.isEmpty()) {
            this.textSearch.setValue(BuiltInRegistries.ITEM.getKey(clicked.getItem()).toString());
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double d, double wheel) {
        if (wheel != 0) {
            this.listScrollIndex += wheel > 0 ? -2 : 2;
            clampScroll();
            return true;
        }
        return false;
    }

    private int getVisibleRows() {
        return Math.max(1, (this.height - GRID_BOTTOM_MARGIN - GRID_TOP) / CELL_SIZE);
    }

    private void clampScroll() {
        int totalRows = (this.stacks.size() + MAX_COLUMN - 1) / MAX_COLUMN;
        int maxScroll = Math.max(0, totalRows - getVisibleRows());
        this.listScrollIndex = Math.max(0, Math.min(this.listScrollIndex, maxScroll));
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        super.render(guiGraphics, mouseX, mouseY, partial);
        this.textSearch.render(guiGraphics, mouseX, mouseY, partial);
        guiGraphics.drawCenteredString(this.font, "Select an Icon:", this.width / 2, 8, 16777215);

        clampScroll();
        drawList(guiGraphics, mouseX, mouseY);
    }

    private int getCellCenterX(int column) {
        return this.width / 2 - (MAX_COLUMN * CELL_SIZE) / 2 + column * CELL_SIZE + CELL_SIZE / 2;
    }

    private int getCellCenterY(int visibleRow) {
        return GRID_TOP + visibleRow * CELL_SIZE + CELL_SIZE / 2;
    }

    private static boolean isInCell(int centerX, int centerY, double mouseX, double mouseY) {
        int half = CELL_SIZE / 2;
        return mouseX >= centerX - half && mouseX < centerX + half && mouseY >= centerY - half && mouseY < centerY + half;
    }

    private void drawList(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        ItemStack highlighted = ItemStack.EMPTY;
        int highlightedX = 0;
        int highlightedY = 0;

        int first = MAX_COLUMN * this.listScrollIndex;
        int last = Math.min(this.stacks.size(), first + MAX_COLUMN * getVisibleRows());
        for (int i = first; i < last; i++) {
            int centerX = getCellCenterX(i % MAX_COLUMN);
            int centerY = getCellCenterY((i - first) / MAX_COLUMN);

            if (isInCell(centerX, centerY, mouseX, mouseY)) {
                highlighted = this.stacks.get(i);
                highlightedX = centerX / 2;
                highlightedY = centerY / 2;
            } else {
                ItemRenderHelper.renderItem(guiGraphics, centerX, centerY, this.stacks.get(i));
            }
        }

        if (!highlighted.isEmpty()) {
            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.scale(2, 2, 2);
            ItemRenderHelper.renderItem(guiGraphics, highlightedX, highlightedY, highlighted);
            poseStack.popPose();
        }
    }

    @Nonnull
    private ItemStack getClickedStack(double mouseX, double mouseY) {
        int first = MAX_COLUMN * this.listScrollIndex;
        int last = Math.min(this.stacks.size(), first + MAX_COLUMN * getVisibleRows());
        for (int i = first; i < last; i++) {
            if (isInCell(getCellCenterX(i % MAX_COLUMN), getCellCenterY((i - first) / MAX_COLUMN), mouseX, mouseY)) {
                return this.stacks.get(i);
            }
        }
        return ItemStack.EMPTY;
    }
}