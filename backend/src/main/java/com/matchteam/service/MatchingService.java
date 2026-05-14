package com.matchteam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.matchteam.dto.RecommendationResponse;
import com.matchteam.dto.RecommendedRecruitmentResponse;
import com.matchteam.entity.*;
import com.matchteam.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 队友匹配推荐服务 - 核心算法
 *
 * 算法说明：
 * 1. 获取招募所需的全部技能标签
 * 2. 遍历系统中所有用户（排除发布者本人）
 * 3. 对每位用户，计算其技能标签与招募要求标签的交集
 * 4. 交集中每个标签，取用户对该标签的熟练度分数（了解=1, 掌握=2, 精通=3）
 * 5. 匹配度百分比 = 用户总得分 / (要求标签数 × 3) × 100%
 * 6. 按匹配度降序排列，返回前N名
 *
 * Redis缓存策略：
 * - 缓存key: recommendations:recruitment:{recruitmentId}
 * - TTL: 30分钟，招募更新时主动清除
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingService {

    private final RecruitmentMapper recruitmentMapper;
    private final RecruitmentRequiredTagMapper requiredTagMapper;
    private final CompetitionCategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final UserTagMapper userTagMapper;
    private final TagMapper tagMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_PREFIX = "recommendations:recruitment:";
    private static final long CACHE_TTL = 30; // 分钟
    private static final int TOP_N = 5; // 返回前5名推荐队友

    /**
     * 为指定招募获取推荐队友列表
     * 优先从Redis缓存读取
     *
     * @param recruitmentId 招募ID
     * @return 推荐队友列表（Top 5，按匹配度降序）
     */
    @SuppressWarnings("unchecked")
    public List<RecommendationResponse> getRecommendations(Long recruitmentId) {
        String cacheKey = CACHE_PREFIX + recruitmentId;

        // 1. 尝试从Redis缓存获取
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.debug("从Redis缓存获取推荐结果: recruitmentId={}", recruitmentId);
            return (List<RecommendationResponse>) cached;
        }

        // 2. 缓存未命中，执行匹配算法
        log.debug("执行匹配算法: recruitmentId={}", recruitmentId);
        List<RecommendationResponse> results = computeRecommendations(recruitmentId);

        // 3. 结果写入Redis缓存
        redisTemplate.opsForValue().set(cacheKey, results, CACHE_TTL, TimeUnit.MINUTES);
        log.debug("推荐结果已缓存: recruitmentId={}, 结果数={}", recruitmentId, results.size());

        return results;
    }

    /**
     * 清除指定招募的推荐缓存
     * 当招募信息变更时调用
     */
    public void evictCache(Long recruitmentId) {
        String cacheKey = CACHE_PREFIX + recruitmentId;
        redisTemplate.delete(cacheKey);
        log.debug("已清除推荐缓存: recruitmentId={}", recruitmentId);
    }

    /**
     * 核心匹配算法实现
     *
     * 计算每位用户与招募要求的技能匹配度，返回Top N推荐
     */
    private List<RecommendationResponse> computeRecommendations(Long recruitmentId) {
        // 步骤1: 获取招募信息
        Recruitment recruitment = recruitmentMapper.selectById(recruitmentId);
        if (recruitment == null) {
            return Collections.emptyList();
        }

        // 步骤2: 获取招募所需标签ID列表
        LambdaQueryWrapper<RecruitmentRequiredTag> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.eq(RecruitmentRequiredTag::getRecruitmentId, recruitmentId);
        List<RecruitmentRequiredTag> requiredTags = requiredTagMapper.selectList(tagWrapper);
        Set<Long> requiredTagIds = requiredTags.stream()
                .map(RecruitmentRequiredTag::getTagId)
                .collect(Collectors.toSet());

        if (requiredTagIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 要求标签数量（用于计算比例）
        int requiredTagCount = requiredTagIds.size();
        // 满分基准：每个标签最高熟练度3分
        int maxPossibleScore = requiredTagCount * 3;

        // 步骤3: 遍历所有用户（排除招募发布者），计算匹配度
        List<User> allUsers = userMapper.selectList(null);
        List<UserScore> userScores = new ArrayList<>();

        for (User user : allUsers) {
            // 排除发布者本人
            if (user.getId().equals(recruitment.getPublisherId())) {
                continue;
            }

            // 步骤3.1: 获取该用户的所有技能标签及熟练度
            LambdaQueryWrapper<UserTag> userTagWrapper = new LambdaQueryWrapper<>();
            userTagWrapper.eq(UserTag::getUserId, user.getId());
            List<UserTag> userTags = userTagMapper.selectList(userTagWrapper);

            // 构建用户标签映射: tagId -> proficiency
            Map<Long, Integer> userTagMap = new HashMap<>();
            for (UserTag ut : userTags) {
                userTagMap.put(ut.getTagId(), ut.getProficiency());
            }

            // 步骤3.2: 计算交集并累加熟练度分数
            int totalScore = 0;
            List<RecommendationResponse.MatchedTag> matchedTags = new ArrayList<>();

            for (Long requiredTagId : requiredTagIds) {
                if (userTagMap.containsKey(requiredTagId)) {
                    int proficiency = userTagMap.get(requiredTagId);
                    totalScore += proficiency; // 熟练度即分数（1/2/3）

                    // 记录匹配的标签详情
                    Tag tag = tagMapper.selectById(requiredTagId);
                    RecommendationResponse.MatchedTag mt = new RecommendationResponse.MatchedTag();
                    mt.setTagName(tag != null ? tag.getName() : "未知");
                    mt.setProficiency(proficiency);
                    matchedTags.add(mt);
                }
            }

            // 步骤3.3: 计算匹配度百分比
            double matchScore = (double) totalScore / maxPossibleScore * 100.0;

            // 保留整数一位小数
            matchScore = Math.round(matchScore * 10.0) / 10.0;

            if (matchedTags.isEmpty()) {
                continue; // 没有任何标签匹配，不推荐
            }

            // 构建评分结果
            UserScore us = new UserScore();
            us.user = user;
            us.matchScore = matchScore;
            us.matchedTags = matchedTags;
            userScores.add(us);
        }

        // 步骤4: 按匹配度降序排列
        userScores.sort((a, b) -> Double.compare(b.matchScore, a.matchScore));

        // 步骤5: 取前TOP_N名，构建响应
        List<RecommendationResponse> results = new ArrayList<>();
        for (int i = 0; i < Math.min(TOP_N, userScores.size()); i++) {
            UserScore us = userScores.get(i);
            RecommendationResponse resp = new RecommendationResponse();
            resp.setUserId(us.user.getId());
            resp.setUserName(us.user.getName());
            resp.setUserStudentId(us.user.getStudentId());
            resp.setMatchScore(us.matchScore);
            resp.setMatchTags(us.matchedTags.stream().map(mt -> {
                RecommendationResponse.MatchedTag copy = new RecommendationResponse.MatchedTag();
                copy.setTagName(mt.getTagName());
                copy.setProficiency(mt.getProficiency());
                return copy;
            }).collect(Collectors.toList()));
            results.add(resp);
        }

        log.info("匹配算法完成: recruitmentId={}, 推荐{}人", recruitmentId, results.size());
        return results;
    }

    /**
     * 为指定用户推荐匹配的招募（首页"推荐给我的招募"）
     * 遍历所有OPEN招募，计算用户的技能标签与招募要求标签的匹配度
     *
     * @param userId 当前用户ID
     * @return Top 5 匹配招募，按匹配度降序
     */
    public List<RecommendedRecruitmentResponse> getRecommendedRecruitments(Long userId) {
        // 1. 获取用户技能标签
        LambdaQueryWrapper<UserTag> userTagWrapper = new LambdaQueryWrapper<>();
        userTagWrapper.eq(UserTag::getUserId, userId);
        List<UserTag> userTags = userTagMapper.selectList(userTagWrapper);
        if (userTags.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, Integer> userTagMap = new HashMap<>();
        for (UserTag ut : userTags) {
            userTagMap.put(ut.getTagId(), ut.getProficiency());
        }

        // 2. 获取所有OPEN状态的招募
        LambdaQueryWrapper<Recruitment> recWrapper = new LambdaQueryWrapper<>();
        recWrapper.eq(Recruitment::getStatus, "OPEN")
                .ne(Recruitment::getPublisherId, userId)  // 排除自己发布的
                .orderByDesc(Recruitment::getCreateTime);
        List<Recruitment> openRecruitments = recruitmentMapper.selectList(recWrapper);
        if (openRecruitments.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 对每个招募计算匹配度
        List<RecruitmentScore> scores = new ArrayList<>();
        for (Recruitment rec : openRecruitments) {
            LambdaQueryWrapper<RecruitmentRequiredTag> reqTagWrapper = new LambdaQueryWrapper<>();
            reqTagWrapper.eq(RecruitmentRequiredTag::getRecruitmentId, rec.getId());
            List<RecruitmentRequiredTag> requiredTags = requiredTagMapper.selectList(reqTagWrapper);
            if (requiredTags.isEmpty()) continue;

            int totalScore = 0;
            List<String> matchedTagNames = new ArrayList<>();
            for (RecruitmentRequiredTag rt : requiredTags) {
                if (userTagMap.containsKey(rt.getTagId())) {
                    totalScore += userTagMap.get(rt.getTagId());
                    Tag tag = tagMapper.selectById(rt.getTagId());
                    if (tag != null) matchedTagNames.add(tag.getName());
                }
            }
            if (matchedTagNames.isEmpty()) continue;

            double matchScore = (double) totalScore / (requiredTags.size() * 3) * 100.0;
            matchScore = Math.round(matchScore * 10.0) / 10.0;

            User publisher = userMapper.selectById(rec.getPublisherId());
            CompetitionCategory category = categoryMapper.selectById(rec.getCategoryId());

            RecruitmentScore rs = new RecruitmentScore();
            rs.recruitment = rec;
            rs.matchScore = matchScore;
            rs.publisherName = publisher != null ? publisher.getName() : "未知";
            rs.categoryName = category != null ? category.getName() : "未知";
            rs.matchedTagNames = matchedTagNames;
            scores.add(rs);
        }

        // 4. 按匹配度降序，取 Top 5
        scores.sort((a, b) -> Double.compare(b.matchScore, a.matchScore));

        List<RecommendedRecruitmentResponse> results = new ArrayList<>();
        for (int i = 0; i < Math.min(TOP_N, scores.size()); i++) {
            RecruitmentScore rs = scores.get(i);
            RecommendedRecruitmentResponse resp = new RecommendedRecruitmentResponse();
            resp.setRecruitmentId(rs.recruitment.getId());
            resp.setTitle(rs.recruitment.getTitle());
            resp.setCategoryName(rs.categoryName);
            resp.setPublisherName(rs.publisherName);
            resp.setMatchScore(rs.matchScore);
            resp.setCreateTime(rs.recruitment.getCreateTime());
            resp.setRequiredTagNames(rs.matchedTagNames);
            results.add(resp);
        }
        return results;
    }

    /**
     * 内部类：用户匹配得分
     */
    private static class UserScore {
        User user;
        double matchScore;
        List<RecommendationResponse.MatchedTag> matchedTags;
    }

    /**
     * 内部类：招募匹配得分
     */
    private static class RecruitmentScore {
        Recruitment recruitment;
        double matchScore;
        String publisherName;
        String categoryName;
        List<String> matchedTagNames;
    }
}
