package org.interactunes.screensaver.services;

import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

    private final Logger logger = Logger.getLogger(DiscogsAlbumCoverArtService.class.getName());

    @Setter
    private String searchQuery;

    @Override
    public String getAlbumCoverArt() {
        try {
            // Construct the URL for searching albums
            InputStream inputStream = getInputStream(searchQuery);
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            String jsonResponse = scanner.hasNext() ? scanner.next() : "";

            // Parse the JSON response
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray releasesArray = jsonObject.getJSONArray("results");

            // Extract cover image URLs
            for (int i = 0; i < releasesArray.length(); i++) {
                JSONObject release = releasesArray.getJSONObject(i);
                String coverImageUrl = release.getString("cover_image");
                if (coverImageUrl != null && !coverImageUrl.isBlank()) {
                    return coverImageUrl;
                }
            }
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
    private static InputStream getInputStream(String searchQuery) throws IOException {
        searchQuery = searchQuery.replaceAll("\\s+", "");

        String query = "q=" + searchQuery + "&type=release&key=" + API_KEY + "&secret=" + API_SECRET;
        String urlString = BASE_URL + "?" + query;
        URL url = new URL(urlString);

        // Set up the HTTP connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "InteracTunesScreenSaver/1.0");

        // Get the JSON response
        return connection.getInputStream();
    }
}