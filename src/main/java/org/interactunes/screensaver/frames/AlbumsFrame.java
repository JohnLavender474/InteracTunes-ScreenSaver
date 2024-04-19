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
import java.awt.image.BufferedImage;
import java.util.Random;

public class AlbumsFrame implements IShowable {

    public static final int DEFAULT_BORDER_GAP = 10;
    public static final int DEFAULT_GRID_ROW_COUNT = 3;
    public static final int DEFAULT_CELL_GAP = 10;
    public static final int DEFAULT_WINDOW_WIDTH = 1200;
    public static final int DEFAULT_WINDOW_HEIGHT = 1200;
    private static final int SETTINGS_FONT_SIZE = 20;
    private static final int IMAGE_CHANGE_INTERVAL_MS = 2000;

    private final JFrame frame;
    private final JPanel albumGridPanel;
    private final SettingsFrame settingsFrame;

    @Getter
    private final LocalAlbumCoverArtService localAlbumCoverArtService;
    @Getter
    private final DiscogsAlbumCoverArtService discogsAlbumCoverArtService;

    private AlbumImageCell[] albumImageCells;

    @Setter
    private boolean useLocalAlbums;

    @Getter
    private int gridRowCount;

    public AlbumsFrame() {
        useLocalAlbums = true;
        localAlbumCoverArtService = new LocalAlbumCoverArtService();
        discogsAlbumCoverArtService = new DiscogsAlbumCoverArtService();

        frame = new JFrame();
        frame.setTitle("InteracTunes ScreenSaver - Albums");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
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

        albumImageCells = new AlbumImageCell[gridRowCount * gridRowCount];
        populateAlbumGrid();

        settingsFrame = new SettingsFrame(this);
        JButton settingsButton = new JButton("Settings");
        settingsButton.setFont(new Font("Arial", Font.PLAIN, UtilMethods.pointToPixel(SETTINGS_FONT_SIZE)));
        settingsButton.addActionListener(e -> settingsFrame.show());
        frame.add(settingsButton, BorderLayout.SOUTH);

        Random random = new Random(System.currentTimeMillis());
        Timer imageChangeTimer = new Timer(IMAGE_CHANGE_INTERVAL_MS, e -> {
            int randomIndex = random.nextInt(getCellCount());
            BufferedImage image = loadAlbumImage();
            if (image != null) {
                albumImageCells[randomIndex].setImage(image);
            }
        });
        imageChangeTimer.setRepeats(true);
        imageChangeTimer.start();
    }

    @Override
    public void show() {
        frame.setVisible(true);
    }

    public void setGridRowCount(int gridRowCount) {
        this.gridRowCount = gridRowCount;
        updateGrid();
    }

    public int getCellCount() {
        return gridRowCount * gridRowCount;
    }

    private BufferedImage loadAlbumImage() {
        IAlbumCoverArtService albumCoverArtService = useLocalAlbums ? localAlbumCoverArtService : discogsAlbumCoverArtService;
        return albumCoverArtService.getRandomAlbumCoverArt();
    }

    private void populateAlbumGrid() {
        albumImageCells = new AlbumImageCell[getCellCount()];
        for (int i = 0; i < getCellCount(); i++) {
            BufferedImage image = loadAlbumImage();
            if (image == null) {
                // TODO: load error image, i.e. image = ErrorImage.getInstance().getImage();
                continue;
            }
            albumImageCells[i] = new AlbumImageCell(image);
            albumGridPanel.add(albumImageCells[i].getPanel());
        }
    }

    private void resizeAlbumImages() {
        int cellWidth = albumGridPanel.getWidth() / gridRowCount;
        int cellHeight = albumGridPanel.getHeight() / gridRowCount;
        for (AlbumImageCell albumImageCell : albumImageCells) {
            albumImageCell.resize(cellWidth, cellHeight);
        }
    }

    private void updateGrid() {
        albumGridPanel.removeAll();
        albumGridPanel.setLayout(new GridLayout(gridRowCount, gridRowCount, 10, 10));
        populateAlbumGrid();
        frame.revalidate();
        frame.repaint();
    }
}
