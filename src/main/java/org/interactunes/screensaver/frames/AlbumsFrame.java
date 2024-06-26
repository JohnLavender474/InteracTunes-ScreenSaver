package org.interactunes.screensaver.frames;

import lombok.Getter;
import lombok.Setter;
import org.interactunes.screensaver.panels.AlbumImageCell;
import org.interactunes.screensaver.services.DiscogsAlbumCoverArtService;
import org.interactunes.screensaver.services.IAlbumCoverArtService;
import org.interactunes.screensaver.services.LocalAlbumCoverArtService;
import org.interactunes.screensaver.utils.IShowable;
import org.interactunes.screensaver.utils.UtilMethods;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A frame that displays album cover art.
 */
public class AlbumsFrame implements IShowable {

    private static final int DEFAULT_BORDER_GAP = 10;
    private static final int DEFAULT_GRID_ROW_COUNT = 3;
    private static final int DEFAULT_CELL_GAP = 10;
    private static final int DEFAULT_WINDOW_WIDTH = 900;
    private static final int DEFAULT_WINDOW_HEIGHT = 900;
    private static final int MIN_WINDOW_WIDTH = 900;
    private static final int MIN_WINDOW_HEIGHT = 900;
    private static final int SETTINGS_FONT_SIZE = 16;
    private static final int LOADING_FONT_SIZE = 36;
    private static final int DEFAULT_IMAGE_CHANGE_INTERVAL_MS = 2000;
    private static final int MAX_LOGS = 16;

    private final Logger logger = Logger.getLogger(AlbumsFrame.class.getName());

    private final JFrame frame;
    private final JPanel albumGridPanel;
    private final SettingsFrame settingsFrame;
    private final LogFrame logFrame;
    private final Timer imageChangeTimer;

    private final JLabel loadingLabel;
    private final JPanel loadingPanel;

    @Getter
    private final LocalAlbumCoverArtService localAlbumCoverArtService;
    @Getter
    private final DiscogsAlbumCoverArtService discogsAlbumCoverArtService;

    private List<AlbumImageCell> albumImageCells;

    @Setter
    @Getter
    private boolean usingLocalAlbums;

    @Getter
    private int gridRowCount;

    private boolean updating;

    /**
     * Create a new instance of the AlbumsFrame class.
     */
    public AlbumsFrame() {
        usingLocalAlbums = true;
        localAlbumCoverArtService = new LocalAlbumCoverArtService();
        discogsAlbumCoverArtService = new DiscogsAlbumCoverArtService();

        frame = new JFrame();
        frame.setTitle("InteracTunes ScreenSaver - Albums");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
        frame.setMinimumSize(new Dimension(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT));
        frame.setLayout(new BorderLayout(DEFAULT_BORDER_GAP, DEFAULT_BORDER_GAP));
        frame.setLocationRelativeTo(null);
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                resizeAlbumImages();
            }
        });
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                settingsFrame.dispose();
                logFrame.dispose();
                imageChangeTimer.stop();
                super.windowClosed(e);
            }
        });

        gridRowCount = DEFAULT_GRID_ROW_COUNT;

        albumGridPanel = new JPanel();
        frame.add(albumGridPanel, BorderLayout.CENTER);
        albumGridPanel.setLayout(new GridLayout(gridRowCount, gridRowCount, DEFAULT_CELL_GAP, DEFAULT_CELL_GAP));

        albumImageCells = new ArrayList<>();
        repopulateAlbumGrid();

        Random random = new Random(System.currentTimeMillis());
        imageChangeTimer = createTimer(random);
        imageChangeTimer.setRepeats(true);
        imageChangeTimer.start();

        JPanel buttonsRow = new JPanel(new GridLayout(1, 2));
        frame.add(buttonsRow, BorderLayout.SOUTH);

        settingsFrame = new SettingsFrame(this);
        JButton settingsButton = new JButton("Settings");
        settingsButton.setPreferredSize(new Dimension(100, 100));
        settingsButton.setFont(new Font("Arial", Font.PLAIN, UtilMethods.pointToPixel(SETTINGS_FONT_SIZE)));
        settingsButton.addActionListener(e -> settingsFrame.show());
        buttonsRow.add(settingsButton);

        logFrame = new LogFrame(MAX_LOGS);
        JButton logButton = new JButton("Logs");
        logButton.setPreferredSize(new Dimension(100, 100));
        logButton.setFont(new Font("Arial", Font.PLAIN, UtilMethods.pointToPixel(SETTINGS_FONT_SIZE)));
        logButton.addActionListener(e -> logFrame.show());
        buttonsRow.add(logButton);

        loadingLabel = new JLabel("Loading...");
        loadingLabel.setFont(new Font("Arial", Font.PLAIN, UtilMethods.pointToPixel(LOADING_FONT_SIZE)));
        loadingLabel.setBackground(Color.BLACK);
        loadingLabel.setForeground(Color.WHITE);

        loadingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                loadingLabel.setBounds((getWidth() - loadingLabel.getPreferredSize().width) / 2, (getHeight() - loadingLabel.getPreferredSize().height) / 2, loadingLabel.getPreferredSize().width, loadingLabel.getPreferredSize().height);
            }
        };
        loadingPanel.setOpaque(false);
        loadingPanel.setLayout(null);
        loadingPanel.add(loadingLabel);
        loadingLabel.setVisible(false);

        frame.setGlassPane(loadingPanel);
    }

    @Override
    public void show() {
        frame.setVisible(true);
    }

    /**
     * Add a log to the log frame.
     *
     * @param log the log to add
     */
    public void addLog(String log) {
        if (logFrame != null) {
            logFrame.addLog(log);
        }
    }

    /**
     * Set the number of rows and columns on the album grid.
     *
     * @param gridRowCount the number of rows and columns on the album grid
     */
    void setGridRowCount(int gridRowCount) {
        this.gridRowCount = gridRowCount;
        updateGrid();
    }

    private void setUpdating(boolean updating) {
        this.updating = updating;
        loadingPanel.setVisible(updating);
        loadingLabel.setVisible(updating);
    }

    /**
     * Update the album grid. Repopulates the album grid with new album images. Resizes the album images.
     */
    void updateGrid() {
        setUpdating(true);

        SwingUtilities.invokeLater(() -> {
            albumGridPanel.removeAll();
            albumGridPanel.setLayout(new GridLayout(gridRowCount, gridRowCount, 10, 10));
            repopulateAlbumGrid();
            resizeAlbumImages();

            SwingUtilities.invokeLater(() -> {
                setUpdating(false);
                frame.repaint();
            });
        });
    }

    /**
     * Set the delay between image updates in seconds.
     *
     * @param delayInSeconds the delay between image updates in seconds
     */
    void setImageUpdateDelay(int delayInSeconds) {
        imageChangeTimer.setDelay(delayInSeconds * 1000);
    }

    /**
     * Get the delay between image updates in seconds.
     *
     * @return the delay between image updates in seconds
     */
    int getImageUpdateDelay() {
        return imageChangeTimer.getDelay() / 1000;
    }

    private Timer createTimer(Random random) {
        return new Timer(DEFAULT_IMAGE_CHANGE_INTERVAL_MS, e -> {
            if (updating) {
                return;
            }

            int randomIndex = random.nextInt(albumImageCells.size());

            Image image = loadSingleAlbumImage();
            if (image == null) {
                String error = "Failed to load album image.";
                logger.log(Level.WARNING, error);
                addLog(error);

                return;
            }

            String info = "Replaced image at index " + randomIndex + " with new image.";
            logger.log(Level.INFO, info);
            addLog(info);

            AlbumImageCell albumImageCell = albumImageCells.get(randomIndex);
            albumImageCell.setImage(image);
        });
    }

    private int getCellCount() {
        return gridRowCount * gridRowCount;
    }

    private void repopulateAlbumGrid() {
        albumImageCells = new ArrayList<>();
        List<Image> images = loadAlbumImages(getCellCount());
        for (Image image : images) {
            AlbumImageCell imageCell = new AlbumImageCell(image);
            albumImageCells.add(imageCell);
            albumGridPanel.add(imageCell.getPanel());
        }
    }

    private IAlbumCoverArtService getAlbumCoverArtService(boolean usingLocalAlbums) {
        return usingLocalAlbums ? localAlbumCoverArtService : discogsAlbumCoverArtService;
    }

    private List<Image> loadAlbumImages(int amount) {
        logger.log(Level.INFO, "Loading " + amount + " album images.");
        addLog("Loading " + amount + " album images.");

        IAlbumCoverArtService albumCoverArtService = getAlbumCoverArtService(usingLocalAlbums);
        List<Image> images = albumCoverArtService.getAlbumCoverArt(amount);

        // If the amount of images loaded is less than the requested amount, try again with the other service.
        if (images.size() < amount) {
            String error = "Failed to load album images using " + albumCoverArtService.getClass().getSimpleName() + ". Falling back to other service.";
            logger.log(Level.WARNING, error);
            addLog(error);

            albumCoverArtService = getAlbumCoverArtService(!usingLocalAlbums);

            int remaining = amount - images.size();
            images.addAll(albumCoverArtService.getAlbumCoverArt(remaining));
        }

        logger.log(Level.INFO, "Loaded " + images.size() + " album images.");
        addLog("Loaded " + images.size() + " album images.");
        return images;
    }

    private Image loadSingleAlbumImage() {
        IAlbumCoverArtService albumCoverArtService = getAlbumCoverArtService(usingLocalAlbums);
        Image image = albumCoverArtService.getRandomAlbumCoverArt();

        // If the image is null, try again with the other service.
        if (image == null) {
            String error = "Failed to load album image using " + albumCoverArtService.getClass().getSimpleName() + ". Falling back to other service.";
            logger.log(Level.WARNING, error);
            addLog(error);

            albumCoverArtService = getAlbumCoverArtService(!usingLocalAlbums);
            image = albumCoverArtService.getRandomAlbumCoverArt();
        }

        return image;
    }

    private void resizeAlbumImages() {
        boolean isUpdatingOnStart = updating;
        if (!isUpdatingOnStart) {
            setUpdating(true);
        }

        int cellWidth = albumGridPanel.getWidth() / gridRowCount;
        int cellHeight = albumGridPanel.getHeight() / gridRowCount;
        albumImageCells.forEach(albumImageCell -> albumImageCell.resize(cellWidth, cellHeight));

        if (!isUpdatingOnStart) {
            setUpdating(false);
        }
    }
}
