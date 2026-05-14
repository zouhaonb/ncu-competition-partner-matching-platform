package com.matchteam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 招募信息实体类
 */
@Data
@TableName("recruitment")
public class Recruitment {

    /** 招募ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 发布者用户ID */
    private Long publisherId;

    /** 招募标题 */
    private String title;

    /** 竞赛类别ID */
    private Long categoryId;

    /** 需要招募的人数 */
    private Integer requiredNumber;

    /** 招募详细描述 */
    private String description;

    /** 招募状态：OPEN=开放中, CLOSED=已关闭 */
    private String status;

    /** 发布时间 */
    @TableField("create_time")
    private LocalDateTime createTime;
}
