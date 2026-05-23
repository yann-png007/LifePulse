package com.lifepulse;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lifepulse.mapper")
public class LifePulseApplication {
    public static void main(String[] args) {
        SpringApplication.run(LifePulseApplication.class,args);
    }
}