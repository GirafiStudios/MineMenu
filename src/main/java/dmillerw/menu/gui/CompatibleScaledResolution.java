package dmillerw.menu.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

public class CompatibleScaledResolution {
    private int scaledWidth;
    private int scaledHeight;
    private int scaleFactor;
    private final double scaledWidthD;
    private final double scaledHeightD;

    public CompatibleScaledResolution(Minecraft minecraft, int width, int height) {
        this.scaledWidth = width;
        this.scaledHeight = height;
        this.scaleFactor = 1;
        int guiScale = minecraft.gameSettings.guiScale;

        if (guiScale == 0) {
            guiScale = 1000;
        }

        while (this.scaleFactor < guiScale && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
            ++this.scaleFactor;
        }

        if (minecraft.gameSettings.forceUnicodeFont && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
            --this.scaleFactor;
        }

        this.scaledWidthD = (double) this.scaledWidth / (double) this.scaleFactor;
        this.scaledHeightD = (double) this.scaledHeight / (double) this.scaleFactor;
        this.scaledWidth = MathHelper.ceil(this.scaledWidthD);
        this.scaledHeight = MathHelper.ceil(this.scaledHeightD);
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