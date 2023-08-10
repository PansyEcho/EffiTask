package com.shi.effitask;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.shi.effitask.dao")
public class EffiTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(EffiTaskApplication.class, args);
    }

}
