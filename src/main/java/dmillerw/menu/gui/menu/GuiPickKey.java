package dmillerw.menu.gui.menu;

import dmillerw.menu.gui.GuiStack;
import dmillerw.menu.gui.menu.list.GuiControlList;
import net.minecraft.client.gui.GuiScreen;

public class GuiPickKey extends GuiScreen {
    private GuiControlList controlList;

    @Override
    public void initGui() {
        controlList = new GuiControlList(this, this.mc);
    }

    /*@Override
    public void handleMouseInput() { //TODO
        super.handleMouseInput();
        this.controlList.handleMouseInput();
    }*/

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public boolean charTyped(char key, int keycode) {
        if (keycode == 1) {
            GuiStack.pop();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton != 0 || !this.controlList.mouseClicked(mouseX, mouseY, mouseButton)) {
            return super.mouseClicked(mouseX, mouseY, mouseButton);
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        if (state != 0 || !this.controlList.mouseReleased(mouseX, mouseY, state)) {
            return super.mouseReleased(mouseX, mouseY, state);
        }
        return false;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.controlList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, "Select a Key:", this.width / 2, 8, 16777215);
        super.render(mouseX, mouseY, partialTicks);
    }
}