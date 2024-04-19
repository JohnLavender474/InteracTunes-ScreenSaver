package org.interactunes.screensaver.services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class DiscogsAlbumCoverArtService implements IAlbumCoverArtService {

    private static final String BASE_URL = "https://api.discogs.com/database/search";
    private static final String API_KEY = "QgIfnvlylCdUIrWaGGjT";

    public static void main(String[] args) {
        String searchQuery = "Pink Floyd Dark Side of the Moon"; // Example search query

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
                System.out.println("Cover Image URL: " + coverImageUrl);
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    private static InputStream getInputStream(String searchQuery) throws IOException {
        String query = "q=" + searchQuery + "&type=release";
        String urlString = BASE_URL + "?" + query;
        URL url = new URL(urlString);

        // Set up the HTTP connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "YourUserAgent/1.0");
        connection.setRequestProperty("Authorization", "Discogs key=" + API_KEY);

        // Get the JSON response
        InputStream inputStream = connection.getInputStream();
        return inputStream;
    }

    @Override
    public String getAlbumCoverArt() {
        return "";
    }
}

