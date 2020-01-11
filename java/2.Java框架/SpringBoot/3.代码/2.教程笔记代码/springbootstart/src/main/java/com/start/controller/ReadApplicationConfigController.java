package com.start.controller;

import com.start.domain.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by wxl
 */
@Controller
public class ReadApplicationConfigController {

    @Autowired
    private Student student;

    @ResponseBody
    @RequestMapping("/getBeanProperties")
    public String getProperties(){
        return "学生姓名是："+student.getName()+",学生年龄是："
                +student.getAge()+",学生分数是："
                +student.getGrade();
    }
}
