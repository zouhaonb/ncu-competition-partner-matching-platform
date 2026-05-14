package com.matchteam.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 招募列表项响应（广场展示用）
 */
@Data
public class RecruitmentListResponse {

    private Long id;
    private String title;
    private String categoryName;
    private Integer requiredNumber;
    private String publisherName;
    private String status;    // OPEN / CLOSED
    private LocalDateTime createTime;
}
