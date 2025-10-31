package com.shuaiwu.cloud.module.ai.controller.admin.content.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.shuaiwu.cloud.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static com.shuaiwu.cloud.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 作品管理分页 Request VO")
@Data
public class AiContentPageReqVO extends PageParam {

    @Schema(description = "作品名称", example = "王五")
    private String name;

    @Schema(description = "作品类型", example = "0")
    private Integer type;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}