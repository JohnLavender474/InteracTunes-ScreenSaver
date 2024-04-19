package org.interactunes.screensaver.services;

import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

    private final Logger logger = Logger.getLogger(LastFMAlbumCoverArtService.class.getName());

    private String searchQuery;

    @Override
    public String getAlbumCoverArt() {
        try {
            // Construct URL for album search API endpoint
            JSONObject results = getJsonObject(searchQuery);
            JSONArray albumMatches = results.getJSONObject("albummatches").getJSONArray("album");

            logger.log(Level.INFO, "Found album matches for query " + searchQuery + ": " + albumMatches.toString(2));

            for (Object obj : albumMatches) {
                try {
                    if (!(obj instanceof JSONObject albumMatch)) {
                        logger.log(Level.INFO, "Object is not JSONObject: " + obj);
                        continue;
                    }
                    JSONArray images = albumMatch.getJSONArray("image");
                    if (images == null || images.isEmpty()) {
                        logger.log(Level.INFO, "Images is null or empty");
                        continue;
                    }
                    JSONObject largestImage = images.getJSONObject(images.length() - 1);
                    String imagePath = largestImage.getString("#text");
                    if (imagePath == null || imagePath.isBlank()) {
                        logger.log(Level.INFO, "Image path is null or blank");
                        continue;
                    }
                    return imagePath;
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error while iterating JSON results. Error is caught, continuing to iterate. Error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            // TODO: create pop up with error message
            logger.log(Level.SEVERE, "Error while attempting to get album cover art: " + e.getMessage());
        }
        return null;
    }

    private static JSONObject getJsonObject(String searchQuery) throws IOException {
        searchQuery = searchQuery.replaceAll("\\s+", "");
        String apiUrl = "https://ws.audioscrobbler.com/2.0/?method=album.search&album=" + searchQuery + "&api_key=" + API_KEY + "&format=json";
        URL url = new URL(apiUrl);

        // Send HTTP request and get response
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        // Parse JSON response
        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse.getJSONObject("results");
    }
}
