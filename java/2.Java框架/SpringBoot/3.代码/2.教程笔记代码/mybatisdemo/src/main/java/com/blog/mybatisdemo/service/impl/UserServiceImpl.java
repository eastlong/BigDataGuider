package com.blog.mybatisdemo.service.impl;

import com.blog.mybatisdemo.db.dao.UserDao;
import com.blog.mybatisdemo.db.dto.UserDto;
import com.blog.mybatisdemo.db.mapper.UserMapper;
import com.blog.mybatisdemo.entity.UserListResponse;
import com.blog.mybatisdemo.entity.UserResponse;
import com.blog.mybatisdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserListResponse queryUsers() {
        List<UserDto> userDtos = userDao.queryList();
        UserListResponse response = new UserListResponse();
        response.setUsers(userDtos);
        response.setCode(0);
        response.setMsg("success");
        return response;
    }

    @Override
    public UserResponse queryUserByName(String name){
        UserDto userDto = userMapper.queryUserByName(name);
        UserResponse response = new UserResponse();
        response.setUser(userDto);
        response.setCode(0);
        response.setMsg("success");
        return response;
    }
}

