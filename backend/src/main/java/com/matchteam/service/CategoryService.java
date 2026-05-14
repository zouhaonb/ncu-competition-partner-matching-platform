package com.matchteam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.matchteam.common.BusinessException;
import com.matchteam.entity.CompetitionCategory;
import com.matchteam.mapper.CompetitionCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 竞赛类别服务
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CompetitionCategoryMapper categoryMapper;

    /**
     * 获取所有竞赛类别
     */
    public List<CompetitionCategory> listAll() {
        return categoryMapper.selectList(null);
    }

    /**
     * 添加竞赛类别（管理员功能）
     */
    public void addCategory(String name) {
        LambdaQueryWrapper<CompetitionCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CompetitionCategory::getName, name);
        if (categoryMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该类别已存在");
        }
        CompetitionCategory category = new CompetitionCategory();
        category.setName(name);
        categoryMapper.insert(category);
    }

    /**
     * 删除竞赛类别（管理员功能）
     */
    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
    }
}
