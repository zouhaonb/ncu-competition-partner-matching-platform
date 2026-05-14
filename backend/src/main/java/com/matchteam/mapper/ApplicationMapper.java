package com.matchteam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.matchteam.entity.Application;
import org.apache.ibatis.annotations.Mapper;

/**
 * 申请数据访问层
 */
@Mapper
public interface ApplicationMapper extends BaseMapper<Application> {
}
