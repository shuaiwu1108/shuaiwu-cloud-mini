package com.shuaiwu.cloud.module.ai.dal.dataobject.content;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.shuaiwu.cloud.framework.mybatis.core.dataobject.BaseDO;

/**
 * 作品管理 DO
 *
 * @author shuaiwu
 */
@TableName(value = "ai_content", autoResultMap = true)
@KeySequence("ai_content_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiContentDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 作品名称
     */
    private String name;
    /**
     * 作品类型
     *
     * 枚举 {@link TODO xhs_note_type 对应的类}
     */
    private Integer type;
    /**
     * 作品内容
     */
    private String content;
    /**
     * 提示词
     */
    private String prompt;
    /**
     * 文件列表
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> files;

    /**
     * 备注
     */
    private String remark;


}