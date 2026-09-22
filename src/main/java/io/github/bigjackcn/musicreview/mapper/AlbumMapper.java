package io.github.bigjackcn.musicreview.mapper;

import io.github.bigjackcn.musicreview.entity.Album;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AlbumMapper {

    List<Album> findAll();

    Album findById(@Param("id") Long id);

    int insert(Album album);

    int update(Album album);

    int deleteById(@Param("id") Long id);
}