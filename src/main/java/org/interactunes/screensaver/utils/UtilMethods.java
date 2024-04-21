package org.interactunes.screensaver.utils;

import java.awt.*;
import java.util.Random;

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

    /**
     * Shuffles the array.
     * @param array the array
     * @param <T> the type
     */
    public static <T> void shuffleArray(T[] array) {
        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < array.length; i++) {
            int randomIndexToSwap = rand.nextInt(array.length);
            T temp = array[randomIndexToSwap];
            array[randomIndexToSwap] = array[i];
            array[i] = temp;
        }
    }

}
