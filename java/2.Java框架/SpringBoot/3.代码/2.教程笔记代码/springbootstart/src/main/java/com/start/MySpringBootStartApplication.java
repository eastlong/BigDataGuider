package com.start;

import com.start.domain.Student;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({Student.class})
@ConfigurationProperties(prefix = "class.student")
public class MySpringBootStartApplication {
    public static void main(String[] args) {
        SpringApplication.run(MySpringBootStartApplication.class);
    }
}
