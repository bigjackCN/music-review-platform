package io.github.bigjackcn.musicreview.service;

import io.github.bigjackcn.musicreview.entity.Review;
import io.github.bigjackcn.musicreview.entity.User;
import io.github.bigjackcn.musicreview.mapper.ReviewMapper;
import io.github.bigjackcn.musicreview.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewMapper reviewMapper;
    private final UserMapper userMapper;

    public ReviewService(ReviewMapper reviewMapper, UserMapper userMapper) {
        this.reviewMapper = reviewMapper;
        this.userMapper = userMapper;
    }

    public List<Review> getReviewsByAlbum(Long albumId) {
        return reviewMapper.findByAlbumId(albumId);
    }

    public Review submitReview(Long albumId, String username, Integer rating, String comment) {
        User user = userMapper.findByUsername(username);
        Review existing = reviewMapper.findByUserAndAlbum(user.getId(), albumId);

        if (existing != null) {
            existing.setRating(rating);
            existing.setComment(comment);
            reviewMapper.update(existing);
            return existing;
        } else {
            Review review = new Review();
            review.setAlbumId(albumId);
            review.setUserId(user.getId());
            review.setRating(rating);
            review.setComment(comment);
            reviewMapper.insert(review);
            return review;
        }
    }

    public void deleteReview(Long reviewId, String username) {
        User user = userMapper.findByUsername(username);
        Review review = reviewMapper.findById(reviewId);

        if (review == null) {
            throw new IllegalArgumentException("Review not found");
        }
        if (!review.getUserId().equals(user.getId())) {
            throw new SecurityException("You can only delete your own review");
        }
        reviewMapper.deleteById(reviewId);
    }
}