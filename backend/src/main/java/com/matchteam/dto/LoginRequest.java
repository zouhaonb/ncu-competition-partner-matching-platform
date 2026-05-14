package com.matchteam.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录请求
 */
@Data
public class LoginRequest {

    /** 学号，必填 */
    @NotBlank(message = "学号不能为空")
    private String studentId;

    /** 密码，必填 */
    @NotBlank(message = "密码不能为空")
    private String password;
}
