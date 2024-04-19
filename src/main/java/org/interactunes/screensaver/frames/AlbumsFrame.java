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

public class AlbumsFrame implements IShowable {

    public static final int DEFAULT_BORDER_GAP = 10;
    public static final int DEFAULT_GRID_ROW_COUNT = 3;
    public static final int DEFAULT_CELL_GAP = 10;
    public static final int DEFAULT_WINDOW_WIDTH = 1200;
    public static final int DEFAULT_WINDOW_HEIGHT = 1200;
    public static final int MIN_WINDOW_WIDTH = 900;
    public static final int MIN_WINDOW_HEIGHT = 900;
    private static final int SETTINGS_FONT_SIZE = 20;
    private static final int IMAGE_CHANGE_INTERVAL_MS = 1000;

    private final Logger logger = Logger.getLogger(AlbumsFrame.class.getName());

    private final JFrame frame;
    private final JPanel albumGridPanel;
    private final SettingsFrame settingsFrame;
    private final Timer imageChangeTimer;

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
                super.windowClosed(e);
                settingsFrame.dispose();
            }
        });

        gridRowCount = DEFAULT_GRID_ROW_COUNT;

        albumGridPanel = new JPanel();
        frame.add(albumGridPanel, BorderLayout.CENTER);
        albumGridPanel.setLayout(new GridLayout(gridRowCount, gridRowCount, DEFAULT_CELL_GAP, DEFAULT_CELL_GAP));

        albumImageCells = new ArrayList<>();
        repopulateAlbumGrid();

        settingsFrame = new SettingsFrame(this);
        JButton settingsButton = new JButton("Settings");
        settingsButton.setFont(new Font("Arial", Font.PLAIN, UtilMethods.pointToPixel(SETTINGS_FONT_SIZE)));
        settingsButton.addActionListener(e -> settingsFrame.show());
        frame.add(settingsButton, BorderLayout.SOUTH);

        Random random = new Random(System.currentTimeMillis());
        imageChangeTimer = createTimer(random);
        imageChangeTimer.setRepeats(true);
        imageChangeTimer.start();
    }

    @Override
    public void show() {
        frame.setVisible(true);
    }

    void setGridRowCount(int gridRowCount) {
        this.gridRowCount = gridRowCount;
        updateGrid();
    }

    void updateGrid() {
        albumGridPanel.removeAll();
        albumGridPanel.setLayout(new GridLayout(gridRowCount, gridRowCount, 10, 10));
        repopulateAlbumGrid();
        frame.revalidate();
        frame.repaint();
        resizeAlbumImages();
    }

    void setImageUpdateDelay(int delay) {
        imageChangeTimer.setDelay(delay);
    }

    int getImageUpdateDelay() {
        return imageChangeTimer.getDelay();
    }

    private Timer createTimer(Random random) {
        return new Timer(IMAGE_CHANGE_INTERVAL_MS, e -> {
            int randomIndex = random.nextInt(albumImageCells.size());
            Image image = loadAlbumImage();
            if (image == null) {
                logger.log(Level.WARNING, "Failed to load album image.");
                return;
            }
            logger.log(Level.INFO, "For index: " + randomIndex + " image: " + image);
            AlbumImageCell albumImageCell = albumImageCells.get(randomIndex);
            albumImageCell.setImage(image);
        });
    }

    private int getCellCount() {
        return gridRowCount * gridRowCount;
    }

    private void repopulateAlbumGrid() {
        albumImageCells = new ArrayList<>();
        for (int i = 0; i < getCellCount(); i++) {
            Image image = loadAlbumImage();
            if (image == null) {
                logger.log(Level.WARNING, "Failed to load album image.");
                continue;
            }
            AlbumImageCell imageCell = new AlbumImageCell(image);
            albumImageCells.add(imageCell);
            albumGridPanel.add(imageCell.getPanel());
        }
    }

    private Image loadAlbumImage() {
        IAlbumCoverArtService albumCoverArtService = usingLocalAlbums ? localAlbumCoverArtService : discogsAlbumCoverArtService;
        return albumCoverArtService.getRandomAlbumCoverArt();
    }

    private void resizeAlbumImages() {
        int cellWidth = albumGridPanel.getWidth() / gridRowCount;
        int cellHeight = albumGridPanel.getHeight() / gridRowCount;
        albumImageCells.forEach(albumImageCell -> albumImageCell.resize(cellWidth, cellHeight));
    }
}
