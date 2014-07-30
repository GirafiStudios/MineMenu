package dmillerw.menu.handler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import dmillerw.menu.data.session.ActionSessionData;
import dmillerw.menu.data.menu.MenuItem;
import dmillerw.menu.data.menu.RadialMenu;
import dmillerw.menu.gui.menu.GuiMenuItem;
import dmillerw.menu.gui.GuiStack;
import dmillerw.menu.helper.AngleHelper;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;

/**
 * @author dmillerw
 */
public class MouseHandler {

    public static void register() {
        FMLCommonHandler.instance().bus().register(new MouseHandler());
    }

    public static double getMouseDistanceFromCenter() {
        int mx = Mouse.getX() - Minecraft.getMinecraft().displayWidth / 2;
        int my = Mouse.getY() - Minecraft.getMinecraft().displayHeight / 2;
        return Math.sqrt(mx * mx + my * my);
    }

    public static boolean showMenu = false;

    private boolean ignoreNextTick = false;

    @SubscribeEvent
    public void onMouseEvent(InputEvent.MouseInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();

        if (Minecraft.getMinecraft().currentScreen != null || Minecraft.getMinecraft().theWorld == null) {
            return;
        }

        if (ignoreNextTick) {
            ignoreNextTick = false;
            return;
        }

        if (Mouse.isButtonDown(0)) {
            if (mc.theWorld != null) {
                if (MouseHandler.showMenu) {
                    double mouseAngle = AngleHelper.getMouseAngle();
                    mouseAngle -= ClientTickHandler.ANGLE_PER_ITEM / 2;
                    mouseAngle = 360 - mouseAngle;
                    mouseAngle = AngleHelper.correctAngle(mouseAngle);

                    if (!mc.gameSettings.hideGUI) {
                        for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
                            double currAngle = ClientTickHandler.ANGLE_PER_ITEM * i;
                            double nextAngle = currAngle + ClientTickHandler.ANGLE_PER_ITEM;
                            currAngle = AngleHelper.correctAngle(currAngle);
                            nextAngle = AngleHelper.correctAngle(nextAngle);

                            boolean mouseIn = mouseAngle > currAngle && mouseAngle < nextAngle;

                            if (mouseIn) {
                                MenuItem item = RadialMenu.getActiveArray()[i];
                                boolean disabled = item != null && !ActionSessionData.availableActions.contains(item.clickAction.getClickAction());

                                if (item != null && item.clickAction != null) {
                                    if (mc.thePlayer.isSneaking()) {
                                        GuiStack.push(new GuiMenuItem(i, item));
                                        showMenu = false;
                                        return;
                                    } else {
                                        if (!disabled) {
                                            if (item.clickAction.onClicked()) {
                                                showMenu = false;
                                                ignoreNextTick = true;
                                                return;
                                            }
                                        }
                                    }
                                } else {
                                    GuiStack.push(new GuiMenuItem(i, item));
                                    showMenu = false;
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (Mouse.isButtonDown(0) && showMenu) {
            grabMouse(false, false); // MC re-grabs the mouse upon click, but the user may wish to choose more options
        }

//		if (!Mouse.isButtonDown(0)) {
//			if (Mouse.isButtonDown(2) && !showMenu) {
//				grabMouse(false, true);
//				MouseHandler.showMenu = true;
//			} else if (!Mouse.isButtonDown(2) && showMenu) {
//				grabMouse(true, true);
//				MouseHandler.showMenu = false;
//			}
//		}
    }

    public static void grabMouse(boolean grab, boolean resetPosition) {
        if (grab != Mouse.isGrabbed()) {
            Mouse.setGrabbed(grab);
            if (resetPosition) {
                Mouse.setCursorPosition(Minecraft.getMinecraft().displayWidth / 2, Minecraft.getMinecraft().displayHeight / 2);
            }
        }
        Minecraft.getMinecraft().inGameHasFocus = grab;
    }
}
