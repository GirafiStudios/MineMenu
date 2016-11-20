package dmillerw.menu.gui.menu;

import dmillerw.menu.data.session.EditSessionData;
import dmillerw.menu.gui.GuiStack;
import dmillerw.menu.helper.GuiRenderHelper;
import dmillerw.menu.helper.ItemRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author dmillerw
 */
public class GuiPickIcon extends GuiScreen {

    private static final int MAX_COLUMN = 14;
    private static final int MAX_ROW = 4; // Actually increased by one

    private GuiTextField textSearch;

    private GuiButton buttonCancel;

    private NonNullList<ItemStack> stacks;

    private int listScrollIndex = 0;

    @Override
    public void updateScreen() {
        this.textSearch.updateCursorCounter();
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        stacks = NonNullList.create();

        for (Item item : Item.REGISTRY) {
            if (item != null && item.getCreativeTab() != null) {
                item.getSubItems(item, null, stacks);
            }
        }

        this.buttonList.clear();

        this.buttonList.add(this.buttonCancel = new GuiButton(0, this.width / 2 - 75, this.height - 60 + 12, 150, 20, I18n.format("gui.cancel")));

        this.textSearch = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 150, 40, 300, 20);
        this.textSearch.setMaxStringLength(32767);
        this.textSearch.setFocused(true);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button.id == 0) {
                GuiStack.pop();
            }
        }
    }

    @Override
    protected void keyTyped(char key, int keycode) {
        if (this.textSearch.textboxKeyTyped(key, keycode)) {
            listScrollIndex = 0;

            if (!textSearch.getText().trim().isEmpty()) {
                stacks.clear();

                NonNullList<ItemStack> temp = NonNullList.create();

                if (textSearch.getText().equalsIgnoreCase(".inv")) {
                    EntityPlayer player = Minecraft.getMinecraft().player;
                    for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                        ItemStack stack = player.inventory.getStackInSlot(i);
                        stacks.add(stack.copy());
                    }
                } else {
                    for (Item item : Item.REGISTRY) {
                        if (item != null && item.getCreativeTab() != null) {
                            item.getSubItems(item, null, temp);
                        }
                    }

                    for (ItemStack stack : temp) {
                        if (stack != null && stack.getDisplayName().toLowerCase().contains(textSearch.getText().toLowerCase())) {
                            stacks.add(stack);
                        }
                    }
                }
            } else {
                stacks.clear();

                for (Object anItemRegistry : Item.REGISTRY) {
                    Item item = (Item) anItemRegistry;

                    if (item != null && item.getCreativeTab() != null) {
                        item.getSubItems(item, null, stacks);
                    }
                }
            }
        }

        if (keycode != 28 && keycode != 156) {
            if (keycode == 1) {
                this.actionPerformed(this.buttonCancel);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);

        ItemStack clicked = getClickedStack(this.width / 2, this.height / 2 - 40, mouseX, mouseY);

        if (!clicked.isEmpty()) {
            EditSessionData.icon = clicked;
            GuiStack.pop();
        }

        if (buttonCancel.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) {
            EditSessionData.icon = ItemStack.EMPTY;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partial) {
        onWheelScrolled(Mouse.getDWheel());

        this.drawDefaultBackground();

        this.textSearch.drawTextBox();

        super.drawScreen(mouseX, mouseY, partial);

        GuiRenderHelper.renderHeaderAndFooter(this, 25, 20, 5, "Select an Icon:");

        drawList(this.width / 2, this.height / 2 - 40, mouseX, mouseY);
    }

    private void onWheelScrolled(int wheel) {
        wheel = -wheel;

        if (wheel < 0) {
            listScrollIndex -= 2;
            if (listScrollIndex < 0) {
                listScrollIndex = 0;
            }
        }

        if (wheel > 0) {
            listScrollIndex += 2;
            if (listScrollIndex > Math.max(0, (stacks.size() / MAX_COLUMN)) - MAX_ROW) {
                listScrollIndex = Math.max(0, (stacks.size() / MAX_COLUMN) - MAX_ROW);
            }
        }
    }

    private void drawList(int x, int y, int mx, int my) {
        ItemStack highlighted = ItemStack.EMPTY;
        float highlightedX = 0;
        float highlightedY = 0;

        for (int i = MAX_COLUMN * listScrollIndex; i < stacks.size(); i++) {
            int drawX = i % MAX_COLUMN;
            int drawY = i / MAX_COLUMN;

            if (((i - 14 * listScrollIndex) / MAX_COLUMN) <= MAX_ROW) {
                GlStateManager.pushMatrix();

                boolean scaled = false;
                float actualDrawX = (x + drawX * 20) - (7 * 20) + 10;
                float actualDrawY = (y + drawY * 20);
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
            GlStateManager.scale(2, 2, 2);
            ItemRenderHelper.renderItem(highlightedX, highlightedY, highlighted);
            GlStateManager.popMatrix();
        }
    }

    @Nonnull
    private ItemStack getClickedStack(int x, int y, int mx, int my) {
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