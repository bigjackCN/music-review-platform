package io.github.bigjackcn.musicreview.service;

import io.github.bigjackcn.musicreview.entity.Album;
import io.github.bigjackcn.musicreview.mapper.AlbumMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlbumService {

    private final AlbumMapper albumMapper;

    public AlbumService(AlbumMapper albumMapper) {
        this.albumMapper = albumMapper;
    }

    public List<Album> getAllAlbums() {
        return albumMapper.findAll();
    }

    public Album getAlbumById(Long id) {
        Album album = albumMapper.findById(id);
        if (album == null) {
            throw new AlbumNotFoundException(id);
        }
        return album;
    }

    public Album createAlbum(Album album) {
        albumMapper.insert(album);
        return album;
    }

    public Album updateAlbum(Long id, Album album) {
        album.setId(id);
        int rows = albumMapper.update(album);
        if (rows == 0) {
            throw new AlbumNotFoundException(id);
        }
        return album;
    }

    public void deleteAlbum(Long id) {
        int rows = albumMapper.deleteById(id);
        if (rows == 0) {
            throw new AlbumNotFoundException(id);
        }
    }
}