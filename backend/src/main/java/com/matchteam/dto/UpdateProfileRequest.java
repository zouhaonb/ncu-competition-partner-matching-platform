package com.matchteam.dto;

import lombok.Data;

/**
 * 更新个人信息请求
 */
@Data
public class UpdateProfileRequest {

    /** 姓名 */
    private String name;

    /** 手机号 */
    private String phone;

    /** QQ号 */
    private String qq;

    /** 个人简介 */
    private String intro;
}
