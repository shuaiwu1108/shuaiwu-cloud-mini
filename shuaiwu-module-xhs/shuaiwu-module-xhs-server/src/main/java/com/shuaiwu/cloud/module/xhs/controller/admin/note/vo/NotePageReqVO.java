package com.shuaiwu.cloud.module.xhs.controller.admin.note.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.shuaiwu.cloud.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static com.shuaiwu.cloud.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 小红书笔记分页 Request VO")
@Data
public class NotePageReqVO extends PageParam {

    @Schema(description = "用户编号", example = "2745")
    private Long userId;

    @Schema(description = "笔记名称", example = "王五")
    private String name;

    @Schema(description = "快照图")
    private String image;

    @Schema(description = "发布时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] releaseTime;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "笔记类型（0图文 1视频）", example = "2")
    private String type;

    @Schema(description = "观看数")
    private Integer views;

    @Schema(description = "评论数")
    private Integer comments;

    @Schema(description = "点赞数")
    private Integer likes;

    @Schema(description = "收藏数")
    private Integer collections;

    @Schema(description = "转发数")
    private Integer forwards;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}