package io.github.bigjackcn.musicreview.mapper;

import io.github.bigjackcn.musicreview.entity.Album;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AlbumMapper {
    List<Album> findAll();
}