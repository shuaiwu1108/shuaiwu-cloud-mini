package com.shuaiwu.cloud.module.xhs.controller.admin.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import jakarta.validation.constraints.NotNull;

@Schema(description = "管理后台 - 小红书用户同步作品 Request VO")
@Data
@ToString
public class XhsUserSyncNotesReqVO {

    @Schema(description = "用户手机号", required = true, example = "1024")
    @NotNull(message = "用户手机号不能为空")
    private String phone;

}
