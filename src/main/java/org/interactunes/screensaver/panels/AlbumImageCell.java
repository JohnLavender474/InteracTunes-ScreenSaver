package org.interactunes.screensaver.panels;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class AlbumImageCell {

    public static final int DEFAULT_INIT_SIZE = 300;

    private final JLabel albumImageLabel;
    @Getter
    private final JPanel panel;

    private BufferedImage image;
    private int imageSize;

    public AlbumImageCell(BufferedImage image) {
        this(image, DEFAULT_INIT_SIZE);
    }

    public AlbumImageCell(BufferedImage image, int imageSize) {
        this.image = image;
        this.imageSize = imageSize;

        panel = new JPanel();
        panel.setPreferredSize(new Dimension(imageSize, imageSize));

        albumImageLabel = new JLabel();
        albumImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        albumImageLabel.setVerticalAlignment(SwingConstants.CENTER);

        Image scaledImage = image.getScaledInstance(imageSize, imageSize, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaledImage);
        albumImageLabel.setIcon(icon);

        panel.add(albumImageLabel);
    }

    public void resize(int cellWidth, int cellHeight) {
        imageSize = Math.min(cellWidth, cellHeight);
        Image scaledImage = image.getScaledInstance(imageSize, imageSize, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaledImage);
        albumImageLabel.setIcon(icon);
        panel.setBackground(Color.BLACK);
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        updateImage();
    }

    private void updateImage() {
        Image scaledImage = image.getScaledInstance(imageSize, imageSize, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaledImage);
        albumImageLabel.setIcon(icon);
    }
}
