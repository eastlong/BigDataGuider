package com.blog.mybatisdemo.service;

import com.blog.mybatisdemo.entity.UserListResponse;
import com.blog.mybatisdemo.entity.UserResponse;

public interface UserService {
    UserListResponse queryUsers();
    UserResponse queryUserByName(String name);
}
