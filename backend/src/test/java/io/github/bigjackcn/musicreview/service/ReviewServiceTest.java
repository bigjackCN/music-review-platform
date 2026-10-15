package io.github.bigjackcn.musicreview.service;

import io.github.bigjackcn.musicreview.entity.Review;
import io.github.bigjackcn.musicreview.entity.User;
import io.github.bigjackcn.musicreview.mapper.ReviewMapper;
import io.github.bigjackcn.musicreview.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ReviewService reviewService;

    private User alice;

    @BeforeEach
    void setUp() {
        alice = new User();
        alice.setId(1L);
        alice.setUsername("alice");
    }

    @Test
    void submitReview_withNoExistingReview_insertsNewOne() {
        when(userMapper.findByUsername("alice")).thenReturn(alice);
        when(reviewMapper.findByUserAndAlbum(1L, 10L)).thenReturn(null);

        Review result = reviewService.submitReview(10L, "alice", 5, "Great");

        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
        verify(reviewMapper).insert(captor.capture());
        assertThat(captor.getValue().getAlbumId()).isEqualTo(10L);
        assertThat(captor.getValue().getUserId()).isEqualTo(1L);
        assertThat(captor.getValue().getRating()).isEqualTo(5);
        assertThat(result.getComment()).isEqualTo("Great");
        verify(reviewMapper, never()).update(any());
    }

    @Test
    void submitReview_withExistingReview_updatesInsteadOfInserting() {
        Review existing = new Review();
        existing.setId(7L);
        existing.setUserId(1L);
        existing.setAlbumId(10L);
        existing.setRating(2);
        when(userMapper.findByUsername("alice")).thenReturn(alice);
        when(reviewMapper.findByUserAndAlbum(1L, 10L)).thenReturn(existing);

        Review result = reviewService.submitReview(10L, "alice", 4, "Changed my mind");

        verify(reviewMapper).update(existing);
        verify(reviewMapper, never()).insert(any());
        assertThat(result.getRating()).isEqualTo(4);
        assertThat(result.getComment()).isEqualTo("Changed my mind");
    }

    @Test
    void submitReview_withRatingOutOfRange_isRejectedBeforeTouchingTheDatabase() {
        assertThatThrownBy(() -> reviewService.submitReview(10L, "alice", 6, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> reviewService.submitReview(10L, "alice", 0, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> reviewService.submitReview(10L, "alice", null, null))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(reviewMapper, userMapper);
    }

    @Test
    void deleteReview_ownReview_deletesIt() {
        Review review = new Review();
        review.setId(7L);
        review.setUserId(1L);
        when(userMapper.findByUsername("alice")).thenReturn(alice);
        when(reviewMapper.findById(7L)).thenReturn(review);

        reviewService.deleteReview(7L, "alice");

        verify(reviewMapper).deleteById(7L);
    }

    @Test
    void deleteReview_someoneElsesReview_isForbidden() {
        Review review = new Review();
        review.setId(7L);
        review.setUserId(2L);
        when(userMapper.findByUsername("alice")).thenReturn(alice);
        when(reviewMapper.findById(7L)).thenReturn(review);

        assertThatThrownBy(() -> reviewService.deleteReview(7L, "alice"))
                .isInstanceOf(SecurityException.class);

        verify(reviewMapper, never()).deleteById(any());
    }

    @Test
    void deleteReview_missingReview_throws() {
        when(userMapper.findByUsername("alice")).thenReturn(alice);
        when(reviewMapper.findById(99L)).thenReturn(null);

        assertThatThrownBy(() -> reviewService.deleteReview(99L, "alice"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }
}
