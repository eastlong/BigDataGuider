package com.blog.mybatisdemo.db.dto;


/**
 * Created by 追梦1819 on 2019-05-05.
 */
public class UserDto {
    private Long id;
    private String name;
    private int age;
    //  set/get 省略

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
