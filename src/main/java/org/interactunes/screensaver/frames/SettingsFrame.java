package org.interactunes.screensaver.frames;

import org.interactunes.screensaver.utils.IDisposable;
import org.interactunes.screensaver.utils.IShowable;
import org.interactunes.screensaver.utils.UtilMethods;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

/**
 * A frame that displays settings for the album cover art display.
 */
public class SettingsFrame implements IShowable, IDisposable {

    private static final int SETTINGS_FONT_SIZE = 24;
    private static final Integer[] GRID_SIZES = {2, 3, 4, 5, 6};
    private static final Integer[] IMAGE_UPDATE_DELAYS = {3, 4, 5};
    private static final String LOCAL = "Local";
    private static final String DISCOGS = "Discogs";

    private final Logger logger = Logger.getLogger(SettingsFrame.class.getName());
    private final JFrame settingsFrame;

    private JLabel queryLabel;
    private JTextField queryTextField;

    /**
     * Creates a new settings frame.
     *
     * @param albumsFrame the albums frame
     */
    public SettingsFrame(AlbumsFrame albumsFrame) {
        settingsFrame = new JFrame("Settings");
        settingsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        settingsFrame.setMinimumSize(new Dimension(800, 600));
        settingsFrame.setLocationRelativeTo(settingsFrame);
        settingsFrame.setVisible(true);

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsFrame.add(settingsPanel);

        createGridSizeSetting(settingsPanel, albumsFrame);
        createImageUpdateSetting(settingsPanel, albumsFrame);
        createServiceSetting(settingsPanel, albumsFrame);
        createQuerySetting(settingsPanel, albumsFrame);
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
        JComboBox<Integer> imageUpdateComboBox = new JComboBox<>(IMAGE_UPDATE_DELAYS);
        imageUpdateComboBox.setFont(new Font("Arial", Font.PLAIN, UtilMethods.pointToPixel(SETTINGS_FONT_SIZE)));
        imageUpdateComboBox.setSelectedItem(albumsFrame.getImageUpdateDelay());
        imageUpdateComboBox.addActionListener(e -> {
            Object selection = imageUpdateComboBox.getSelectedItem();
            if (!(selection instanceof Integer seconds)) {
                return;
            }
            albumsFrame.setImageUpdateDelay(seconds);
        });
        parent.add(imageUpdateDelayLabel);
        parent.add(imageUpdateComboBox);
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
            boolean showQuerySetting = service.equals(DISCOGS);
            queryLabel.setVisible(showQuerySetting);
            queryTextField.setVisible(showQuerySetting);
        });
        parent.add(serviceLabel);
        parent.add(serviceComboBox);
    }

    private void createQuerySetting(JPanel parent, AlbumsFrame albumsFrame) {
        queryLabel = new JLabel("Query (press ENTER to update):");
        queryLabel.setFont(new Font("Arial", Font.PLAIN, UtilMethods.pointToPixel(SETTINGS_FONT_SIZE)));
        queryLabel.setVisible(!albumsFrame.isUsingLocalAlbums());
        queryTextField = new JTextField(albumsFrame.getDiscogsAlbumCoverArtService().getSearchQuery());
        queryTextField.setFont(new Font("Arial", Font.PLAIN, UtilMethods.pointToPixel(SETTINGS_FONT_SIZE)));
        queryTextField.addActionListener(e -> {
            albumsFrame.getDiscogsAlbumCoverArtService().setSearchQuery(queryTextField.getText());
            logger.info("Query set to: " + queryTextField.getText());
        });
        queryTextField.setVisible(!albumsFrame.isUsingLocalAlbums());
        parent.add(queryLabel);
        parent.add(queryTextField);
    }

    private void createResetButton(JPanel parent, AlbumsFrame albumsFrame) {
        JButton resetButton = new JButton("Reset");
        resetButton.setHorizontalAlignment(SwingConstants.CENTER);
        resetButton.setFont(new Font("Arial", Font.PLAIN, UtilMethods.pointToPixel(SETTINGS_FONT_SIZE)));
        resetButton.addActionListener(e -> albumsFrame.updateGrid());
        parent.add(resetButton);
    }
}
