package com.matchteam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.matchteam.entity.Recruitment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 招募信息Mapper
 */
public interface RecruitmentMapper extends BaseMapper<Recruitment> {

    /**
     * 分页查询招募列表，支持按关键词和类别筛选
     * @param page    分页对象
     * @param keyword 搜索关键词（匹配标题）
     * @param categoryId 竞赛类别ID，为null表示不限
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT r.*, u.name AS publisher_name, cc.name AS category_name " +
            "FROM recruitment r " +
            "LEFT JOIN user u ON r.publisher_id = u.id " +
            "LEFT JOIN competition_category cc ON r.category_id = cc.id " +
            "<where>" +
            "  <if test='keyword != null and keyword != \"\"'>" +
            "    AND r.title LIKE CONCAT('%', #{keyword}, '%')" +
            "  </if>" +
            "  <if test='categoryId != null'>" +
            "    AND r.category_id = #{categoryId}" +
            "  </if>" +
            "</where>" +
            "ORDER BY r.create_time DESC" +
            "</script>")
    IPage<Recruitment> pageWithDetails(Page<Recruitment> page,
                                       @Param("keyword") String keyword,
                                       @Param("categoryId") Long categoryId);
}
