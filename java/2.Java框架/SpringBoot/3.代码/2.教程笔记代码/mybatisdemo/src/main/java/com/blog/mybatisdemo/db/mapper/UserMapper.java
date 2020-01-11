package com.blog.mybatisdemo.db.mapper;

import com.blog.mybatisdemo.db.dto.UserDto;
import org.springframework.data.repository.query.Param;

public interface UserMapper {
    UserDto queryUserByName(@Param("name") String name);
}
