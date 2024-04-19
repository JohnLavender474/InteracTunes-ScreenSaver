package org.interactunes.screensaver.services;

import lombok.NonNull;
import lombok.Setter;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The service for fetching album cover art via the Discogs API.
 */
public class DiscogsAlbumCoverArtService implements IAlbumCoverArtService {

    private static final String BASE_URL = "https://api.discogs.com/database/search";
    private static final String API_KEY = "QgIfnvlylCdUIrWaGGjT";
    private static final String API_SECRET = "TvDQdJIxisuvdquJDPGlvFWTcrunoYen";
    private static final int MAX_RESULTS_RANDOM = 100;
    private static final int MAX_RESULTS_SCALAR = 4;

    private final Logger logger = Logger.getLogger(DiscogsAlbumCoverArtService.class.getName());

    @Setter
    @NonNull
    private String searchQuery = "";

    /**
     * {@inheritDoc}
     * <p>
     * Fetches a random album cover art from the service. Might be null if no album cover art is found.
     *
     * @return The album cover art or null if no album cover art is found.
     */
    public Image getRandomAlbumCoverArt() {
        List<Image> albumCoverArt = getAlbumCoverArt(MAX_RESULTS_RANDOM);
        return albumCoverArt.isEmpty() ? null : albumCoverArt.get(0);
    }

    @Override
    public List<Image> getAlbumCoverArt(int maxResults) {
        try {
            InputStream inputStream = getInputStream(searchQuery, maxResults);
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            String jsonResponse = scanner.hasNext() ? scanner.next() : "";

            JSONObject jsonObject = new JSONObject(jsonResponse);
            List<Object> releaseObjs = jsonObject.getJSONArray("results").toList();
            Collections.shuffle(releaseObjs);

            List<Image> images = new ArrayList<>();

            for (int i = 0; i < Math.min(maxResults, releaseObjs.size()); i++) {
                Object releaseObj = releaseObjs.get(i);
                if (!(releaseObj instanceof JSONObject release)) {
                    continue;
                }
                String coverImageUrl = release.getString("cover_image");
                if (coverImageUrl != null && !coverImageUrl.isBlank()) {
                    Image image = ImageIO.read(new URL(coverImageUrl));
                    images.add(image);
                }
            }

            return images;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to get album cover art. Error: " + e.getMessage());
        }
        return null;
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

        String query = "q=" + searchQuery + "&per_page=" + (maxResults * MAX_RESULTS_SCALAR) + "&type=release&key=" + API_KEY + "&secret=" + API_SECRET;
        String urlString = BASE_URL + "?" + query;
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "InteracTunesScreenSaver/1.0");

        return connection.getInputStream();
    }
}