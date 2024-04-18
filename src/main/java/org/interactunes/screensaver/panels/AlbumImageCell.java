package org.interactunes.screensaver.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class AlbumImageCell {

    public static final int DEFAULT_INIT_SIZE = 300;

    private final JPanel panel;
    private final BufferedImage image;
    private final JLabel albumImageLabel;

    public AlbumImageCell(BufferedImage image) {
        this(image, DEFAULT_INIT_SIZE);
    }

    public AlbumImageCell(BufferedImage image, int size) {
        this.image = image;
        panel = new JPanel();
        panel.setPreferredSize(new Dimension(size, size));

        albumImageLabel = new JLabel();
        albumImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        albumImageLabel.setVerticalAlignment(SwingConstants.CENTER);

        Image scaledImage = image.getScaledInstance(size, size, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaledImage);
        albumImageLabel.setIcon(icon);

        panel.add(albumImageLabel);
    }

    public JPanel getPanel() {
        return panel;
    }

    public void resize(int cellWidth, int cellHeight) {
        int imageSize = Math.min(cellWidth, cellHeight);
        Image scaledImage = image.getScaledInstance(imageSize, imageSize, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaledImage);
        albumImageLabel.setIcon(icon);
        panel.setBackground(Color.BLACK);
    }
}
