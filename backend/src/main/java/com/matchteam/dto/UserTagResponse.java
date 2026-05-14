package com.matchteam.dto;

import lombok.Data;

/**
 * 用户标签响应（含标签名称和熟练度）
 */
@Data
public class UserTagResponse {

    private Long id;
    private Long tagId;
    private String tagName;
    private Integer proficiency; // 1=了解, 2=掌握, 3=精通
}
