package com.taskmag.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = "com.taskmag.server")
@SpringBootApplication
public class TaskMagServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskMagServerApplication.class, args);
    }
}
