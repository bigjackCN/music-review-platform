package io.github.bigjackcn.musicreview.mapper;

import io.github.bigjackcn.musicreview.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User findByUsername(@Param("username") String username);
    int insert(User user);
    boolean existsByUsername(@Param("username") String username);
}