package com.matchteam.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 招募详情响应
 */
@Data
public class RecruitmentDetailResponse {

    private Long id;
    private String title;
    private Long categoryId;
    private String categoryName;
    private Integer requiredNumber;
    private String description;
    private String status;
    private LocalDateTime createTime;

    /** 发布者信息 */
    private Long publisherId;
    private String publisherName;

    /** 所需标签列表 */
    private List<TagResponse> requiredTags;
}
