package dmillerw.menu.helper;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;

public class AngleHelper {

    public static double getMouseAngle() {
        return getRelativeAngle(Minecraft.getMinecraft().displayWidth / 2, Minecraft.getMinecraft().displayHeight / 2, Mouse.getX(), Mouse.getY());
    }

    private static double getRelativeAngle(double originX, double originY, double x, double y) {
        double angle = Math.toDegrees(Math.atan2(y - originY, x - originX));

        // Remove 90 from the angle to make 0 and 180 at the top and bottom of the screen
        angle -= 90;

        if (angle < 0) {
            angle += 360;
        } else if (angle > 360) {
            angle -= 360;
        }
        return angle;
    }

    public static double correctAngle(double angle) {
        if (angle < 0) {
            angle += 360;
        } else if (angle > 360) {
            angle -= 360;
        }
        return angle;
    }
}