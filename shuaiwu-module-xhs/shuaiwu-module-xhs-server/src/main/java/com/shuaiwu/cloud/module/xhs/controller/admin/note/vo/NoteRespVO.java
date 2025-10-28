package com.shuaiwu.cloud.module.xhs.controller.admin.note.vo;

import com.shuaiwu.cloud.framework.excel.core.annotations.DictFormat;
import com.shuaiwu.cloud.framework.excel.core.convert.DictConvert;
import com.shuaiwu.cloud.module.xhs.enums.DictTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 小红书笔记 Response VO")
@Data
@ExcelIgnoreUnannotated
public class NoteRespVO {

    @Schema(description = "笔记编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "29121")
    @ExcelProperty("笔记编号")
    private Long id;

    @Schema(description = "平台笔记编号", example = "1024")
    @ExcelProperty("平台笔记编号")
    private String platformNoteId;

    @Schema(description = "用户编号", example = "2745")
    @ExcelIgnore
    private Long userId;

    @Schema(description = "用户名称")
    @ExcelProperty("用户名称")
    private String userName;

    @Schema(description = "笔记名称", example = "王五")
    @ExcelProperty("笔记名称")
    private String name;

    @Schema(description = "快照图")
    @ExcelProperty("快照图")
    private String image;

    @Schema(description = "作品文件列表")
    @ExcelProperty("作品文件列表")
    private List<String> files;

    @Schema(description = "发布时间")
    @ExcelProperty("发布时间")
    private LocalDateTime releaseTime;

    @Schema(description = "内容")
    @ExcelProperty("内容")
    private String content;

    @Schema(description = "笔记类型（0图文 1视频）", example = "2")
    @ExcelProperty(value = "笔记类型", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.XHS_NOTE_TYPE)
    private String type;

    @Schema(description = "观看数")
    @ExcelProperty("观看数")
    private Integer views;

    @Schema(description = "评论数")
    @ExcelProperty("评论数")
    private Integer comments;

    @Schema(description = "点赞数")
    @ExcelProperty("点赞数")
    private Integer likes;

    @Schema(description = "收藏数")
    @ExcelProperty("收藏数")
    private Integer collections;

    @Schema(description = "转发数")
    @ExcelProperty("转发数")
    private Integer forwards;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
