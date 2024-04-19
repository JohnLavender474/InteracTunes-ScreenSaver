package org.interactunes.screensaver.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DiscogsAlbumCoverArtServiceTest {

    private DiscogsAlbumCoverArtService albumCoverArtService;

    @BeforeEach
    public void setUp() {
        albumCoverArtService = new DiscogsAlbumCoverArtService();
    }

    @Test
    public void testGetAlbumCoverArt() {
        List<String> queries = List.of("PinkFloyd", "LedZeppelin", "Rush", "TheBeatles");
        queries.forEach(query -> {
            albumCoverArtService.setSearchQuery(query);
            String coverArtPath = albumCoverArtService.getAlbumCoverArt();
            System.out.println("Local Album Cover Art Path: " + coverArtPath);
            assertNotNull(coverArtPath);
        });
    }

}
