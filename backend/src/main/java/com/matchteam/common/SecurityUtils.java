package com.matchteam.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Spring Security工具类
 * 从SecurityContext中提取当前登录用户的信息
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户的ID
     * JwtAuthenticationFilter在验证token后会将userId存入Authentication的principal
     * @return 用户ID
     * @throws RuntimeException 如果用户未登录
     */
    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof String) {
            return Long.valueOf((String) auth.getPrincipal());
        }
        throw new BusinessException(401, "用户未登录或登录已过期");
    }
}
