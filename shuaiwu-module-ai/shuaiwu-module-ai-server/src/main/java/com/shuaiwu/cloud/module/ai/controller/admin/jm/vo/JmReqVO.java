package com.shuaiwu.cloud.module.ai.controller.admin.jm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema(description = "管理后台 - 即梦AI Request VO")
@Data
@ToString
public class JmReqVO {
    @Schema(description = "服务标识", requiredMode = Schema.RequiredMode.REQUIRED)
    private String  reqKey;

    @Schema(description = "prompt", requiredMode = Schema.RequiredMode.REQUIRED)
    private String  prompt;
}
