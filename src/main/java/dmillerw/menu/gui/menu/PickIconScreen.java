package dmillerw.menu.gui.menu;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
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
    public void func_231023_e_() {
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
    public IGuiEventListener func_241217_q_() {
        return this.textSearch;
    }

    @Override
    public void func_231160_c_() {
        this.getMinecraft().keyboardListener.enableRepeatEvents(true);

        stacks = NonNullList.create();
        this.reconstructList(stacks);

        func_230480_a_(this.buttonCancel = new Button(this.field_230708_k_ / 2 - 75, this.field_230709_l_ - 60 + 12, 150, 20, new TranslationTextComponent("gui.cancel"), (screen) -> ScreenStack.pop()));
        this.textSearch = new TextFieldWidget(this.field_230712_o_, this.field_230708_k_ / 2 - 150, 40, 300, 20, new TranslationTextComponent("minemenu.pickIcon.search"));
        this.textSearch.setMaxStringLength(32767);
        this.textSearch.func_231049_c__(true);
    }

    @Override
    public void func_231164_f_() {
        this.getMinecraft().keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public boolean func_231177_au__() {
        return false;
    }

    @Override
    public boolean func_231042_a_(char key, int keyCode) {
        if (textSearch.func_231042_a_(key, key)) {
            listScrollIndex = 0;

            if (!textSearch.getText().trim().isEmpty()) {
                stacks.clear();

                NonNullList<ItemStack> temp = NonNullList.create();

                if (textSearch.getText().equalsIgnoreCase(".inv")) {
                    PlayerEntity player = Minecraft.getInstance().player;
                    if (player != null) {
                        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                            ItemStack stack = player.inventory.getStackInSlot(i);
                            stacks.add(stack.copy());
                        }
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
    public boolean func_231046_a_(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (p_keyPressed_1_ == GLFW.GLFW_KEY_ESCAPE) {
            ScreenStack.pop();
            return true;
        } else {
            return super.func_231046_a_(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        }
    }

    @Override
    public boolean func_231044_a_(double mouseX, double mouseY, int button) {
        super.func_231044_a_(mouseX, mouseY, button);

        ItemStack clicked = getClickedStack(this.field_230708_k_ / 2, this.field_230709_l_ - (Minecraft.getInstance().getMainWindow().getScaledHeight() - 80), mouseX, mouseY);

        if (!clicked.isEmpty()) {
            EditSessionData.icon = clicked;
            ScreenStack.pop();
        }
        return true;
    }

    @Override
    public boolean func_231043_a_(double mouseX, double mouseY, double wheel) {
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
    public void func_230430_a_(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partial) {
        this.func_230446_a_(matrixStack);

        this.textSearch.func_230430_a_(matrixStack, mouseX, mouseY, partial);

        super.func_230430_a_(matrixStack, mouseX, mouseY, partial);

        GuiRenderHelper.renderHeaderAndFooter(matrixStack, this, 25, 20, 5, "Select an Icon:");

        drawList(this.field_230708_k_ / 2, this.field_230709_l_ - (Minecraft.getInstance().getMainWindow().getScaledHeight() - 80), mouseX, mouseY);
    }

    private void drawList(int x, int y, int mx, int my) {
        ItemStack highlighted = ItemStack.EMPTY;
        int highlightedX = 0;
        int highlightedY = 0;

        for (int i = MAX_COLUMN * listScrollIndex; i < stacks.size(); i++) {
            int drawX = i % MAX_COLUMN;
            int drawY = i / MAX_COLUMN;

            if (((i - 14 * listScrollIndex) / MAX_COLUMN) <= MAX_ROW) {
                RenderSystem.pushMatrix();

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

                RenderSystem.popMatrix();
            } else {
                break;
            }
        }

        if (!highlighted.isEmpty()) {
            RenderSystem.pushMatrix();
            RenderSystem.scaled(2, 2, 2);
            ItemRenderHelper.renderItem(highlightedX, highlightedY, highlighted);
            RenderSystem.popMatrix();
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