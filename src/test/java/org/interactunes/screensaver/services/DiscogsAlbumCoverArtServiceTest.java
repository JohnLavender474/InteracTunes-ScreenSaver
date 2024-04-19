package org.interactunes.screensaver.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
            List<BufferedImage> coverArt = albumCoverArtService.getAlbumCoverArt(5);
            assertEquals(5, coverArt.size());
        });
    }

}
