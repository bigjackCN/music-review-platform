package io.github.bigjackcn.musicreview.dto;

import io.github.bigjackcn.musicreview.entity.Album;
import io.github.bigjackcn.musicreview.entity.Track;

import java.util.List;

public class AlbumDetailResponse {
    private final Album album;
    private final Double averageRating;
    private final Integer reviewCount;
    private final Integer positiveRate;
    private final List<Track> tracks;

    public AlbumDetailResponse(Album album, Double averageRating, Integer reviewCount,
                               Integer positiveRate, List<Track> tracks) {
        this.album = album;
        this.averageRating = averageRating == null ? 0.0 : Math.round(averageRating * 10) / 10.0;
        this.reviewCount = reviewCount;
        this.positiveRate = positiveRate;
        this.tracks = tracks;
    }

    public Album getAlbum() { return album; }
    public Double getAverageRating() { return averageRating; }
    public Integer getReviewCount() { return reviewCount; }
    /** Percentage of reviews rated 4 or 5 stars; null when the album has no reviews. */
    public Integer getPositiveRate() { return positiveRate; }
    public List<Track> getTracks() { return tracks; }
}
