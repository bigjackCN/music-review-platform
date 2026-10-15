package io.github.bigjackcn.musicreview.mapper;

import io.github.bigjackcn.musicreview.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewMapper {
    List<Review> findByAlbumId(@Param("albumId") Long albumId);
    Review findById(@Param("id") Long id);
    Review findByUserAndAlbum(@Param("userId") Long userId, @Param("albumId") Long albumId);
    int insert(Review review);
    int update(Review review);
    int deleteById(@Param("id") Long id);
    Double getAverageRating(@Param("albumId") Long albumId);
    int getReviewCount(@Param("albumId") Long albumId);
}
