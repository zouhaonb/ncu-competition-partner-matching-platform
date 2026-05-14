package com.matchteam.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 推荐招募响应 — 首页"推荐给我的招募"
 */
@Data
public class RecommendedRecruitmentResponse {

    private Long recruitmentId;
    private String title;
    private String categoryName;
    private String publisherName;
    private Double matchScore;
    private LocalDateTime createTime;
    private List<String> requiredTagNames;
}
