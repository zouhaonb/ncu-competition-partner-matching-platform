package com.matchteam.dto;

import lombok.Data;
import java.util.List;

/**
 * 推荐队友响应
 */
@Data
public class RecommendationResponse {

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String userName;

    /** 学号 */
    private String userStudentId;

    /** 匹配度百分比 0-100 */
    private Double matchScore;

    /** 匹配的标签详情 */
    private List<MatchedTag> matchTags;

    /** 匹配标签子项 */
    @Data
    public static class MatchedTag {
        private String tagName;
        private Integer proficiency; // 该用户在此标签的熟练度
    }
}
