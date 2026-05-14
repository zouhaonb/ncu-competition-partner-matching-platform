package com.matchteam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.matchteam.common.BusinessException;
import com.matchteam.dto.TagResponse;
import com.matchteam.dto.UserTagRequest;
import com.matchteam.dto.UserTagResponse;
import com.matchteam.entity.Tag;
import com.matchteam.entity.UserTag;
import com.matchteam.mapper.TagMapper;
import com.matchteam.mapper.UserTagMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 标签服务 - 管理系统标签和用户技能标签
 * 使用Redis缓存标签列表，减少数据库查询
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagMapper tagMapper;
    private final UserTagMapper userTagMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String TAG_CACHE_KEY = "tags:all";
    private static final long CACHE_TTL = 30; // 缓存30分钟

    /**
     * 获取所有系统标签
     * 优先从Redis缓存读取，缓存未命中则查数据库并写入缓存
     */
    @SuppressWarnings("unchecked")
    public List<TagResponse> listAllTags() {
        // 尝试从Redis获取缓存
        Object cached = redisTemplate.opsForValue().get(TAG_CACHE_KEY);
        if (cached != null) {
            log.debug("从Redis缓存获取标签列表");
            return (List<TagResponse>) cached;
        }

        // 缓存未命中，查询数据库
        log.debug("缓存未命中，从数据库查询标签列表");
        List<Tag> tags = tagMapper.selectList(null);
        List<TagResponse> result = tags.stream().map(tag -> {
            TagResponse resp = new TagResponse();
            resp.setId(tag.getId());
            resp.setName(tag.getName());
            return resp;
        }).collect(Collectors.toList());

        // 写入Redis缓存，TTL 30分钟
        redisTemplate.opsForValue().set(TAG_CACHE_KEY, result, CACHE_TTL, TimeUnit.MINUTES);
        return result;
    }

    /**
     * 获取当前用户的技能标签列表
     */
    public List<UserTagResponse> getUserTags(Long userId) {
        // 查询用户的所有标签关联，并关联标签表获取标签名
        LambdaQueryWrapper<UserTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTag::getUserId, userId);
        List<UserTag> userTags = userTagMapper.selectList(wrapper);

        return userTags.stream().map(ut -> {
            Tag tag = tagMapper.selectById(ut.getTagId());
            UserTagResponse resp = new UserTagResponse();
            resp.setId(ut.getId());
            resp.setTagId(ut.getTagId());
            resp.setTagName(tag != null ? tag.getName() : "未知");
            resp.setProficiency(ut.getProficiency());
            return resp;
        }).collect(Collectors.toList());
    }

    /**
     * 为用户添加技能标签
     */
    public void addUserTag(Long userId, UserTagRequest request) {
        // 检查是否已添加该标签
        LambdaQueryWrapper<UserTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTag::getUserId, userId).eq(UserTag::getTagId, request.getTagId());
        if (userTagMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该标签已添加");
        }

        UserTag userTag = new UserTag();
        userTag.setUserId(userId);
        userTag.setTagId(request.getTagId());
        userTag.setProficiency(request.getProficiency());
        userTagMapper.insert(userTag);
        log.info("用户{}添加标签: tagId={}, proficiency={}", userId, request.getTagId(), request.getProficiency());
    }

    /**
     * 删除用户的技能标签
     */
    public void deleteUserTag(Long userId, Long userTagId) {
        UserTag userTag = userTagMapper.selectById(userTagId);
        if (userTag == null || !userTag.getUserId().equals(userId)) {
            throw new BusinessException("标签不存在或无权操作");
        }
        userTagMapper.deleteById(userTagId);
    }
}
