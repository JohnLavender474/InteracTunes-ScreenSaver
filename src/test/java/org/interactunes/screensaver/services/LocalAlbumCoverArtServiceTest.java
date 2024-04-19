package org.interactunes.screensaver.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LocalAlbumCoverArtServiceTest {

    private LocalAlbumCoverArtService albumCoverArtService;

    @BeforeEach
    public void setUp() {
        albumCoverArtService = new LocalAlbumCoverArtService();
    }

    @Test
    public void testGetAlbumCoverArt() {
        for (int i = 0; i < 10; i++) {
            String coverArtPath = albumCoverArtService.getAlbumCoverArt();
            System.out.println("Local Album Cover Art Path: " + coverArtPath);
            assertNotNull(coverArtPath);
        }
    }
}

