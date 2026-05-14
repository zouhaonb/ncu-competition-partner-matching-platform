package com.matchteam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 招募-所需标签关联实体类
 */
@Data
@TableName("recruitment_required_tag")
public class RecruitmentRequiredTag {

    /** 关联ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 招募ID */
    private Long recruitmentId;

    /** 所需标签ID */
    private Long tagId;
}
