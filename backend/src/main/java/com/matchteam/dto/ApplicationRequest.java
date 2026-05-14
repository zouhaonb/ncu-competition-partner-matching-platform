package com.matchteam.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 申请加入招募请求
 */
@Data
public class ApplicationRequest {

    /** 申请理由 */
    @NotBlank(message = "申请理由不能为空")
    private String reason;
}
