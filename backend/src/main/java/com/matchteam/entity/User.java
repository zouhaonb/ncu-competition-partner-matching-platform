package com.matchteam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("user")
public class User {

    /** 用户ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 学号，用于登录的唯一标识 */
    private String studentId;

    /** 姓名 */
    private String name;

    /** 密码（BCrypt加密存储） */
    private String password;

    /** 手机号 */
    private String phone;

    /** QQ号 */
    private String qq;

    /** 个人简介 */
    private String intro;

    /** 角色：USER=普通用户, ADMIN=管理员 */
    private String role;

    /** 注册时间 */
    @TableField("create_time")
    private LocalDateTime createTime;
}
