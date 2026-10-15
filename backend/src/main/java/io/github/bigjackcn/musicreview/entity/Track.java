package io.github.bigjackcn.musicreview.entity;

public class Track {

    private Long id;
    private Long albumId;
    private Integer trackNumber;
    private String title;
    private Integer durationMs;
    private String previewUrl;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAlbumId() { return albumId; }
    public void setAlbumId(Long albumId) { this.albumId = albumId; }

    public Integer getTrackNumber() { return trackNumber; }
    public void setTrackNumber(Integer trackNumber) { this.trackNumber = trackNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getDurationMs() { return durationMs; }
    public void setDurationMs(Integer durationMs) { this.durationMs = durationMs; }

    public String getPreviewUrl() { return previewUrl; }
    public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }
}
