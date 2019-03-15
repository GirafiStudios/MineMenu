package dmillerw.menu.gui.menu;

import dmillerw.menu.data.session.EditSessionData;
import dmillerw.menu.gui.GuiStack;
import dmillerw.menu.helper.GuiRenderHelper;
import dmillerw.menu.helper.ItemRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GuiPickIcon extends GuiScreen {
    private static final int MAX_COLUMN = 14;
    private static final int MAX_ROW = 4; // Actually increased by one
    private GuiTextField textSearch;
    private GuiButton buttonCancel;
    private NonNullList<ItemStack> stacks;
    private int listScrollIndex = 0;

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
    public void initGui() {
        this.mc.keyboardListener.enableRepeatEvents(true);

        stacks = NonNullList.create();
        this.reconstructList(stacks);

        addButton(this.buttonCancel = new GuiButton(0, this.width / 2 - 75, this.height - 60 + 12, 150, 20, I18n.format("gui.cancel")) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                GuiStack.pop();
            }
        });

        this.textSearch = new GuiTextField(0, this.fontRenderer, this.width / 2 - 150, 40, 300, 20);
        this.textSearch.setMaxStringLength(32767);
        this.textSearch.setFocused(true);
    }

    @Override
    public void onGuiClosed() {
        this.mc.keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public boolean doesGuiPauseGame() {
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
                    EntityPlayer player = Minecraft.getInstance().player;
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
        return super.charTyped(key, keyCode);
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (p_keyPressed_1_ == GLFW.GLFW_KEY_ESCAPE) {
            GuiStack.pop();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        ItemStack clicked = getClickedStack(this.width / 2, this.height - (Minecraft.getInstance().mainWindow.getScaledHeight() - 80), mouseX, mouseY);

        if (!clicked.isEmpty()) {
            EditSessionData.icon = clicked;
            GuiStack.pop();
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double wheel) {
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
        this.drawDefaultBackground();

        this.textSearch.drawTextField(mouseX, mouseY, partial);

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