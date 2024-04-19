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
    private static final String API_KEY = DotEnvInstance.get("DISCOGS_API_KEY"); // DotEnvInstance
    private static final String API_SECRET = DotEnvInstance.get("DISCOGS_API_SECRET");
    private static final int MAX_RESULTS_RANDOM = 100;
    private static final int MAX_RESULTS_SCALAR = 4;

    private final Logger logger;
    private final Queue<String> loadedUrls;

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

    private void reloadQueueWithUrlsFromAPI() {
        loadedUrls.clear();
        getAlbumCoverArtUrlsFromAPI(loadedUrls);
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
        if (!loadedUrls.isEmpty()) {
            try {
                return ImageIO.read(new URL(loadedUrls.poll()));
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to get album cover art. Error: " + e.getMessage());
            }
        }

        List<String> albumCoverUrls = getAlbumCoverArtUrls(MAX_RESULTS_RANDOM);
        if (albumCoverUrls.isEmpty()) {
            logger.log(Level.WARNING, "No album cover art found.");
            return null;
        }

        try {
            return ImageIO.read(new URL(albumCoverUrls.get(0)));
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
                reloadQueueWithUrlsFromAPI();
            }
            imageUrls.add(loadedUrls.poll());
        }

        return imageUrls;
    }

    /**
     * Gets the album cover art URLs for the search query.
     *
     * @param imageUrls The list to add the image URLs to.
     */
    private void getAlbumCoverArtUrlsFromAPI(Collection<String> imageUrls) {
        try {
            InputStream inputStream = getInputStream(searchQuery, MAX_RESULTS_RANDOM);
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            String jsonResponse = scanner.hasNext() ? scanner.next() : "";

            JSONObject jsonObject = new JSONObject(jsonResponse);
            List<Object> releaseObjs = jsonObject.getJSONArray("results").toList();
            Collections.shuffle(releaseObjs);
            logger.log(Level.INFO, "Found " + releaseObjs.size() + " results.");

            for (int i = 0; i < Math.min(MAX_RESULTS_RANDOM, releaseObjs.size()); i++) {
                Object releaseObj = releaseObjs.get(i);
                try {
                    Map<String, Object> release = (Map<String, Object>) releaseObj;
                    String imageUrl = (String) release.get("cover_image");
                    if (imageUrl != null && !imageUrl.isBlank()) {
                        imageUrls.add(imageUrl);
                    }
                    if (imageUrls.size() >= MAX_RESULTS_RANDOM) {
                        break;
                    }
                } catch (ClassCastException e) {
                    logger.log(Level.WARNING, "Invalid release object: " + e.getMessage());
                }
            }

            logger.log(Level.INFO, "Found " + imageUrls.size() + " image URLs.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to get album cover art. Error: " + e.getMessage());
        }
    }

    @Override
    public List<Image> getAlbumCoverArt(int maxResults) {
        return getAlbumCoverArtUrls(maxResults).stream().map(url -> {
            try {
                return (Image) ImageIO.read(new URL(url));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    /**
     * Get the input stream for the search query.
     *
     * @param searchQuery The search query.
     * @return The input stream.
     * @throws IOException If an I/O error occurs.
     */
    private static InputStream getInputStream(String searchQuery, int maxResults) throws IOException {
        searchQuery = searchQuery.replaceAll("\\s+", "");

        String query = "q=" + searchQuery + "&per_page=" + (maxResults * MAX_RESULTS_SCALAR) + "&key=" + API_KEY + "&secret=" + API_SECRET;
        String urlString = BASE_URL + "?" + query;
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "InteracTunesScreenSaver/1.0");

        return connection.getInputStream();
    }
}