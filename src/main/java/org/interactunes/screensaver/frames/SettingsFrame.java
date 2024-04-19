package org.interactunes.screensaver.frames;

import org.interactunes.screensaver.utils.IDisposable;
import org.interactunes.screensaver.utils.IShowable;
import org.interactunes.screensaver.utils.UtilMethods;

import javax.swing.*;
import java.awt.*;

public class SettingsFrame implements IShowable, IDisposable {

    private static final int SETTINGS_FONT_SIZE = 24;
    private static final Integer[] GRID_SIZES = {2, 3, 4, 5, 6};
    private static final String LOCAL = "Local";
    private static final String DISCOGS = "Discogs";

    private final JFrame settingsFrame;

    public SettingsFrame(AlbumsFrame albumsFrame) {
        settingsFrame = new JFrame("Settings");
        settingsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        settingsFrame.setSize(800, 600);
        settingsFrame.setLocationRelativeTo(this.settingsFrame);
        settingsFrame.setVisible(true);

        JPanel settingsPanel = new JPanel();
        settingsFrame.add(settingsPanel);

        createGridSizeSetting(settingsPanel, albumsFrame);
        createImageUpdateSetting(settingsPanel, albumsFrame);
        createServiceSetting(settingsPanel, albumsFrame);
        createResetButton(settingsPanel, albumsFrame);
    }

    @Override
    public void show() {
        settingsFrame.setVisible(true);
    }

    @Override
    public void dispose() {
        settingsFrame.dispose();
    }

    private void createGridSizeSetting(JPanel parent, AlbumsFrame albumsFrame) {
        JLabel gridSizeLabel = new JLabel("Grid Size:");
        gridSizeLabel.setFont(new Font("Arial", Font.PLAIN, UtilMethods.pointToPixel(SETTINGS_FONT_SIZE)));
        JComboBox<Integer> gridSizeComboBox = new JComboBox<>(GRID_SIZES);
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
        parent.add(gridSizeLabel);
        parent.add(gridSizeComboBox);
    }

    private void createImageUpdateSetting(JPanel parent, AlbumsFrame albumsFrame) {
        JLabel imageUpdateDelayLabel = new JLabel("Image Update Delay (ms):");
        imageUpdateDelayLabel.setFont(new Font("Arial", Font.PLAIN, UtilMethods.pointToPixel(SETTINGS_FONT_SIZE)));
        JTextField imageUpdateDelayTextField = new JTextField(String.valueOf(albumsFrame.getImageUpdateDelay()));
        imageUpdateDelayTextField.setFont(new Font("Arial", Font.PLAIN, UtilMethods.pointToPixel(SETTINGS_FONT_SIZE)));
        imageUpdateDelayTextField.addActionListener(e -> {
            try {
                int imageUpdateDelay = Integer.parseInt(imageUpdateDelayTextField.getText());
                albumsFrame.setImageUpdateDelay(imageUpdateDelay);
            } catch (NumberFormatException ex) {
                imageUpdateDelayTextField.setText(String.valueOf(albumsFrame.getImageUpdateDelay()));
            }
        });
        parent.add(imageUpdateDelayLabel);
        parent.add(imageUpdateDelayTextField);
    }

    private void createServiceSetting(JPanel parent, AlbumsFrame albumsFrame) {
        JLabel serviceLabel = new JLabel("Service:");
        serviceLabel.setFont(new Font("Arial", Font.PLAIN, UtilMethods.pointToPixel(SETTINGS_FONT_SIZE)));
        JComboBox<String> serviceComboBox = new JComboBox<>(new String[]{LOCAL, DISCOGS});
        serviceComboBox.setFont(new Font("Arial", Font.PLAIN, UtilMethods.pointToPixel(SETTINGS_FONT_SIZE)));
        serviceComboBox.setSelectedItem(albumsFrame.isUsingLocalAlbums() ? LOCAL : DISCOGS);
        serviceComboBox.addActionListener(e -> {
            Object selection = serviceComboBox.getSelectedItem();
            if (!(selection instanceof String service)) {
                return;
            }
            albumsFrame.setUsingLocalAlbums(service.equals(LOCAL));
        });
        parent.add(serviceLabel);
        parent.add(serviceComboBox);
    }

    private void createResetButton(JPanel parent, AlbumsFrame albumsFrame) {
        JButton resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Arial", Font.PLAIN, UtilMethods.pointToPixel(SETTINGS_FONT_SIZE)));
        resetButton.addActionListener(e -> albumsFrame.updateGrid());
        parent.add(resetButton);
    }
}
