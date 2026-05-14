package com.matchteam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 申请实体类
 */
@Data
@TableName("application")
public class Application {

    /** 申请ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 招募ID */
    private Long recruitmentId;

    /** 申请者用户ID */
    private Long applicantId;

    /** 申请理由 */
    private String reason;

    /** 申请状态：PENDING=待审核, ACCEPTED=已通过, REJECTED=已拒绝 */
    private String status;

    /** 申请时间 */
    @TableField("apply_time")
    private LocalDateTime applyTime;
}
