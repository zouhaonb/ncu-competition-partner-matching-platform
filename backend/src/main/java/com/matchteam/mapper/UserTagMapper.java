package com.matchteam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.matchteam.entity.UserTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户标签关联数据访问层
 */
@Mapper
public interface UserTagMapper extends BaseMapper<UserTag> {

    /**
     * 根据用户ID查询用户标签（含标签名称）
     */
    @Select("SELECT ut.*, t.name as tag_name FROM user_tag ut LEFT JOIN tag t ON ut.tag_id = t.id WHERE ut.user_id = #{userId}")
    List<UserTag> selectByUserIdWithTagName(@Param("userId") Long userId);
}
