package io.github.bigjackcn.musicreview.dto;

import io.github.bigjackcn.musicreview.entity.Album;

public class AlbumDetailResponse {
    private Album album;
    private Double averageRating;
    private Integer reviewCount;

    public AlbumDetailResponse(Album album, Double averageRating, Integer reviewCount) {
        this.album = album;
        this.averageRating = averageRating == null ? 0.0 : Math.round(averageRating * 10) / 10.0;
        this.reviewCount = reviewCount;
    }

    public Album getAlbum() { return album; }
    public Double getAverageRating() { return averageRating; }
    public Integer getReviewCount() { return reviewCount; }
}