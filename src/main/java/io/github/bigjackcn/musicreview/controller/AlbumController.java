package io.github.bigjackcn.musicreview.controller;

import io.github.bigjackcn.musicreview.entity.Album;
import io.github.bigjackcn.musicreview.service.AlbumService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    public List<Album> getAllAlbums() {
        return albumService.getAllAlbums();
    }

    @GetMapping("/{id}")
    public Album getAlbumById(@PathVariable Long id) {
        return albumService.getAlbumById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Album createAlbum(@RequestBody Album album) {
        return albumService.createAlbum(album);
    }

    @PutMapping("/{id}")
    public Album updateAlbum(@PathVariable Long id, @RequestBody Album album) {
        return albumService.updateAlbum(id, album);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlbum(@PathVariable Long id) {
        albumService.deleteAlbum(id);
    }
}
