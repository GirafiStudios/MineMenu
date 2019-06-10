package dmillerw.menu.gui.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import dmillerw.menu.data.session.EditSessionData;
import dmillerw.menu.gui.ScreenStack;
import dmillerw.menu.helper.GuiRenderHelper;
import dmillerw.menu.helper.ItemRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PickIconScreen extends Screen {
    private static final int MAX_COLUMN = 14;
    private static final int MAX_ROW = 4; // Actually increased by one
    private TextFieldWidget textSearch;
    private Button buttonCancel;
    private NonNullList<ItemStack> stacks;
    private int listScrollIndex = 0;

    public PickIconScreen() {
        super(new TranslationTextComponent("minemenu.iconScreen.title"));
    }

    @Override
    public void tick() {
        this.textSearch.tick();

        if (textSearch.getText().trim().isEmpty()) {
            this.reconstructList(stacks);
        }
    }

    private void reconstructList(NonNullList<ItemStack> list) {
        list.clear();

        for (Item registryItem : ForgeRegistries.ITEMS) {
            ItemStack stack = new ItemStack(registryItem);
            if (!stack.isEmpty() && stack != null) {
                Item item = stack.getItem();
                if (item.getGroup() != null) {
                    item.fillItemGroup(item.getGroup(), list);
                }
            }
        }
    }

    @Override
    @Nullable
    public IGuiEventListener getFocused() {
        return this.textSearch;
    }

    @Override
    public void init() {
        this.getMinecraft().keyboardListener.enableRepeatEvents(true);

        stacks = NonNullList.create();
        this.reconstructList(stacks);

        addButton(this.buttonCancel = new Button(this.width / 2 - 75, this.height - 60 + 12, 150, 20, I18n.format("gui.cancel"), (screen) -> ScreenStack.pop()));
        this.textSearch = new TextFieldWidget(this.font, this.width / 2 - 150, 40, 300, 20, "minemenu.pickIcon.search");
        this.textSearch.setMaxStringLength(32767);
        this.textSearch.changeFocus(true);
    }

    @Override
    public void removed() {
        this.getMinecraft().keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean charTyped(char key, int keyCode) {
        if (textSearch.charTyped(key, key)) {
            listScrollIndex = 0;

            if (!textSearch.getText().trim().isEmpty()) {
                stacks.clear();

                NonNullList<ItemStack> temp = NonNullList.create();

                if (textSearch.getText().equalsIgnoreCase(".inv")) {
                    PlayerEntity player = Minecraft.getInstance().player;
                    for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                        ItemStack stack = player.inventory.getStackInSlot(i);
                        stacks.add(stack.copy());
                    }
                } else {
                    this.reconstructList(temp);
                    for (ItemStack stack : temp) {
                        if (stack.getDisplayName().getString().toLowerCase().contains(textSearch.getText().toLowerCase())) {
                            stacks.add(stack);
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (p_keyPressed_1_ == GLFW.GLFW_KEY_ESCAPE) {
            ScreenStack.pop();
            return true;
        } else {
            return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        ItemStack clicked = getClickedStack(this.width / 2, this.height - (Minecraft.getInstance().mainWindow.getScaledHeight() - 80), mouseX, mouseY);

        if (!clicked.isEmpty()) {
            EditSessionData.icon = clicked;
            ScreenStack.pop();
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double wheel) {
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
    public void render(int mouseX, int mouseY, float partial) {
        this.renderBackground();

        this.textSearch.render(mouseX, mouseY, partial);

        super.render(mouseX, mouseY, partial);

        GuiRenderHelper.renderHeaderAndFooter(this, 25, 20, 5, "Select an Icon:");

        drawList(this.width / 2, this.height - (Minecraft.getInstance().mainWindow.getScaledHeight() - 80), mouseX, mouseY);
    }

    private void drawList(int x, int y, int mx, int my) {
        ItemStack highlighted = ItemStack.EMPTY;
        int highlightedX = 0;
        int highlightedY = 0;

        for (int i = MAX_COLUMN * listScrollIndex; i < stacks.size(); i++) {
            int drawX = i % MAX_COLUMN;
            int drawY = i / MAX_COLUMN;

            if (((i - 14 * listScrollIndex) / MAX_COLUMN) <= MAX_ROW) {
                GlStateManager.pushMatrix();

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
                    ItemRenderHelper.renderItem(actualDrawX, actualDrawY, stacks.get(i));
                }

                GlStateManager.popMatrix();
            } else {
                break;
            }
        }

        if (!highlighted.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.scaled(2, 2, 2);
            ItemRenderHelper.renderItem(highlightedX, highlightedY, highlighted);
            GlStateManager.popMatrix();
        }
    }

    @Nonnull
    private ItemStack getClickedStack(int x, int y, double mx, double my) {
        for (int i = MAX_COLUMN * listScrollIndex; i < stacks.size(); i++) {
            int drawX = i % MAX_COLUMN;
            int drawY = i / MAX_COLUMN;

            if (((i - 14 * listScrollIndex) / MAX_COLUMN) <= MAX_ROW) {
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