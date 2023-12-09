package com.girafi.minemenu.menu;

import com.girafi.minemenu.data.session.EditSessionData;
import com.girafi.minemenu.gui.ScreenStack;
import com.girafi.minemenu.helper.GuiRenderHelper;
import com.girafi.minemenu.helper.ItemRenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;

public class PickIconScreen extends Screen {
    private static final int MAX_COLUMN = 14;
    private static final int MAX_ROW = 4; // Actually increased by one
    private EditBox textSearch;
    private Button buttonCancel;
    private NonNullList<ItemStack> stacks;
    private int listScrollIndex = 0;

    public PickIconScreen() {
        super(Component.translatable("mine_menu.iconScreen.title"));
    }

    @Override
    public void tick() {
        //this.textSearch.tick();

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

        addRenderableWidget(this.buttonCancel = Button.builder(Component.translatable("gui.cancel"), (screen) -> ScreenStack.pop()).bounds(this.width / 2 - 75, this.height - 60 + 12, 150, 20).build());
        this.textSearch = new EditBox(this.font, this.width / 2 - 150, 40, 300, 20, Component.translatable("mine_menu.pickIcon.search"));
        this.textSearch.setMaxLength(32767);
        this.textSearch.setFocused(true);
        this.setInitialFocus(textSearch);
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

        ItemStack clicked = getClickedStack(this.width / 2, this.height - (Minecraft.getInstance().getWindow().getGuiScaledHeight() - 80), mouseX, mouseY);

        if (!clicked.isEmpty()) {
            EditSessionData.icon = clicked;
            ScreenStack.pop();
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double d, double wheel) {
        wheel = -wheel;

        if (wheel < 0) {
            listScrollIndex -= 2;
            if (listScrollIndex < 0) {
                listScrollIndex = 0;
            }
            return true;
        }

        if (wheel > 0) {
            listScrollIndex += 2;
            if (listScrollIndex > Math.max(0, (stacks.size() / MAX_COLUMN)) - MAX_ROW) {
                listScrollIndex = Math.max(0, (stacks.size() / MAX_COLUMN) - MAX_ROW);
            }
            return true;
        }
        return false;
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        super.render(guiGraphics, mouseX, mouseY, partial);
        this.textSearch.render(guiGraphics, mouseX, mouseY, partial);

        GuiRenderHelper.renderHeaderAndFooter(guiGraphics, this, 25, 20, 5, "Select an Icon:");

        drawList(guiGraphics, this.width / 2, this.height - (Minecraft.getInstance().getWindow().getGuiScaledHeight() - 80), mouseX, mouseY);
    }

    private void drawList(GuiGraphics guiGraphics, int x, int y, int mx, int my) {
        ItemStack highlighted = ItemStack.EMPTY;
        int highlightedX = 0;
        int highlightedY = 0;

        int amountOfRowsThatFits = ((this.minecraft.getWindow().getGuiScaledHeight() - 150) / 16);
        amountOfRowsThatFits = amountOfRowsThatFits > 7 ? amountOfRowsThatFits - 6 : amountOfRowsThatFits;

        for (int i = MAX_COLUMN * listScrollIndex; i < stacks.size(); i++) {
            int drawX = i % MAX_COLUMN;
            int drawY = i / MAX_COLUMN;

            if (((i - 14 * listScrollIndex) / MAX_COLUMN) <= amountOfRowsThatFits) {
                boolean scaled = false;
                int actualDrawX = (x + drawX * 20) - (7 * 20) + 10;
                int actualDrawY = (y + drawY * 20);
                actualDrawY -= 20 * listScrollIndex;

                if (mx > (actualDrawX - 8) && mx < (actualDrawX + 20 - 8) && my > actualDrawY - 8 && my < actualDrawY + 20 - 8) {
                    scaled = true;
                    highlighted = stacks.get(i);
                    highlightedX = actualDrawX / 2;
                    highlightedY = actualDrawY / 2;
                }

                if (!scaled) {
                    ItemRenderHelper.renderItem(guiGraphics, actualDrawX, actualDrawY, stacks.get(i));
                }

            } else {
                break;
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
    private ItemStack getClickedStack(int x, int y, double mx, double my) {
        for (int i = MAX_COLUMN * listScrollIndex; i < stacks.size(); i++) {
            int drawX = i % MAX_COLUMN;
            int drawY = i / MAX_COLUMN;

            int amountOfRowsThatFits = ((this.minecraft.getWindow().getGuiScaledHeight() - 150) / 16);
            amountOfRowsThatFits = amountOfRowsThatFits > 7 ? amountOfRowsThatFits - 6 : amountOfRowsThatFits;

            if (((i - 14 * listScrollIndex) / MAX_COLUMN) <= amountOfRowsThatFits) {
                float actualDrawX = (x + drawX * 20) - (7 * 20) + 10;
                float actualDrawY = (y + drawY * 20);
                actualDrawY -= 20 * listScrollIndex;

                if (mx > (actualDrawX - 8) && mx < (actualDrawX + 20 - 8) && my > actualDrawY - 8 && my < actualDrawY + 20 - 8) {
                    return stacks.get(i);
                }
            }
        }
        return ItemStack.EMPTY;
    }
}