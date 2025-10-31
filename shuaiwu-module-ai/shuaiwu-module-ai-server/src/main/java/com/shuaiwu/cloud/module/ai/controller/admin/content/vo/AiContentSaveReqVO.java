package com.shuaiwu.cloud.module.ai.controller.admin.content.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 作品管理新增/修改 Request VO")
@Data
public class AiContentSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "6794")
    private Long id;

    @Schema(description = "作品名称", example = "王五")
    private String name;

    @Schema(description = "作品类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "作品类型不能为空")
    private Integer type;

    @Schema(description = "作品内容")
    private String content;

    @Schema(description = "提示词")
    private String prompt;

    @Schema(description = "文件列表")
    private List<String> files;

    @Schema(description = "备注")
    private String remark;

}