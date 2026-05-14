package com.matchteam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.matchteam.common.BusinessException;
import com.matchteam.dto.*;
import com.matchteam.entity.*;
import com.matchteam.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 招募服务 - 处理招募信息的发布、查询、关闭等操作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecruitmentService {

    private final RecruitmentMapper recruitmentMapper;
    private final RecruitmentRequiredTagMapper requiredTagMapper;
    private final TagMapper tagMapper;
    private final CompetitionCategoryMapper categoryMapper;
    private final UserMapper userMapper;

    /**
     * 发布新招募
     * 创建招募记录并关联所需技能标签
     */
    @Transactional
    public Long createRecruitment(Long publisherId, CreateRecruitmentRequest request) {
        // 创建招募记录
        Recruitment recruitment = new Recruitment();
        recruitment.setPublisherId(publisherId);
        recruitment.setTitle(request.getTitle());
        recruitment.setCategoryId(request.getCategoryId());
        recruitment.setRequiredNumber(request.getRequiredNumber());
        recruitment.setDescription(request.getDescription());
        recruitment.setStatus("OPEN");
        recruitmentMapper.insert(recruitment);

        // 保存所需技能标签关联
        if (request.getRequiredTagIds() != null && !request.getRequiredTagIds().isEmpty()) {
            for (Long tagId : request.getRequiredTagIds()) {
                RecruitmentRequiredTag requiredTag = new RecruitmentRequiredTag();
                requiredTag.setRecruitmentId(recruitment.getId());
                requiredTag.setTagId(tagId);
                requiredTagMapper.insert(requiredTag);
            }
        }

        log.info("用户{}发布了招募: {}", publisherId, recruitment.getTitle());
        return recruitment.getId();
    }

    /**
     * 分页查询招募列表
     * 支持按关键词搜索标题、按类别筛选
     */
    public PageResult<RecruitmentListResponse> listRecruitments(String keyword, Long categoryId, int page, int size) {
        Page<Recruitment> pageObj = new Page<>(page, size);
        IPage<Recruitment> result = recruitmentMapper.pageWithDetails(pageObj, keyword, categoryId);

        List<RecruitmentListResponse> records = result.getRecords().stream().map(rec -> {
            // 从查询结果中获取关联信息（通过额外查询）
            User publisher = userMapper.selectById(rec.getPublisherId());
            CompetitionCategory category = categoryMapper.selectById(rec.getCategoryId());

            RecruitmentListResponse resp = new RecruitmentListResponse();
            resp.setId(rec.getId());
            resp.setTitle(rec.getTitle());
            resp.setCategoryName(category != null ? category.getName() : "未知");
            resp.setRequiredNumber(rec.getRequiredNumber());
            resp.setPublisherName(publisher != null ? publisher.getName() : "未知");
            resp.setStatus(rec.getStatus());
            resp.setCreateTime(rec.getCreateTime());
            return resp;
        }).collect(Collectors.toList());

        return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    /**
     * 获取招募详情
     * 包含发布者信息和所需标签列表
     */
    public RecruitmentDetailResponse getDetail(Long recruitmentId) {
        Recruitment recruitment = recruitmentMapper.selectById(recruitmentId);
        if (recruitment == null) {
            throw new BusinessException("招募信息不存在");
        }

        User publisher = userMapper.selectById(recruitment.getPublisherId());
        CompetitionCategory category = categoryMapper.selectById(recruitment.getCategoryId());

        RecruitmentDetailResponse resp = new RecruitmentDetailResponse();
        resp.setId(recruitment.getId());
        resp.setTitle(recruitment.getTitle());
        resp.setCategoryId(recruitment.getCategoryId());
        resp.setCategoryName(category != null ? category.getName() : "未知");
        resp.setRequiredNumber(recruitment.getRequiredNumber());
        resp.setDescription(recruitment.getDescription());
        resp.setStatus(recruitment.getStatus());
        resp.setCreateTime(recruitment.getCreateTime());
        resp.setPublisherId(recruitment.getPublisherId());
        resp.setPublisherName(publisher != null ? publisher.getName() : "未知");

        // 查询所需标签
        LambdaQueryWrapper<RecruitmentRequiredTag> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.eq(RecruitmentRequiredTag::getRecruitmentId, recruitmentId);
        List<RecruitmentRequiredTag> requiredTags = requiredTagMapper.selectList(tagWrapper);

        List<TagResponse> tagResponses = requiredTags.stream().map(rt -> {
            Tag tag = tagMapper.selectById(rt.getTagId());
            TagResponse tr = new TagResponse();
            tr.setId(tag.getId());
            tr.setName(tag.getName());
            return tr;
        }).collect(Collectors.toList());
        resp.setRequiredTags(tagResponses);

        return resp;
    }

    /**
     * 关闭招募（仅发布者可操作）
     */
    public void closeRecruitment(Long recruitmentId, Long userId) {
        Recruitment recruitment = recruitmentMapper.selectById(recruitmentId);
        if (recruitment == null) {
            throw new BusinessException("招募信息不存在");
        }
        if (!recruitment.getPublisherId().equals(userId)) {
            throw new BusinessException("只有发布者才能关闭招募");
        }
        if ("CLOSED".equals(recruitment.getStatus())) {
            throw new BusinessException("招募已关闭");
        }

        recruitment.setStatus("CLOSED");
        recruitmentMapper.updateById(recruitment);
        log.info("招募{}已关闭", recruitmentId);
    }

    /**
     * 获取当前用户发布的所有招募
     */
    public List<RecruitmentListResponse> getUserRecruitments(Long userId) {
        LambdaQueryWrapper<Recruitment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Recruitment::getPublisherId, userId).orderByDesc(Recruitment::getCreateTime);
        List<Recruitment> recruitments = recruitmentMapper.selectList(wrapper);

        return recruitments.stream().map(rec -> {
            CompetitionCategory category = categoryMapper.selectById(rec.getCategoryId());
            RecruitmentListResponse resp = new RecruitmentListResponse();
            resp.setId(rec.getId());
            resp.setTitle(rec.getTitle());
            resp.setCategoryName(category != null ? category.getName() : "未知");
            resp.setRequiredNumber(rec.getRequiredNumber());
            resp.setStatus(rec.getStatus());
            resp.setCreateTime(rec.getCreateTime());
            return resp;
        }).collect(Collectors.toList());
    }
}
