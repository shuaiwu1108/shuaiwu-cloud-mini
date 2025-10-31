CREATE TABLE `ai_content`
(
    `id`          bigint                                 NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name`        varchar(255) COLLATE utf8mb4_general_ci         DEFAULT NULL COMMENT '作品名称',
    `type`        int                                    NOT NULL DEFAULT '0' COMMENT '0-图文，1-视频',
    `content`     mediumtext COLLATE utf8mb4_general_ci COMMENT '作品内容',
    `prompt`      mediumtext COLLATE utf8mb4_general_ci COMMENT '提示词',
    `files`       mediumtext COLLATE utf8mb4_general_ci COMMENT '文件列表',
    `remark`      varchar(255) COLLATE utf8mb4_general_ci         DEFAULT NULL COMMENT '备注',
    `creator`     varchar(64) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime                               NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     varchar(64) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime                               NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     bit(1)                                 NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`   bigint                                 NOT NULL DEFAULT '0' COMMENT '租户编号',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='作品管理';

