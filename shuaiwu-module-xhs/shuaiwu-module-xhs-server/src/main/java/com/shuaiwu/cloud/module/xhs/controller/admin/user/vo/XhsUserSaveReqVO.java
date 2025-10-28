package com.shuaiwu.cloud.module.xhs.controller.admin.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 小红书-用户管理新增/修改 Request VO")
@Data
public class XhsUserSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3924")
    private Long id;

    @Schema(description = "名称", example = "赵六")
    private String name;

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "头像")
    private String image;

    @Schema(description = "个人说明")
    private String explainStr;

    @Schema(description = "账号状态", example = "1")
    private String status;

    @Schema(description = "登录状态", example = "1")
    private String loginStatus;

    @Schema(description = "登录cookie")
    private String cookie;

    @Schema(description = "平台账号")
    private String platformNo;

    @Schema(description = "关注数")
    private String watchNum;

    @Schema(description = "粉丝数")
    private String fansNum;

    @Schema(description = "获赞与收藏数")
    private String starsNum;

    @Schema(description = "账号状态图片")
    private String statusImage;
}