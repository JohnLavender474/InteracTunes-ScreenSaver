package org.interactunes.screensaver.services;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * The service for getting album cover art.
 */
public interface IAlbumCoverArtService {

    /**
     * Fetches a random album cover art from the service.
     *
     * @return The album cover art.
     */
    Image getRandomAlbumCoverArt();

    /**
     * Fetches a list of album cover art from the service. The service may return up to {@code maxResults} images.
     *
     * @return The list of album cover art.
     */
    List<Image> getAlbumCoverArt(int maxResults);

}
