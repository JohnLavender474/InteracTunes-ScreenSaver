package org.interactunes.screensaver.frames;

import org.interactunes.screensaver.panels.AlbumImageCell;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class MainFrame {

    private final JFrame frame;
    private final JPanel albumGridPanel;
    private final BufferedImage[] albumImages;
    private final AlbumImageCell[] albumImageCells;

    public MainFrame() {
        frame = new JFrame();
        frame.setTitle("InteracTunes ScreenSaver");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 900);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                resizeAlbumImages();
            }
        });

        albumGridPanel = new JPanel();
        frame.add(albumGridPanel, BorderLayout.CENTER);
        albumGridPanel.setLayout(new GridLayout(3, 3, 10, 10));

        albumImages = loadAlbumImages();
        albumImageCells = new AlbumImageCell[9];
        populateAlbumGrid();
    }

    public void show() {
        frame.setVisible(true);
    }

    private BufferedImage[] loadAlbumImages() {
        BufferedImage[] images = new BufferedImage[9];
        for (int i = 0; i < 9; i++) {
            String imagePath = "images/albums/Animals.jpeg";
            URL imageURL = getClass().getClassLoader().getResource(imagePath);
            if (imageURL != null) {
                try {
                    images[i] = ImageIO.read(imageURL);
                } catch (IOException e) {
                    System.err.println("Error loading album image: " + e.getMessage());
                }
            }
        }
        return images;
    }

    private void populateAlbumGrid() {
        for (int i = 0; i < 9; i++) {
            albumImageCells[i] = new AlbumImageCell(albumImages[i]);
            albumGridPanel.add(albumImageCells[i].getPanel());
        }
    }

    private void resizeAlbumImages() {
        int cellWidth = albumGridPanel.getWidth() / 3; // Assuming 3 columns
        int cellHeight = albumGridPanel.getHeight() / 3; // Assuming 3 rows
        for (AlbumImageCell albumImageCell : albumImageCells) {
            albumImageCell.resize(cellWidth, cellHeight);
        }
    }
}
