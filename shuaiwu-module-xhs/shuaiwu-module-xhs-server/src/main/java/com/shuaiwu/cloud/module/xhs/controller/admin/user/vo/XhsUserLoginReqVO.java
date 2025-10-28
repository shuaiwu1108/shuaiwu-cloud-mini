package com.shuaiwu.cloud.module.xhs.controller.admin.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import jakarta.validation.constraints.NotNull;

@Schema(description = "管理后台 - 小红书用户登录 Request VO")
@Data
@ToString
public class XhsUserLoginReqVO {

    @Schema(description = "手机号", required = true, example = "13812345678")
    @NotNull(message = "手机号不能为空")
    private String phone;

    @Schema(description = "验证码", required = true, example = "123456")
    @NotNull(message = "验证码不能为空")
    private String verifyCode;

}
