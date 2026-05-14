package com.matchteam.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.matchteam.common.BusinessException;
import com.matchteam.common.Result;
import com.matchteam.common.SecurityUtils;
import com.matchteam.entity.CompetitionCategory;
import com.matchteam.entity.Recruitment;
import com.matchteam.entity.User;
import com.matchteam.mapper.CompetitionCategoryMapper;
import com.matchteam.mapper.RecruitmentMapper;
import com.matchteam.mapper.UserMapper;
import com.matchteam.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员控制器 - 竞赛类别/用户/招募管理
 * 接口受Spring Security保护，仅ADMIN角色可访问
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CategoryService categoryService;
    private final CompetitionCategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final RecruitmentMapper recruitmentMapper;

    /** 获取所有竞赛类别 */
    @GetMapping("/categories")
    public Result<List<CompetitionCategory>> listCategories() {
        return Result.success(categoryService.listAll());
    }

    /** 添加竞赛类别 */
    @PostMapping("/categories")
    public Result<?> addCategory(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        categoryService.addCategory(name);
        return Result.success("添加成功", null);
    }

    /** 删除竞赛类别 */
    @DeleteMapping("/categories/{id}")
    public Result<?> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success("删除成功", null);
    }

    /** 查看所有用户 */
    @GetMapping("/users")
    public Result<List<User>> listUsers() {
        List<User> users = userMapper.selectList(null);
        users.forEach(u -> u.setPassword(null));
        return Result.success(users);
    }

    /** 删除用户（不能删除自己和admin） */
    @DeleteMapping("/users/{id}")
    public Result<?> deleteUser(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (id.equals(currentUserId)) {
            throw new BusinessException("不能删除自己");
        }
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if ("ADMIN".equals(user.getRole())) {
            throw new BusinessException("不能删除管理员账号");
        }
        userMapper.deleteById(id);
        return Result.success("删除成功", null);
    }

    /** 查看所有招募 */
    @GetMapping("/recruitments")
    public Result<List<Recruitment>> listRecruitments() {
        return Result.success(recruitmentMapper.selectList(null));
    }

    /** 关闭招募 */
    @PutMapping("/recruitments/{id}/close")
    public Result<?> closeRecruitment(@PathVariable Long id) {
        Recruitment r = recruitmentMapper.selectById(id);
        if (r == null) {
            throw new BusinessException("招募不存在");
        }
        r.setStatus("CLOSED");
        recruitmentMapper.updateById(r);
        return Result.success("已关闭", null);
    }

    /** 删除招募 */
    @DeleteMapping("/recruitments/{id}")
    public Result<?> deleteRecruitment(@PathVariable Long id) {
        recruitmentMapper.deleteById(id);
        return Result.success("删除成功", null);
    }
}
