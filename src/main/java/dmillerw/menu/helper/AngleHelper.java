package dmillerw.menu.helper;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;

/**
 * @author dmillerw
 */
public class AngleHelper {

	public static double getMouseAngle() {
		return getRelativeAngle(Minecraft.getMinecraft().displayWidth / 2, Minecraft.getMinecraft().displayHeight / 2, Mouse.getX(), Mouse.getY());
	}

	public static double getRelativeAngle(double originX, double originY, double x, double y) {
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

	public static void rotate(double x, double y, double angle) {
		double length = Math.sqrt(x * x + y * y);

		x = (length * Math.cos(StrictMath.toRadians(angle)));
		y = (length * Math.sin(StrictMath.toRadians(angle)));
	}

	public static double correctAngle(double angle) {
		if (angle < 0) {
			angle += 360;
		} else if (angle > 360) {
			angle -= 360;
		}

		return angle;
	}

	public static double offsetAngle(double angle, double offset) {
		angle -= offset;

		if (angle < 0) {
			angle += 360;
		} else if (angle > 360) {
			angle -= 360;
		}

		return angle;
	}
}
