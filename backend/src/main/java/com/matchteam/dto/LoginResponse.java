package com.matchteam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录成功响应
 */
@Data
@AllArgsConstructor
public class LoginResponse {

    /** JWT令牌 */
    private String token;

    /** 用户ID */
    private Long userId;

    /** 学号 */
    private String studentId;

    /** 姓名 */
    private String name;

    /** 角色：USER / ADMIN */
    private String role;
}
