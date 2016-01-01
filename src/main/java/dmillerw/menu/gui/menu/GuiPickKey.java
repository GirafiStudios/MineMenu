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
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        if (button != 0 || !this.controlList.mouseClicked(mouseX, mouseY, button)) {
            super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int button) {
        if (button != 0 || !this.controlList.mouseReleased(mouseX, mouseY, button)) {
            super.mouseReleased(mouseX, mouseY, button);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partial) {
        this.drawDefaultBackground();
        this.controlList.drawScreen(mouseX, mouseY, partial);
        this.drawCenteredString(this.fontRendererObj, "Select a Key:", this.width / 2, 8, 16777215);
        super.drawScreen(mouseX, mouseY, partial);
    }
}
