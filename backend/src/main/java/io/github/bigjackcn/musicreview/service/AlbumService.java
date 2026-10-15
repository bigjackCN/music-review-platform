package io.github.bigjackcn.musicreview.service;

import io.github.bigjackcn.musicreview.dto.AlbumDetailResponse;
import io.github.bigjackcn.musicreview.dto.AlbumSummary;
import io.github.bigjackcn.musicreview.entity.Album;
import io.github.bigjackcn.musicreview.mapper.AlbumMapper;
import io.github.bigjackcn.musicreview.mapper.ReviewMapper;
import io.github.bigjackcn.musicreview.mapper.TrackMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlbumService {

    private final AlbumMapper albumMapper;
    private final ReviewMapper reviewMapper;
    private final TrackMapper trackMapper;

    public AlbumService(AlbumMapper albumMapper, ReviewMapper reviewMapper, TrackMapper trackMapper) {
        this.albumMapper = albumMapper;
        this.reviewMapper = reviewMapper;
        this.trackMapper = trackMapper;
    }

    public AlbumDetailResponse getAlbumDetail(Long id) {
        Album album = getAlbumById(id); // throws AlbumNotFoundException (404) if missing
        return new AlbumDetailResponse(
                album,
                reviewMapper.getAverageRating(id),
                reviewMapper.getReviewCount(id),
                reviewMapper.getPositiveRate(id),
                trackMapper.findByAlbumId(id));
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

    public List<AlbumSummary> getAlbumStats() {
        return albumMapper.findAllWithStats();
    }

    public List<AlbumSummary> getRanking(int limit) {
        return albumMapper.findRanking(limit);
    }
}
