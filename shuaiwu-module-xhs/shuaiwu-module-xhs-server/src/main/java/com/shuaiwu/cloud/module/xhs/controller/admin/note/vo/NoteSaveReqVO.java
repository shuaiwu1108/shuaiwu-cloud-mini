package com.shuaiwu.cloud.module.xhs.controller.admin.note.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 小红书笔记新增/修改 Request VO")
@Data
public class NoteSaveReqVO {

    @Schema(description = "笔记编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "29121")
    private Long id;

    @Schema(description = "平台笔记编号", example = "1024")
    private String platformNoteId;

    @Schema(description = "用户编号", example = "2745")
    private Long userId;

    @Schema(description = "笔记名称", example = "王五")
    private String name;

    @Schema(description = "快照图")
    private String image;

    @Schema(description = "作品文件列表")
    private List<String> files;

    @Schema(description = "发布时间")
    private LocalDateTime releaseTime;

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

}