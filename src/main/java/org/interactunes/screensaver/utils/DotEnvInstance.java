package org.interactunes.screensaver.utils;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * A singleton class for the Dotenv instance.
 */
public class DotEnvInstance {

    private static Dotenv dotenv;

    /**
     * Gets the value of the key from the Dotenv instance.
     *
     * @param key The key.
     * @return The value of the key.
     */
    public static String get(String key) {
        return getInstance().get(key);
    }

    /**
     * Gets the Dotenv instance.
     *
     * @return The Dotenv instance.
     */
    public static Dotenv getInstance() {
        if (dotenv == null) {
            dotenv = Dotenv.load();
        }
        return dotenv;
    }

}
