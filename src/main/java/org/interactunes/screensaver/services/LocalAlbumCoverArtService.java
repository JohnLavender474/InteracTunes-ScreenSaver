package org.interactunes.screensaver.services;

import org.interactunes.screensaver.utils.UtilMethods;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The service for fetching album cover art from the local file system.
 */
public class LocalAlbumCoverArtService implements IAlbumCoverArtService {

    private static final String ALBUMS_FOLDER_PATH = "images/albums";

    private final Logger logger;
    private final Queue<String> queue;

    /**
     * Creates a new local album cover art service.
     */
    public LocalAlbumCoverArtService() {
        logger = Logger.getLogger(LocalAlbumCoverArtService.class.getName());
        queue = new LinkedList<>();
        reloadQueueWithLocalPaths();
    }

    private void reloadQueueWithLocalPaths() {
        queue.clear();
        getAlbumCoverArtPaths(queue);
    }

    private void getAlbumCoverArtPaths(Collection<String> collection) {
        try {
            URL albumsFolderUrl = getClass().getClassLoader().getResource(ALBUMS_FOLDER_PATH);
            if (albumsFolderUrl == null) {
                logger.log(Level.SEVERE, "Albums folder not found.");
                return;
            }

            File albumsFolder = new File(albumsFolderUrl.toURI());
            File[] albumFiles = albumsFolder.listFiles();
            if (albumFiles == null || albumFiles.length == 0) {
                logger.log(Level.SEVERE, "No album images found in the folder.");
                return;
            }
            UtilMethods.shuffleArray(albumFiles);
            for (File albumFile : albumFiles) {
                collection.add(albumFile.getAbsolutePath());
            }

            logger.log(Level.INFO, "Fetched " + collection.size() + " album images.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error accessing albums folder: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Fetches a random album cover art from the service. Might be null if no album cover art is found.
     *
     * @return The album cover art or null if no album cover art is found.
     */
    @Override
    public Image getRandomAlbumCoverArt() {
        if (queue.isEmpty()) {
            reloadQueueWithLocalPaths();
        }
        String path = queue.poll();
        if (path == null) {
            logger.log(Level.WARNING, "No album cover art found.");
            return null;
        }
        try {
            return ImageIO.read(new File(path));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to get album cover art. Error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Image> getAlbumCoverArt(int maxResults) {
        List<Image> images = new ArrayList<>();

        while (images.size() < maxResults) {
            if (queue.isEmpty()) {
                reloadQueueWithLocalPaths();
            }
            String path = queue.poll();
            if (path == null) {
                logger.log(Level.WARNING, "No album cover art found for path.");
                break;
            }
            try {
                Image image = ImageIO.read(new File(path));
                if (image == null) {
                    logger.log(Level.WARNING, "Error reading album image: " + path);
                    continue;
                }
                images.add(image);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to get album cover art. Error: " + e.getMessage());
            }
        }

        return images;
    }
}

