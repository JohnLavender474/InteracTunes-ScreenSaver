package org.interactunes.screensaver.frames;

import org.interactunes.screensaver.utils.IShowable;
import org.interactunes.screensaver.utils.UtilMethods;

import javax.swing.*;
import java.awt.*;

public class SettingsFrame implements IShowable {

    private static final int SETTINGS_FONT_SIZE = 24;

    private final JFrame settingsFrame;

    public SettingsFrame(AlbumsFrame albumsFrame) {
        settingsFrame = new JFrame("Settings");
        settingsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        settingsFrame.setSize(800, 600);
        settingsFrame.setLocationRelativeTo(this.settingsFrame);
        settingsFrame.setVisible(true);

        JPanel settingsPanel = new JPanel();
        settingsFrame.add(settingsPanel);

        JLabel gridSizeLabel = new JLabel("Grid Size:");
        gridSizeLabel.setFont(new Font("Arial", Font.PLAIN, UtilMethods.pointToPixel(SETTINGS_FONT_SIZE)));
        JComboBox<Integer> gridSizeComboBox = new JComboBox<>(new Integer[]{2, 3, 4, 5, 6});
        gridSizeComboBox.setFont(new Font("Arial", Font.PLAIN, UtilMethods.pointToPixel(SETTINGS_FONT_SIZE)));
        gridSizeComboBox.setSelectedItem(albumsFrame.getGridRowCount());
        gridSizeComboBox.addActionListener(e -> {
            Object selection = gridSizeComboBox.getSelectedItem();
            if (!(selection instanceof Integer)) {
                return;
            }
            int gridRowCount = (int) selection;
            albumsFrame.setGridRowCount(gridRowCount);
        });

        settingsPanel.add(gridSizeLabel);
        settingsPanel.add(gridSizeComboBox);
    }

    @Override
    public void show() {
        settingsFrame.setVisible(true);
    }

    public void dispose() {
        settingsFrame.dispose();
    }
}
