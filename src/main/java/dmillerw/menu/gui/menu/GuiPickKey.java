package dmillerw.menu.gui.menu;

import dmillerw.menu.gui.GuiStack;
import dmillerw.menu.gui.menu.list.GuiControlList;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

/**
 * @author dmillerw
 */
public class GuiPickKey extends GuiScreen {

    private GuiControlList controlList;

    @Override
    public void initGui() {
        controlList = new GuiControlList(this, this.mc);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.controlList.handleMouseInput();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void keyTyped(char key, int keycode) {
        if (keycode == 1) {
            GuiStack.pop();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton != 0 || !this.controlList.mouseClicked(mouseX, mouseY, mouseButton)) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state != 0 || !this.controlList.mouseReleased(mouseX, mouseY, state)) {
            super.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.controlList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, "Select a Key:", this.width / 2, 8, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}