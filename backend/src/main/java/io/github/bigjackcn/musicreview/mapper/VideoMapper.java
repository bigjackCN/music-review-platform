package io.github.bigjackcn.musicreview.mapper;

import io.github.bigjackcn.musicreview.entity.Video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VideoMapper {

    List<Video> findByAlbumId(@Param("albumId") Long albumId);
}
