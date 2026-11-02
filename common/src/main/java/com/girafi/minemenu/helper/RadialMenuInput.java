package com.girafi.minemenu.helper;

import com.girafi.minemenu.data.menu.RadialMenu;
import com.girafi.minemenu.gui.RadialMenuScreen;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Works out what the player is pointing at in the radial menu. The mouse is used by default, but other inputs (e.g. a
 * controller thumbstick) can register a {@link Source} that takes over while it is active.
 */
public class RadialMenuInput {
    private static final List<Source> SOURCES = new ArrayList<>();

    public static void register(Source source) {
        SOURCES.add(source);
    }

    @Nullable
    private static Source getActiveSource() {
        for (Source source : SOURCES) {
            if (source.isActive()) {
                return source;
            }
        }
        return null;
    }

    /**
     * @return The angle being pointed at, using the same convention as {@link AngleHelper#getMouseAngle()}, or NaN if nothing is pointed at
     */
    public static double getPointerAngle() {
        Source source = getActiveSource();
        return source != null ? source.getAngle() : AngleHelper.getMouseAngle();
    }

    /**
     * @return True if the center of the radial menu is being pointed at
     */
    public static boolean isCenterHovered() {
        Source source = getActiveSource();
        return source != null ? Double.isNaN(source.getAngle()) : RadialMenuScreen.isMouseInCenter();
    }

    /**
     * @return The index of the hovered menu item, or -1 if no item is hovered
     */
    public static int getHoveredIndex() {
        double angle = getPointerAngle();
        if (Double.isNaN(angle)) {
            return -1;
        }
        angle -= RadialMenuScreen.ANGLE_PER_ITEM / 2;
        angle = 360 - angle;
        angle = AngleHelper.correctAngle(angle);

        for (int i = 0; i < RadialMenu.MAX_ITEMS; i++) {
            double currAngle = AngleHelper.correctAngle(RadialMenuScreen.ANGLE_PER_ITEM * i);
            double nextAngle = AngleHelper.correctAngle(RadialMenuScreen.ANGLE_PER_ITEM * i + RadialMenuScreen.ANGLE_PER_ITEM);
            if (angle > currAngle && angle < nextAngle) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @return True if any registered source is holding down its "open menu" button
     */
    public static boolean isOpenHeld() {
        for (Source source : SOURCES) {
            if (source.isOpenHeld()) {
                return true;
            }
        }
        return false;
    }

    public static void onMenuClosed() {
        SOURCES.forEach(Source::onMenuClosed);
    }

    /**
     * Called at the end of every client tick.
     */
    public static void tick() {
        SOURCES.forEach(Source::tick);
    }

    public interface Source {
        /**
         * @return True if this source should be used instead of the mouse
         */
        boolean isActive();

        /**
         * @return The angle being pointed at, using the same convention as {@link AngleHelper#getMouseAngle()}, or NaN when pointing at the center
         */
        double getAngle();

        /**
         * @return True while the button for opening the radial menu is held down
         */
        boolean isOpenHeld();

        default void onMenuClosed() {
        }

        /**
         * Called at the end of every client tick.
         */
        default void tick() {
        }
    }
}
