package io.github.bigjackcn.musicreview.entity;

public class Video {

    private Long id;
    private Long albumId;
    private String title;
    private String youtubeId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAlbumId() { return albumId; }
    public void setAlbumId(Long albumId) { this.albumId = albumId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getYoutubeId() { return youtubeId; }
    public void setYoutubeId(String youtubeId) { this.youtubeId = youtubeId; }
}
