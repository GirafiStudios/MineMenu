package com.girafi.minemenu.gui;

import com.girafi.minemenu.data.menu.Category;
import com.girafi.minemenu.data.menu.MenuItem;
import com.girafi.minemenu.data.menu.RadialMenu;
import com.girafi.minemenu.helper.AngleHelper;
import com.girafi.minemenu.helper.KeyboardHandlerHelper;
import com.girafi.minemenu.helper.RadialMenuInput;
import com.girafi.minemenu.menu.CategoryColorScreen;
import com.girafi.minemenu.menu.MenuItemScreen;
import com.girafi.minemenu.util.Config;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RadialMenuScreen extends Screen {
    public static final RadialMenuScreen INSTANCE = new RadialMenuScreen();
    public static boolean active = false;
    public static final double ANGLE_PER_ITEM = 360F / RadialMenu.MAX_ITEMS;
    private static final float OUTER_RADIUS = 80;
    private static final float INNER_RADIUS = 60;
    private static final double CENTER_EDIT_FRACTION = 0.5D;

    public RadialMenuScreen() {
        super(Component.translatable("mine_menu.radialMenu.title"));
    }

    public static void activate() {
        if (Minecraft.getInstance().gui.screen() == null) {
            active = true;
            Minecraft.getInstance().gui.setScreen(INSTANCE);
        }
    }

    public static void deactivate() {
        if (Minecraft.getInstance().gui.screen() == INSTANCE) {
            active = false;
            Minecraft.getInstance().gui.setScreen(null);
        }
    }

    public static boolean isMouseInCenter() {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        double dx = mc.mouseHandler.xpos() - window.getScreenWidth() * 0.5D;
        double dy = mc.mouseHandler.ypos() - window.getScreenHeight() * 0.5D;
        double distance = Math.sqrt(dx * dx + dy * dy) * window.getGuiScaledWidth() / window.getScreenWidth();
        float innerRadius = ((INNER_RADIUS - RadialMenu.animationTimer - 2) / 100F) * (130F);
        double innerEdgeDistance = innerRadius * Math.cos(Math.toRadians(ANGLE_PER_ITEM / 2));
        return distance < innerEdgeDistance * CENTER_EDIT_FRACTION;
    }

    public static boolean shouldHideHud() {
        return active && Config.GENERAL.hideHud.get();
    }

    public static boolean isEditClick(int button) {
        return (Config.GENERAL.shiftClickToEdit.get() && KeyboardHandlerHelper.hasShiftDown()) || (Config.GENERAL.rightClickToEdit.get() && button == 1);
    }

    public static boolean isShiftEditing() {
        return Config.GENERAL.shiftClickToEdit.get() && KeyboardHandlerHelper.hasShiftDown();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent buttonEvent, boolean isDoubleClick) {
        if (isEditClick(buttonEvent.button())) {
            return editHovered(buttonEvent.button() == 0);
        } else if (buttonEvent.button() == 0) {
            return selectHovered();
        }
        return false;
    }

    /**
     * Runs the click action of the hovered menu item, or opens the screen for adding an item if the hovered slot is empty.
     */
    public boolean selectHovered() {
        if (!canInteract()) {
            return false;
        }
        int index = RadialMenuInput.getHoveredIndex();
        if (index < 0) {
            return false;
        }
        MenuItem menuItem = RadialMenu.getActiveArray()[index];
        if (menuItem == null) {
            deactivate();
            ScreenStack.open(new MenuItemScreen(index, null));
        } else {
            if (menuItem.clickAction.deactivates()) {
                deactivate();
            }
            menuItem.clickAction.onClicked();
        }
        return true;
    }

    /**
     * Opens the edit screen for the hovered menu item, or the category color screen if the center is hovered.
     *
     * @param addIfEmpty Whether to open the screen for adding an item if the hovered slot is empty
     */
    public boolean editHovered(boolean addIfEmpty) {
        if (!active || RadialMenu.animationTimer != 0) {
            return false;
        }
        if (RadialMenuInput.isCenterHovered()) {
            return editCategory();
        }
        if (!canInteract()) {
            return false;
        }
        int index = RadialMenuInput.getHoveredIndex();
        if (index < 0) {
            return false;
        }
        MenuItem menuItem = RadialMenu.getActiveArray()[index];
        if (menuItem != null || addIfEmpty) {
            deactivate();
            ScreenStack.open(new MenuItemScreen(index, menuItem));
            return true;
        }
        return false;
    }

    /**
     * Opens the color screen for the current category.
     */
    public boolean editCategory() {
        if (!active || RadialMenu.animationTimer != 0) {
            return false;
        }
        String category = RadialMenu.currentCategory;
        deactivate();
        Minecraft.getInstance().gui.setScreen(new CategoryColorScreen(category));
        return true;
    }

    private static boolean canInteract() {
        Minecraft mc = Minecraft.getInstance();
        return active && RadialMenu.animationTimer == 0 && !mc.gui.hud.isHidden();
    }

    @Override
    public void removed() {
        super.removed();
        active = false;
        RadialMenuInput.onMenuClosed();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void extractBackground(@Nonnull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public void extractRenderState(@Nonnull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && !mc.gui.hud.isHidden() && !mc.isPaused() && RadialMenuScreen.active) {
            guiGraphics.guiRenderState.addGuiElement(new RadialMenuBackgroundElement(RenderPipelines.GUI, TextureSetup.noTexture(), guiGraphics.pose(), this.width / 2, this.height / 2, null));
            renderItems(guiGraphics);
            renderText(guiGraphics);
        }
    }

    public void renderItems(GuiGraphicsExtractor guiGraphics) {
        int x = this.width / 2;
        int y = this.height / 2;

        for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
            MenuItem item = RadialMenu.getActiveArray()[i];
            Item menuButton = BuiltInRegistries.ITEM.getValue(Identifier.parse(Config.GENERAL.menuButtonIcon.get()));
            ItemStack stack = (item != null && !item.icon.isEmpty()) ? item.icon : (menuButton == null ? ItemStack.EMPTY : new ItemStack(menuButton));

            double angle = (ANGLE_PER_ITEM * i) + 90;
            double drawOffset = 1.5;
            double drawX = INNER_RADIUS - RadialMenu.animationTimer + drawOffset;
            double drawY = INNER_RADIUS - RadialMenu.animationTimer + drawOffset;

            double length = Math.sqrt(drawX * drawX + drawY * drawY);

            drawX = -(length * Math.cos(Math.toRadians(angle)));
            drawY = (length * Math.sin(Math.toRadians(angle)));

            if (!stack.isEmpty()) {
                int itemX = (int) (drawX + x);
                int itemY = (int) (drawY + y);
                itemX -= 8;
                itemY -= 8;
                guiGraphics.item(stack, itemX, itemY);
            }
        }
    }

    public void renderText(GuiGraphicsExtractor guiGraphics) {
        int hovered = RadialMenuInput.getHoveredIndex();
        if (hovered < 0) {
            return;
        }

        MenuItem item = RadialMenu.getActiveArray()[hovered];
        String string = item == null ? "Add Item" : item.title;
        if (isShiftEditing() && RadialMenuInput.isCenterHovered()) {
            string = ChatFormatting.RED + "EDIT: " + ChatFormatting.WHITE + I18n.get("mine_menu.categoryScreen.edit");
        } else if (isShiftEditing() && item != null) {
            string = ChatFormatting.RED + "EDIT: " + ChatFormatting.WHITE + string;
        }

        int drawX = this.width / 2 - this.font.width(string) / 2;
        int drawY = this.height / 2;

        int drawWidth = this.font.width(string);
        int drawHeight = this.font.lineHeight;

        float padding = 5F;

        // Background
        int color = RadialMenu.getActiveCategory().getMenuColor();

        guiGraphics.fill(RenderPipelines.GUI, (int) (drawX - padding), (int) (drawY - drawHeight + padding), (int) (drawX + drawWidth + padding), (int) (drawY + drawHeight + padding), color);

        guiGraphics.text(this.font, string, drawX, drawY, 0xFFFFFFFF, false);
    }

    public record RadialMenuBackgroundElement(RenderPipeline pipeline,
                                        TextureSetup textureSetup,
                                        Matrix3x2f pose,
                                        int x,
                                        int y,
                                        @Nullable ScreenRectangle scissorArea,
                                        @Nullable ScreenRectangle bounds) implements GuiElementRenderState {

        public RadialMenuBackgroundElement(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x, int y, @Nullable ScreenRectangle bounds) {
            this(pipeline, textureSetup, pose, x, y, bounds, getBounds(x, y, pose, bounds));
        }

        @Override
        public void buildVertices(@Nonnull VertexConsumer vertexConsumer) {

            Category category = RadialMenu.getActiveCategory();
            int hovered = RadialMenuInput.getHoveredIndex();
            for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
                double currAngle = (ANGLE_PER_ITEM * i) + 90 + (ANGLE_PER_ITEM / 2);
                double nextAngle = (currAngle + ANGLE_PER_ITEM);
                currAngle = AngleHelper.correctAngle(currAngle);
                nextAngle = AngleHelper.correctAngle(nextAngle);

                boolean mouseIn = hovered >= 0 && i == RadialMenu.MAX_ITEMS - 1 - hovered;

                currAngle = Math.toRadians(currAngle);
                nextAngle = Math.toRadians(nextAngle);

                float innerRadius = ((INNER_RADIUS - RadialMenu.animationTimer - (mouseIn ? 2 : 0)) / 100F) * (130F);
                float outerRadius = ((OUTER_RADIUS - RadialMenu.animationTimer + (mouseIn ? 2 : 0)) / 100F) * (130F);

                float pos1InX = x + innerRadius * Mth.cos((float) currAngle);
                float pos1InY = y + innerRadius * Mth.sin((float) currAngle);
                float pos1OutX = x + outerRadius * Mth.cos((float) currAngle);
                float pos1OutY = y + outerRadius * Mth.sin((float) currAngle);
                float pos2OutX = x + outerRadius * Mth.cos((float) nextAngle);
                float pos2OutY = y + outerRadius * Mth.sin((float) nextAngle);
                float pos2InX = x + innerRadius * Mth.cos((float) nextAngle);
                float pos2InY = y + innerRadius * Mth.sin((float) nextAngle);

                int color = mouseIn ? category.getSelectColor() : category.getMenuColor();

                vertexConsumer.addVertexWith2DPose(this.pose(), pos1OutX, pos1OutY).setColor(color);
                vertexConsumer.addVertexWith2DPose(this.pose(), pos1InX, pos1InY).setColor(color);
                vertexConsumer.addVertexWith2DPose(this.pose(), pos2InX, pos2InY).setColor(color);
                vertexConsumer.addVertexWith2DPose(this.pose(), pos2OutX, pos2OutY).setColor(color);
            }
        }

        @Nullable
        private static ScreenRectangle getBounds(int x, int y, Matrix3x2f pose, @Nullable ScreenRectangle rect) {
            int radius = Mth.ceil(((OUTER_RADIUS + 2) / 100F) * 130F);
            ScreenRectangle screenrectangle = new ScreenRectangle(x - radius, y - radius, radius * 2, radius * 2).transformMaxBounds(pose);
            return rect != null ? rect.intersection(screenrectangle) : screenrectangle;
        }
    }
}