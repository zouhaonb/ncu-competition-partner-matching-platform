package com.matchteam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security安全配置
 * - 使用JWT无状态认证
 * - 密码加密使用BCrypt
 * - 开放注册/登录接口，其余需要登录
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * 密码编码器 - 使用BCrypt算法加密密码
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 安全过滤器链
     * 配置接口权限和JWT过滤器
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（使用JWT无需CSRF防护）
            .csrf(csrf -> csrf.disable())
            // 无状态会话（不使用Session存储用户状态）
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 接口权限配置
            .authorizeHttpRequests(auth -> auth
                // 公开接口：注册、登录
                .requestMatchers("/api/auth/**").permitAll()
                // 竞赛类别列表（所有登录用户可读）
                .requestMatchers(HttpMethod.GET, "/api/admin/categories").authenticated()
                // 管理员接口（仅ADMIN角色可访问）
                .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                // 其余所有接口需要认证
                .anyRequest().authenticated()
            )
            // 将JWT过滤器添加到UsernamePasswordAuthenticationFilter之前
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
