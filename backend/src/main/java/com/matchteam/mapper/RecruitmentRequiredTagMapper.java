package com.matchteam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.matchteam.entity.RecruitmentRequiredTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 招募所需标签数据访问层
 */
@Mapper
public interface RecruitmentRequiredTagMapper extends BaseMapper<RecruitmentRequiredTag> {

    /** 根据招募ID查询所有所需标签（含标签名称） */
    @Select("SELECT rrt.*, t.name as tag_name FROM recruitment_required_tag rrt LEFT JOIN tag t ON rrt.tag_id = t.id WHERE rrt.recruitment_id = #{recruitmentId}")
    List<RecruitmentRequiredTag> selectByRecruitmentIdWithTagName(@Param("recruitmentId") Long recruitmentId);
}
