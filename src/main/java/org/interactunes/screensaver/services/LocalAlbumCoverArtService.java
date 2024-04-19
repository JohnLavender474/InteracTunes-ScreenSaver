package org.interactunes.screensaver.services;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocalAlbumCoverArtService implements IAlbumCoverArtService {

    private static final String ALBUMS_FOLDER_PATH = "images/albums";
    private static final int MAX_RESULTS_RANDOM = 10;

    private final Logger logger = Logger.getLogger(LocalAlbumCoverArtService.class.getName());

    /**
     * {@inheritDoc}
     * <p>
     * Fetches a random album cover art from the service. Might be null if no album cover art is found.
     *
     * @return The album cover art or null if no album cover art is found.
     */
    @Override
    public Image getRandomAlbumCoverArt() {
        List<Image> albumCoverArt = getAlbumCoverArt(MAX_RESULTS_RANDOM);
        return albumCoverArt.isEmpty() ? null : albumCoverArt.get(0);
    }

    @Override
    public List<Image> getAlbumCoverArt(int maxResults) {
        try {
            URL albumsFolderUrl = getClass().getClassLoader().getResource(ALBUMS_FOLDER_PATH);
            if (albumsFolderUrl == null) {
                logger.log(Level.SEVERE, "Albums folder not found.");
                return null;
            }

            File albumsFolder = new File(albumsFolderUrl.toURI());
            List<File> albumFiles = new ArrayList<>(Arrays.stream(Objects.requireNonNull(albumsFolder.listFiles())).toList());
            if (albumFiles.isEmpty()) {
                logger.log(Level.SEVERE, "No album images found in the folder.");
                return null;
            }
            Collections.shuffle(albumFiles);

            List<Image> images = new ArrayList<>();
            for (int i = 0; i < Math.min(maxResults, albumFiles.size()); i++) {
                File albumFile = albumFiles.get(i);
                Image image = ImageIO.read(albumFile);
                if (image == null) {
                    logger.log(Level.WARNING, "Error reading album image: " + albumFile.getName());
                    continue;
                }
                images.add(image);
            }

            logger.log(Level.INFO, "Fetched " + images.size() + " album images.");
            return images;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error accessing albums folder: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}

