package org.interactunes.screensaver.services;

import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The service for fetching album cover art via the LastFM API. Sadly, the API is kind of disappointing as a lot
 * of queries (even those that one would think to be very popular) cannot return an image url. As such , the
 * {@link DiscogsAlbumCoverArtService} class is preferable.
 */
@Setter
public class LastFMAlbumCoverArtService implements IAlbumCoverArtService {

    private static final String API_KEY = "19349375ee9a97891c4741b022c8bc5b";
    private static final int MAX_RESULTS_RANDOM = 100;

    private final Logger logger = Logger.getLogger(LastFMAlbumCoverArtService.class.getName());

    private String searchQuery;

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
            JSONObject results = getJsonObject(searchQuery);
            if (results == null) {
                logger.log(Level.INFO, "Results are null");
                return new ArrayList<>();
            }

            List<Object> albumMatches = results.getJSONObject("albummatches").getJSONArray("album").toList();
            Collections.shuffle(albumMatches);

            List<Image> images = new ArrayList<>();

            for (int i = 0; i < Math.min(maxResults, albumMatches.size()); i++) {
                try {
                    Object obj = albumMatches.get(i);
                    if (!(obj instanceof JSONObject albumMatch)) {
                        logger.log(Level.INFO, "Object is not JSONObject: " + obj);
                        continue;
                    }

                    JSONArray imageJSONArray = albumMatch.getJSONArray("image");
                    if (imageJSONArray == null || imageJSONArray.isEmpty()) {
                        logger.log(Level.INFO, "Images is null or empty");
                        continue;
                    }
                    JSONObject largestImage = imageJSONArray.getJSONObject(imageJSONArray.length() - 1);
                    String imagePath = largestImage.getString("#text");
                    if (imagePath == null || imagePath.isBlank()) {
                        logger.log(Level.INFO, "Image path is null or blank");
                        continue;
                    }

                    Image image = ImageIO.read(new URL(imagePath));
                    images.add(image);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error while iterating JSON results. Error is caught, continuing to iterate. Error: " + e.getMessage());
                }
            }

            return images;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while attempting to get album cover art: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    private static JSONObject getJsonObject(String searchQuery) throws IOException {
        searchQuery = searchQuery.replaceAll("\\s+", "");
        String apiUrl = "https://ws.audioscrobbler.com/2.0/?method=album.search&album=" + searchQuery + "&api_key=" + API_KEY + "&format=json";
        URL url = new URL(apiUrl);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse.getJSONObject("results");
    }
}
