package com.shuaiwu.cloud.module.ai.controller.admin.jm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 即梦AI搜索 Request VO")
@Data
public class JmSearchReqVO {

    @Schema(description = "服务标识", requiredMode = Schema.RequiredMode.REQUIRED)
    private String  reqKey;

    @Schema(description = "任务ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String  taskId;

    @Schema(description = "json序列化后的字符串", requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "{\"return_url\":true}")
    private String reqJson;
}
