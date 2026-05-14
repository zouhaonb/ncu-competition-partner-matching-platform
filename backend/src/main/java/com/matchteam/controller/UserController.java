package com.matchteam.controller;

import com.matchteam.common.Result;
import com.matchteam.common.SecurityUtils;
import com.matchteam.dto.*;
import com.matchteam.service.ApplicationService;
import com.matchteam.service.RecruitmentService;
import com.matchteam.service.TeammateService;
import com.matchteam.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器 - 处理个人信息、我的招募、我的申请、我的队友
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RecruitmentService recruitmentService;
    private final ApplicationService applicationService;
    private final TeammateService teammateService;

    /**
     * 获取当前用户的个人信息
     * GET /api/user/me
     */
    @GetMapping("/me")
    public Result<UserProfileResponse> getProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(userService.getProfile(userId));
    }

    /**
     * 更新个人信息
     * PUT /api/user/me
     */
    @PutMapping("/me")
    public Result<?> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        userService.updateProfile(userId, request);
        return Result.success("更新成功", null);
    }

    /**
     * 获取我的招募列表
     * GET /api/user/recruitments
     */
    @GetMapping("/recruitments")
    public Result<List<RecruitmentListResponse>> getMyRecruitments() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(recruitmentService.getUserRecruitments(userId));
    }

    /**
     * 获取我的申请记录
     * GET /api/user/applications
     */
    @GetMapping("/applications")
    public Result<List<ApplicationResponse>> getMyApplications() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(applicationService.getUserApplications(userId));
    }

    /**
     * 获取我的队友列表
     * GET /api/user/teammates
     */
    @GetMapping("/teammates")
    public Result<List<UserProfileResponse>> getMyTeammates() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(teammateService.getTeammates(userId));
    }
}
