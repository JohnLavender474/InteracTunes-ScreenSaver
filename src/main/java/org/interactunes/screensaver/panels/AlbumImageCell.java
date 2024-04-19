package org.interactunes.screensaver.panels;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * A cell for displaying album images.
 */
public class AlbumImageCell {

    public static final int DEFAULT_INIT_SIZE = 400;

    private final JLabel albumImageLabel;
    @Getter
    private final JPanel panel;

    private Image image;
    private int imageSize;

    /**
     * Creates a new album image cell.
     *
     * @param image The image to display.
     */
    public AlbumImageCell(Image image) {
        this(image, DEFAULT_INIT_SIZE);
    }

    /**
     * Creates a new album image cell.
     *
     * @param image     The image to display.
     * @param imageSize The size of the image.
     */
    public AlbumImageCell(Image image, int imageSize) {
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

    /**
     * Resizes the cell.
     *
     * @param cellWidth  The width of the cell.
     * @param cellHeight The height of the cell.
     */
    public void resize(int cellWidth, int cellHeight) {
        imageSize = Math.min(cellWidth, cellHeight);
        Image scaledImage = image.getScaledInstance(imageSize, imageSize, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaledImage);
        albumImageLabel.setIcon(icon);
        panel.setBackground(Color.BLACK);
    }

    /**
     * Sets the image to display.
     *
     * @param image The image to display.
     */
    public void setImage(Image image) {
        this.image = image;
        updateImage();
    }

    private void updateImage() {
        Image scaledImage = image.getScaledInstance(imageSize, imageSize, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaledImage);
        albumImageLabel.setIcon(icon);
    }
}
