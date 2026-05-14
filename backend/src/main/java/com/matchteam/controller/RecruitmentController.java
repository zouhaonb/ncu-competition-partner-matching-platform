package com.matchteam.controller;

import com.matchteam.common.Result;
import com.matchteam.common.SecurityUtils;
import com.matchteam.dto.*;
import com.matchteam.service.ApplicationService;
import com.matchteam.service.MatchingService;
import com.matchteam.service.RecruitmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 招募控制器 - 处理招募信息的发布、查询、申请等功能
 */
@RestController
@RequestMapping("/api/recruitments")
@RequiredArgsConstructor
public class RecruitmentController {

    private final RecruitmentService recruitmentService;
    private final MatchingService matchingService;
    private final ApplicationService applicationService;

    /**
     * 发布招募
     * POST /api/recruitments
     */
    @PostMapping
    public Result<?> create(@Valid @RequestBody CreateRecruitmentRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        Long recruitmentId = recruitmentService.createRecruitment(userId, request);
        return Result.success("发布成功", recruitmentId);
    }

    /**
     * 分页查询招募列表（招募广场）
     * GET /api/recruitments?keyword=&categoryId=&page=1&size=12
     */
    @GetMapping
    public Result<PageResult<RecruitmentListResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size) {
        return Result.success(recruitmentService.listRecruitments(keyword, categoryId, page, size));
    }

    /**
     * 获取招募详情
     * GET /api/recruitments/{id}
     */
    @GetMapping("/{id}")
    public Result<RecruitmentDetailResponse> detail(@PathVariable Long id) {
        return Result.success(recruitmentService.getDetail(id));
    }

    /**
     * 关闭招募（仅发布者可操作）
     * PUT /api/recruitments/{id}/close
     */
    @PutMapping("/{id}/close")
    public Result<?> close(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        recruitmentService.closeRecruitment(id, userId);
        // 清除推荐缓存
        matchingService.evictCache(id);
        return Result.success("招募已关闭", null);
    }

    /**
     * 获取推荐队友列表
     * GET /api/recruitments/{id}/recommendations
     */
    @GetMapping("/{id}/recommendations")
    public Result<List<RecommendationResponse>> recommendations(@PathVariable Long id) {
        return Result.success(matchingService.getRecommendations(id));
    }

    /**
     * 申请加入招募
     * POST /api/recruitments/{id}/apply
     */
    @PostMapping("/{id}/apply")
    public Result<?> apply(@PathVariable Long id, @Valid @RequestBody ApplicationRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        applicationService.apply(id, userId, request.getReason());
        // 清除推荐缓存（人员变化）
        matchingService.evictCache(id);
        return Result.success("申请已提交", null);
    }

    /**
     * 获取招募的申请列表（仅发布者可查看）
     * GET /api/recruitments/{id}/applications
     */
    @GetMapping("/{id}/applications")
    public Result<List<ApplicationResponse>> applications(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(applicationService.getApplications(id, userId));
    }

    /**
     * 推荐匹配的招募（首页"推荐给我的招募"）
     * GET /api/recruitments/recommended
     */
    @GetMapping("/recommended")
    public Result<List<RecommendedRecruitmentResponse>> recommended() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(matchingService.getRecommendedRecruitments(userId));
    }
}
