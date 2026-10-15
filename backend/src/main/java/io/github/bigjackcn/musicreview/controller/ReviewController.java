package io.github.bigjackcn.musicreview.controller;

import io.github.bigjackcn.musicreview.dto.ReviewRequest;
import io.github.bigjackcn.musicreview.entity.Review;
import io.github.bigjackcn.musicreview.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/api/albums/{albumId}/reviews")
    public List<Review> getReviews(@PathVariable Long albumId) {
        return reviewService.getReviewsByAlbum(albumId);
    }

    @PostMapping("/api/albums/{albumId}/reviews")
    @ResponseStatus(HttpStatus.OK)
    public Review submitReview(@PathVariable Long albumId,
                               @RequestBody ReviewRequest request,
                               Authentication authentication) {
        String username = authentication.getName();
        return reviewService.submitReview(albumId, username, request.getRating(), request.getComment());
    }

    @DeleteMapping("/api/reviews/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable Long id, Authentication authentication) {
        reviewService.deleteReview(id, authentication.getName());
    }
}