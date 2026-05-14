package com.matchteam.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 处理申请请求（接受或拒绝）
 */
@Data
public class HandleApplicationRequest {

    /** 处理结果：ACCEPTED=同意, REJECTED=拒绝 */
    @NotBlank(message = "处理状态不能为空")
    private String status;
}
