package com.matchteam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 技能标签实体类
 */
@Data
@TableName("tag")
public class Tag {

    /** 标签ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 标签名称（如：Java、Python、数据分析等） */
    private String name;
}
