package com.matchteam.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 添加用户技能标签请求
 */
@Data
public class UserTagRequest {

    /** 标签ID */
    @NotNull(message = "标签ID不能为空")
    private Long tagId;

    /** 熟练度：1=了解, 2=掌握, 3=精通 */
    @NotNull(message = "熟练度不能为空")
    @Min(value = 1, message = "熟练度最小为1")
    @Max(value = 3, message = "熟练度最大为3")
    private Integer proficiency;
}
