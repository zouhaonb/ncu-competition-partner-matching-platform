package com.matchteam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.matchteam.entity.CompetitionCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 竞赛类别数据访问层
 */
@Mapper
public interface CompetitionCategoryMapper extends BaseMapper<CompetitionCategory> {
}
