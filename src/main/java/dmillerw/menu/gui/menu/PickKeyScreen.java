package dmillerw.menu.gui.menu;

import com.mojang.blaze3d.matrix.MatrixStack;
import dmillerw.menu.gui.ScreenStack;
import dmillerw.menu.gui.menu.list.GuiControlList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;

public class PickKeyScreen extends Screen {
    private GuiControlList controlList;

    public PickKeyScreen() {
        super(new TranslationTextComponent("minemenu.keyScreen.title"));
    }

    @Override
    public void func_231160_c_() {
        controlList = new GuiControlList(this, this.getMinecraft());
        this.field_230705_e_.add(controlList);
        this.func_231035_a_(controlList);
    }

    @Override
    public boolean func_231177_au__() {
        return false;
    }

    @Override
    public boolean func_231046_a_(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (p_keyPressed_1_ == GLFW.GLFW_KEY_ESCAPE) {
            ScreenStack.pop();
            return true;
        }
        return super.func_231046_a_(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }

    @Override
    public boolean func_231048_c_(double mouseX, double mouseY, int state) {
        if (state == 0 && this.controlList.func_231048_c_(mouseX, mouseY, state)) {
            this.func_231037_b__(false);
            return true;
        } else {
            return super.func_231048_c_(mouseX, mouseY, state);
        }
    }

    @Override
    public boolean func_231044_a_(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton == 0 && this.controlList.func_231044_a_(mouseX, mouseY, mouseButton)) {
            this.func_231037_b__(true);
            this.func_231035_a_(this.controlList);
            return true;
        } else {
            return this.controlList != null && this.controlList.func_231044_a_((int) mouseX, (int) mouseY, mouseButton);
        }
    }

    @Override
    public void func_230430_a_(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.func_230446_a_(matrixStack);
        this.controlList.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
        this.func_238471_a_(matrixStack, this.field_230712_o_, "Select a Key:", this.field_230708_k_ / 2, 8, 16777215);
        super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
    }
}