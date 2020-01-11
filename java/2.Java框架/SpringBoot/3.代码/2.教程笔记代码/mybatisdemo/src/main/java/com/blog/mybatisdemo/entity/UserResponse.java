package com.blog.mybatisdemo.entity;


import com.blog.mybatisdemo.db.dto.UserDto;

public class UserResponse {
    private int code;
    private String msg;
    private UserDto user;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}
