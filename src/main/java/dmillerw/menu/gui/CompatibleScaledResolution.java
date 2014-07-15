package dmillerw.menu.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

/**
 * @author dmillerw
 */
public class CompatibleScaledResolution {

	private int scaledWidth;
	private int scaledHeight;
	private int scaleFactor;

	private double scaledWidthD;
	private double scaledHeightD;

	public CompatibleScaledResolution(Minecraft minecraft, int width, int height) {
		this.scaledWidth = width;
		this.scaledHeight = height;
		this.scaleFactor = 1;
		boolean flag = minecraft.func_152349_b();
		int k = minecraft.gameSettings.guiScale;

		if (k == 0) {
			k = 1000;
		}

		while (this.scaleFactor < k && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
			++this.scaleFactor;
		}

		if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
			--this.scaleFactor;
		}

		this.scaledWidthD = (double) this.scaledWidth / (double) this.scaleFactor;
		this.scaledHeightD = (double) this.scaledHeight / (double) this.scaleFactor;
		this.scaledWidth = MathHelper.ceiling_double_int(this.scaledWidthD);
		this.scaledHeight = MathHelper.ceiling_double_int(this.scaledHeightD);
	}

	public int getScaledWidth() {
		return this.scaledWidth;
	}

	public int getScaledHeight() {
		return this.scaledHeight;
	}

	public double getScaledWidth_double() {
		return this.scaledWidthD;
	}

	public double getScaledHeight_double() {
		return this.scaledHeightD;
	}

	public int getScaleFactor() {
		return this.scaleFactor;
	}
}
