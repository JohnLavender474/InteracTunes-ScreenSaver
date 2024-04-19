package org.interactunes.screensaver;

import org.interactunes.screensaver.frames.AlbumsFrame;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * The main class of the application.
 */
public class AppLauncher {

    /**
     * The entry point of the application.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    try {
                        UIManager.setLookAndFeel(info.getClassName());
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                             UnsupportedLookAndFeelException e) {
                        System.err.println("Error setting Nimbus look and feel: " + e.getMessage());
                    }
                    break;
                }
            }

            AlbumsFrame albumsFrame = new AlbumsFrame();
            albumsFrame.show();
        });
    }
}