package com.shuaiwu.cloud.module.xhs.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 小红书相关枚举
 *
 * @author ws
 */
@Getter
@AllArgsConstructor
public enum XhsEnum {

    /**
     * 登录状态 - 未登录
     */
    NOT_LOGIN("0", "未登录"),
    /**
     * 登录状态 - 已登录
     */
    IS_LOGIN("1", "已登录"),

    /**
     * 笔记类型 - 图文
     */
    IMAGE_NOTE("0", "图文笔记"),
    /**
     * 笔记类型 - 视频
     */
    VIDEO_NOTE("1", "视频笔记");

    /**
     * 状态码
     */
    private final String code;
    /**
     * 描述
     */
    private final String desc;

}
