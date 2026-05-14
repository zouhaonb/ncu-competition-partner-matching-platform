package com.matchteam.controller;

import com.matchteam.common.Result;
import com.matchteam.common.SecurityUtils;
import com.matchteam.dto.HandleApplicationRequest;
import com.matchteam.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 申请控制器 - 处理申请审核操作
 */
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    /**
     * 处理申请（接受或拒绝）
     * PUT /api/applications/{id}/handle
     */
    @PutMapping("/{id}/handle")
    public Result<?> handle(@PathVariable Long id, @Valid @RequestBody HandleApplicationRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        applicationService.handleApplication(id, userId, request.getStatus());
        return Result.success(request.getStatus().equals("ACCEPTED") ? "已同意申请" : "已拒绝申请", null);
    }
}
