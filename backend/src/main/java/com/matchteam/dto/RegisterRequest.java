package com.matchteam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册请求
 */
@Data
public class RegisterRequest {

    /** 学号，必填 */
    @NotBlank(message = "学号不能为空")
    private String studentId;

    /** 姓名，必填 */
    @NotBlank(message = "姓名不能为空")
    private String name;

    /** 密码，至少6位 */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度不能少于6位")
    private String password;

    /** 手机号 */
    private String phone;

    /** QQ号 */
    private String qq;
}
