package com.matchteam.controller;

import com.matchteam.common.Result;
import com.matchteam.common.SecurityUtils;
import com.matchteam.dto.TagResponse;
import com.matchteam.dto.UserTagRequest;
import com.matchteam.dto.UserTagResponse;
import com.matchteam.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签控制器 - 处理系统标签查询和用户技能标签管理
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    /**
     * 获取所有系统标签
     * GET /api/tags
     */
    @GetMapping("/tags")
    public Result<List<TagResponse>> getAllTags() {
        return Result.success(tagService.listAllTags());
    }

    /**
     * 获取当前用户的技能标签
     * GET /api/user/tags
     */
    @GetMapping("/user/tags")
    public Result<List<UserTagResponse>> getUserTags() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(tagService.getUserTags(userId));
    }

    /**
     * 添加用户技能标签
     * POST /api/user/tags
     */
    @PostMapping("/user/tags")
    public Result<?> addUserTag(@Valid @RequestBody UserTagRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        tagService.addUserTag(userId, request);
        return Result.success("标签添加成功", null);
    }

    /**
     * 删除用户技能标签
     * DELETE /api/user/tags/{id}
     */
    @DeleteMapping("/user/tags/{id}")
    public Result<?> deleteUserTag(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        tagService.deleteUserTag(userId, id);
        return Result.success("标签删除成功", null);
    }
}
