package org.interactunes.screensaver.services;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;

public class LocalAlbumCoverArtService implements IAlbumCoverArtService {

    private static final String ALBUMS_FOLDER_PATH = "images/albums";

    private final Random random = new Random(System.currentTimeMillis());

    @Override
    public String getAlbumCoverArt() {
        try {
            // Get the URL of the albums folder
            URL albumsFolderUrl = getClass().getClassLoader().getResource(ALBUMS_FOLDER_PATH);
            if (albumsFolderUrl == null) {
                System.err.println("Albums folder not found.");
                return null;
            }

            // Convert URL to a file path
            File albumsFolder = new File(albumsFolderUrl.toURI());

            // List all files in the albums folder
            File[] albumFiles = albumsFolder.listFiles();
            if (albumFiles == null || albumFiles.length == 0) {
                System.err.println("No album images found in the folder.");
                return null;
            }

            // Randomly select an album file
            File randomAlbumFile = albumFiles[random.nextInt(albumFiles.length)];

            // Return the path of the selected album file
            return randomAlbumFile.getPath();
        } catch (URISyntaxException e) {
            System.err.println("Error accessing albums folder: " + e.getMessage());
        }
        return null;
    }
}

