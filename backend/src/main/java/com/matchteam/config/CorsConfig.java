package com.matchteam.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置
 * 允许前端开发服务器(localhost:3000)跨域访问后端API
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")          // 匹配所有API路径
                .allowedOriginPatterns("*")      // 开发环境允许所有来源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")             // 允许所有请求头
                .allowCredentials(true)          // 允许携带Cookie
                .maxAge(3600);                   // 预检请求缓存1小时
    }
}
