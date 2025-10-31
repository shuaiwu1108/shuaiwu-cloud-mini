package com.shuaiwu.cloud.module.ai.controller.admin.content.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import com.shuaiwu.cloud.framework.excel.core.annotations.DictFormat;
import com.shuaiwu.cloud.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 作品管理 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AiContentRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "6794")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "作品名称", example = "王五")
    @ExcelProperty("作品名称")
    private String name;

    @Schema(description = "作品类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty(value = "作品类型", converter = DictConvert.class)
    @DictFormat("xhs_note_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer type;

    @Schema(description = "作品内容")
    @ExcelProperty("作品内容")
    private String content;

    @Schema(description = "提示词")
    @ExcelProperty("提示词")
    private String prompt;

    @Schema(description = "文件列表")
    @ExcelProperty("文件列表")
    private List<String> files;

    @Schema(description = "备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}