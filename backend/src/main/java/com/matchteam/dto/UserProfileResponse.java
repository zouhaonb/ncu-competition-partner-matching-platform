package com.matchteam.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户个人信息响应
 */
@Data
public class UserProfileResponse {

    private Long id;
    private String studentId;
    private String name;
    private String phone;
    private String qq;
    private String intro;
    private LocalDateTime createTime;
}
