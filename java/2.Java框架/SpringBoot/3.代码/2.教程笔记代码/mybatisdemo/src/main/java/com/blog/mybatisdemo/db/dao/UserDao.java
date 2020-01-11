package com.blog.mybatisdemo.db.dao;

import com.blog.mybatisdemo.db.dto.UserDto;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserDao {
    @Select("select * from user ")
    List<UserDto> queryList();
}
