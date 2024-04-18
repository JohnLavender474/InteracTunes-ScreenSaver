package org.interactunes.screensaver.utils;

import java.awt.*;

/**
 * Utility methods for the application.
 */
public class UtilMethods {

    /**
     * Convert point to pixel. This method is used to convert font size from point to pixel due to the fact that
     * {@link Font#Font(String, int, int)} constructor only accepts font size in pixel.
     *
     * @param pt font size in point
     * @return font size in pixel
     */
    public static int pointToPixel(float pt) {
        int ppi = Toolkit.getDefaultToolkit().getScreenResolution();
        return Math.round(pt / ((float) 72 / ppi));
    }

}
