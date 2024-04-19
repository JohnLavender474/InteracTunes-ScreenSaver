package org.interactunes.screensaver.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocalAlbumCoverArtServiceTest {

    private LocalAlbumCoverArtService albumCoverArtService;

    @BeforeEach
    public void setUp() {
        albumCoverArtService = new LocalAlbumCoverArtService();
    }

    @Test
    public void testGetAlbumCoverArt() {
        for (int i = 0; i < 10; i++) {
            List<Image> coverArt = albumCoverArtService.getAlbumCoverArt(5);
            assertEquals(5, coverArt.size());
        }
    }
}

