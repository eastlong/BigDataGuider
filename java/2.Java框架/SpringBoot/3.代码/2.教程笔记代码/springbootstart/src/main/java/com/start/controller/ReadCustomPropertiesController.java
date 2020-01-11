package com.start.controller;

import com.start.domain.JdbcBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by 追梦1819 on 2019-05-06.
 */
@RestController
public class ReadCustomPropertiesController {
    @Autowired
    private JdbcBean jdbcBean;

    @GetMapping("/getJdbcBean")
    private String getJdbcBean(){
        return jdbcBean.toString();
    }
}
