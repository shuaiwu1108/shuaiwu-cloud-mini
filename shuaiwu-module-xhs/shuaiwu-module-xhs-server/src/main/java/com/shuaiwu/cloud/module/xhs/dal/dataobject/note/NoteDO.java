package com.shuaiwu.cloud.module.xhs.dal.dataobject.note;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.shuaiwu.cloud.framework.mybatis.core.dataobject.BaseDO;
import org.springframework.data.annotation.Transient;

/**
 * 小红书笔记 DO
 *
 * @author 芋道源码
 */
@TableName(value = "xhs_note", autoResultMap = true)
@KeySequence("xhs_note_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteDO extends BaseDO {

    /**
     * 笔记编号
     */
    @TableId
    private Long id;

    /**
     * 平台笔记编号
     */
    private String platformNoteId;

    /**
     * 用户编号
     */
    private Long userId;

    @TableField(exist = false)
    private String userName;
    /**
     * 笔记名称
     */
    private String name;
    /**
     * 快照图
     */
    private String image;
    /**
     * 作品文件url列表
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> files;
    /**
     * 发布时间
     */
    private LocalDateTime releaseTime;
    /**
     * 内容
     */
    private String content;
    /**
     * 笔记类型（0图文 1视频）
     */
    private String type;
    /**
     * 观看数
     */
    private Integer views;
    /**
     * 评论数
     */
    private Integer comments;
    /**
     * 点赞数
     */
    private Integer likes;
    /**
     * 收藏数
     */
    private Integer collections;
    /**
     * 转发数
     */
    private Integer forwards;


}
