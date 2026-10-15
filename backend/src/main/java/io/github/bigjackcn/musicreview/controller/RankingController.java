package io.github.bigjackcn.musicreview.controller;

import io.github.bigjackcn.musicreview.dto.AlbumSummary;
import io.github.bigjackcn.musicreview.service.AlbumService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final AlbumService albumService;

    public RankingController(AlbumService albumService) {
        this.albumService = albumService;
    }

    /** Albums ranked by positive-review rate (share of 4-5 star reviews), then by review count. */
    @GetMapping
    public List<AlbumSummary> getRanking(@RequestParam(defaultValue = "10") int limit) {
        return albumService.getRanking(Math.min(Math.max(limit, 1), 50));
    }
}
