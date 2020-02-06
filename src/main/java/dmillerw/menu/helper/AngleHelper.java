package dmillerw.menu.helper;

import net.minecraft.client.Minecraft;

public class AngleHelper {

    public static double getMouseAngle() {
        Minecraft mc = Minecraft.getInstance();
        return getRelativeAngle(mc.getMainWindow().getWidth() * 0.5D, mc.getMainWindow().getHeight() * 0.5D, mc.mouseHelper.getMouseX(), mc.mouseHelper.getMouseY());
    }

    private static double getRelativeAngle(double originX, double originY, double x, double y) {
        double angle = Math.toDegrees(Math.atan2(x - originX, y - originY));

        // Remove 90 from the angle to make 0 and 180 at the top and bottom of the screen
        angle -= 180;

        return correctAngle(angle);
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