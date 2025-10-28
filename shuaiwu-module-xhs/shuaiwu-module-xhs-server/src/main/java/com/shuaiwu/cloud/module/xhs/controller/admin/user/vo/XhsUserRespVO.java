package com.shuaiwu.cloud.module.xhs.controller.admin.user.vo;

import com.shuaiwu.cloud.framework.excel.core.annotations.DictFormat;
import com.shuaiwu.cloud.framework.excel.core.convert.DictConvert;
import com.shuaiwu.cloud.module.xhs.enums.DictTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 小红书用户 Response VO")
@Data
@ExcelIgnoreUnannotated
public class XhsUserRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3924")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "名称")
    @ExcelProperty("名称")
    private String name;

    @Schema(description = "平台账号")
    @ExcelProperty("平台账号")
    private String platformNo;

    @Schema(description = "性别")
    @ExcelProperty(value = "性别", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.USER_SEX)
    private String gender;

    @Schema(description = "联系电话")
    @ExcelProperty("联系电话")
    private String phone;

    @Schema(description = "邮箱")
    @ExcelProperty("邮箱")
    private String email;

    @Schema(description = "头像")
    @ExcelProperty("头像")
    private String image;

    @Schema(description = "关注数")
    @ExcelProperty("关注数")
    private String watchNum;

    @Schema(description = "粉丝数")
    @ExcelProperty("粉丝数")
    private String fansNum;

    @Schema(description = "获赞与收藏数")
    @ExcelProperty("获赞与收藏数")
    private String starsNum;

    @Schema(description = "个人说明")
    @ExcelProperty("个人说明")
    private String explainStr;

    @Schema(description = "账号状态")
    @ExcelProperty(value = "账号状态", converter =  DictConvert.class)
    @DictFormat(DictTypeConstants.XHS_USER_STATUS)
    private String status;

    @Schema(description = "账号状态图")
    @ExcelProperty("账号状态图")
    private String statusImage;

    @Schema(description = "登录状态")
    @ExcelProperty(value = "登录状态", converter =  DictConvert.class)
    @DictFormat(DictTypeConstants.XHS_LOGIN_STATUS)
    private String loginStatus;

    @Schema(description = "登录cookie")
    @ExcelProperty("登录cookie")
    private String cookie;

    @Schema(description = "创建者")
    @ExcelProperty("创建者")
    private String creator;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新者")
    @ExcelProperty("更新者")
    private String updater;

}
