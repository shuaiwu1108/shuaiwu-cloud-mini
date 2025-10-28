package com.shuaiwu.cloud.module.xhs.controller.admin.user.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.shuaiwu.cloud.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static com.shuaiwu.cloud.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 小红书-用户管理分页 Request VO")
@Data
public class XhsUserPageReqVO extends PageParam {

    @Schema(description = "名称", example = "赵六")
    private String name;

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "账号状态", example = "1")
    private String status;

    @Schema(description = "登录状态", example = "1")
    private String loginStatus;
}