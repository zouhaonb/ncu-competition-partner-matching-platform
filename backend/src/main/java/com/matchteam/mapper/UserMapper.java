package com.matchteam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.matchteam.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问层
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
