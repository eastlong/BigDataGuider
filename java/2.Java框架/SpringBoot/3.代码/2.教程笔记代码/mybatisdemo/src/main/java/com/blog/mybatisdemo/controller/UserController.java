package com.blog.mybatisdemo.controller;

import com.blog.mybatisdemo.entity.UserListResponse;
import com.blog.mybatisdemo.entity.UserResponse;
import com.blog.mybatisdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @ResponseBody
    @GetMapping("/queryUsers")
    public UserListResponse queryUsers(){
        return userService.queryUsers();
    }

    @ResponseBody
    @GetMapping("/queryUserByName")
    public UserResponse queryUserByName(String name){
        return userService.queryUserByName(name);
    }
}
