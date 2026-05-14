package com.matchteam;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 南昌大学竞赛队友匹配平台 - 主启动类
 */
@SpringBootApplication
@MapperScan("com.matchteam.mapper")
public class MatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatchApplication.class, args);
    }
}
