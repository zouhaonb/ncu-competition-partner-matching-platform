package com.matchteam.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

/**
 * 发布招募请求
 */
@Data
public class CreateRecruitmentRequest {

    /** 招募标题 */
    @NotBlank(message = "招募标题不能为空")
    private String title;

    /** 竞赛类别ID */
    @NotNull(message = "竞赛类别不能为空")
    private Long categoryId;

    /** 需要人数 */
    @NotNull(message = "需要人数不能为空")
    @Min(value = 1, message = "需要人数至少为1")
    private Integer requiredNumber;

    /** 招募详细描述 */
    private String description;

    /** 所需技能标签ID列表 */
    private List<Long> requiredTagIds;
}
