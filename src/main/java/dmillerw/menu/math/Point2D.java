package dmillerw.menu.math;

/**
 * @author dmillerw
 */
public class Point2D {

	private float x;
	private float y;

	public Point2D(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void add(float x, float y) {
		this.x += x;
		this.y += y;
	}

	public void sub(float x, float y) {
		this.x -= x;
		this.y -= y;
	}

	public double getTheta() {
		double theta = StrictMath.toDegrees(StrictMath.atan2(y, x));

		if ((theta < -360) || (theta > 360)) {
			theta = theta % 360;
		}

		if (theta < 0) {
			theta = 360 + theta;
		}

		return theta;
	}

	public void setTheta(double theta) {
		if ((theta < -360) || (theta > 360)) {
			theta = theta % 360;
		}

		if (theta < 0) {
			theta = 360 + theta;
		}

//		double oldTheta = getTheta();
//
//		if ((theta < -360) || (theta > 360)) {
//			oldTheta = oldTheta % 360;
//		}
//
//		if (theta < 0) {
//			oldTheta = 360 + oldTheta;
//		}

		float len = length();
		x = len * (float) Math.cos(StrictMath.toRadians(theta));
		y = len * (float) Math.sin(StrictMath.toRadians(theta));
	}

	public float lengthSquared() {
		return (x * x) + (y * y);
	}

	public float length() {
		return (float) Math.sqrt(lengthSquared());
	}
}
