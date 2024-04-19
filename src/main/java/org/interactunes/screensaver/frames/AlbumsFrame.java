package org.interactunes.screensaver.frames;

import lombok.Getter;
import org.interactunes.screensaver.panels.AlbumImageCell;
import org.interactunes.screensaver.utils.IShowable;
import org.interactunes.screensaver.utils.UtilMethods;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

public class AlbumsFrame implements IShowable {

    public static final int DEFAULT_BORDER_GAP = 10;
    public static final int DEFAULT_GRID_ROW_COUNT = 3;
    public static final int DEFAULT_CELL_GAP = 10;
    public static final int DEFAULT_WINDOW_WIDTH = 900;
    public static final int DEFAULT_WINDOW_HEIGHT = 900;
    private static final int SETTINGS_FONT_SIZE = 20;
    private static final int IMAGE_CHANGE_INTERVAL_MS = 2000;

    private final JFrame frame;
    private final JPanel albumGridPanel;
    private final SettingsFrame settingsFrame;

    private AlbumImageCell[] albumImageCells;

    @Getter
    private int gridRowCount;

    public AlbumsFrame() {
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
        // TODO: should load random image, not the same one each time
        String imagePath = "images/albums/Pink Floyd - Animals.jpeg";
        URL imageURL = getClass().getClassLoader().getResource(imagePath);
        if (imageURL != null) {
            try {
                return ImageIO.read(imageURL);
            } catch (IOException e) {
                System.err.println("Error loading album image: " + e.getMessage());
            }
        }
        return null;
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
