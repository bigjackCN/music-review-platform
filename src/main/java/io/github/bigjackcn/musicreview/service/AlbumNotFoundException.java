package io.github.bigjackcn.musicreview.service;

public class AlbumNotFoundException extends RuntimeException {
    public AlbumNotFoundException(Long id) {
        super("Album not found with id: " + id);
    }
}
