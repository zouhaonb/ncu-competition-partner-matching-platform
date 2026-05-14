package com.matchteam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 竞赛类别实体类
 */
@Data
@TableName("competition_category")
public class CompetitionCategory {

    /** 类别ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 类别名称（如：数学建模、创新创业等） */
    private String name;
}
