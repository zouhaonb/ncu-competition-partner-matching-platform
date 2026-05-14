package com.matchteam.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 申请记录响应
 */
@Data
public class ApplicationResponse {

    private Long id;
    private Long recruitmentId;
    private String recruitmentTitle;
    private Long applicantId;
    private String applicantName;
    private String reason;
    private String status;    // PENDING / ACCEPTED / REJECTED
    private LocalDateTime applyTime;
    /** 联系方式（仅在被接受后可见） */
    private String applicantPhone;
    private String applicantQq;
}
