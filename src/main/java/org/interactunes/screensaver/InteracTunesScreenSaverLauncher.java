package org.interactunes.screensaver;

import javax.swing.*;

public class InteracTunesScreenSaverLauncher {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow mainWindow = new MainWindow();
            mainWindow.show();
        });
    }
}