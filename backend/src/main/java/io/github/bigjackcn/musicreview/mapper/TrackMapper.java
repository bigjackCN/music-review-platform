package io.github.bigjackcn.musicreview.mapper;

import io.github.bigjackcn.musicreview.entity.Track;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TrackMapper {

    List<Track> findByAlbumId(@Param("albumId") Long albumId);
}
