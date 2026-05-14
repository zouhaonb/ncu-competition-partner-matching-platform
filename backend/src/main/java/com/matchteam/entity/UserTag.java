package com.matchteam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户-标签关联实体类
 */
@Data
@TableName("user_tag")
public class UserTag {

    /** 关联ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 标签ID */
    private Long tagId;

    /** 熟练度：1=了解, 2=掌握, 3=精通 */
    private Integer proficiency;
}
