package org.interactunes.screensaver.services;

import lombok.Getter;
import lombok.NonNull;
import org.interactunes.screensaver.utils.DotEnvInstance;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The service for fetching album cover art via the Discogs API.
 */
public class DiscogsAlbumCoverArtService implements IAlbumCoverArtService {

    private static final String BASE_URL = "https://api.discogs.com/database/search";
    private static final String API_KEY = DotEnvInstance.get("DISCOGS_API_KEY");
    private static final String API_SECRET = DotEnvInstance.get("DISCOGS_API_SECRET");
    private static final int MAX_RESULTS_RANDOM = 100;
    private static final int MAX_RESULTS_SCALAR = 4;

    private final Logger logger;

    private Queue<String> loadedUrls;
    @Getter
    @NonNull
    private String searchQuery = "";

    /**
     * Creates a new Discogs album cover art service.
     */
    public DiscogsAlbumCoverArtService() {
        logger = Logger.getLogger(DiscogsAlbumCoverArtService.class.getName());
        loadedUrls = new LinkedList<>();
        reloadQueueWithUrlsFromAPI();
    }

    private boolean reloadQueueWithUrlsFromAPI() {
        try {
            loadedUrls = getAlbumCoverArtUrlsFromAPI(loadedUrls);
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to get album cover art. Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Set the search query.
     *
     * @param searchQuery The search query.
     */
    public void setSearchQuery(@NonNull String searchQuery) {
        if (this.searchQuery.equals(searchQuery)) {
            return;
        }
        this.searchQuery = searchQuery;
        reloadQueueWithUrlsFromAPI();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Fetches a random album cover art from the service. Might be null if no album cover art is found.
     *
     * @return The album cover art or null if no album cover art is found.
     */
    public Image getRandomAlbumCoverArt() {
        if (loadedUrls.isEmpty()) {
            boolean response = reloadQueueWithUrlsFromAPI();
            if (!response) {
                logger.log(Level.WARNING, "Failed to reload url queue from API.");
                return null;
            }
        }
        try {
            String url = loadedUrls.poll();
            if (url == null) {
                logger.log(Level.WARNING, "Image url polled from queue is null.");
                return null;
            }
            return ImageIO.read(new URL(url));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to get album cover art. Error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Gets the album cover art URLs for the search query.
     *
     * @param maxResults The maximum number of results to fetch.
     * @return The album cover art URLs.
     */
    public List<String> getAlbumCoverArtUrls(int maxResults) {
        List<String> imageUrls = new ArrayList<>();
        while (imageUrls.size() < maxResults) {
            if (loadedUrls.isEmpty()) {
                boolean response = reloadQueueWithUrlsFromAPI();
                if (!response) {
                    logger.log(Level.WARNING, "Failed to reload url queue from API.");
                    break;
                }
            }
            String url = loadedUrls.poll();
            if (url == null) {
                logger.log(Level.WARNING, "Image url polled from queue is null.");
                break;
            }
            imageUrls.add(url);
        }
        return imageUrls;
    }

    /**
     * Gets the album cover art URLs for the search query.
     *
     * @param oldUrls The old URLs which will be returned if the new URLs cannot be fetched.
     * @return The new album cover art URLs if there is no exception, otherwise the old URLs.
     */
    private Queue<String> getAlbumCoverArtUrlsFromAPI(Queue<String> oldUrls) throws Exception {
        Queue<String> newUrls = new LinkedList<>();
        HttpURLConnection connection = getConnection(searchQuery);
        InputStream inputStream = connection.getInputStream();
        try (inputStream) {
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            String jsonResponse = scanner.hasNext() ? scanner.next() : "";

            JSONObject jsonObject = new JSONObject(jsonResponse);
            List<Object> releaseObjs = jsonObject.getJSONArray("results").toList();
            Collections.shuffle(releaseObjs);
            logger.log(Level.INFO, "Found " + releaseObjs.size() + " results.");

            for (int i = 0; i < Math.min(MAX_RESULTS_RANDOM, releaseObjs.size()); i++) {
                Object releaseObj = releaseObjs.get(i);
                try {
                    // Suppressing unchecked cast warning because the JSON object is cast to a map.
                    @SuppressWarnings("unchecked") Map<String, Object> release = (Map<String, Object>) releaseObj;

                    String imageUrl = (String) release.get("cover_image");
                    if (imageUrl != null && !imageUrl.isBlank()) {
                        newUrls.add(imageUrl);
                    }

                    if (newUrls.size() >= MAX_RESULTS_RANDOM) {
                        break;
                    }
                } catch (ClassCastException e) {
                    logger.log(Level.WARNING, "Invalid release object: " + e.getMessage());
                }
            }

            logger.log(Level.INFO, "Found " + newUrls.size() + " image URLs.");
            return newUrls;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to get album cover art. Error: " + e.getMessage());
            return oldUrls;
        } finally {
            connection.disconnect();
        }
    }

    @Override
    public List<Image> getAlbumCoverArt(int maxResults) {
        List<String> urls = getAlbumCoverArtUrls(maxResults);
        List<Image> images = new ArrayList<>();
        for (String url : urls) {
            try {
                images.add(ImageIO.read(new URL(url)));
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to get album cover art. Error: " + e.getMessage());
            }
        }
        return images;
    }

    /**
     * Get the input stream for the search query.
     *
     * @param searchQuery The search query.
     * @return The input stream.
     * @throws IOException If an I/O error occurs.
     */
    private static HttpURLConnection getConnection(String searchQuery) throws Exception {
        searchQuery = searchQuery.replaceAll("\\s+", "");

        String query = "q=" + searchQuery + "&per_page=" + (DiscogsAlbumCoverArtService.MAX_RESULTS_RANDOM * MAX_RESULTS_SCALAR) + "&key=" + API_KEY + "&secret=" + API_SECRET;
        String urlString = BASE_URL + "?" + query;
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "InteracTunesScreenSaver/1.0");

        return connection;
    }
}
