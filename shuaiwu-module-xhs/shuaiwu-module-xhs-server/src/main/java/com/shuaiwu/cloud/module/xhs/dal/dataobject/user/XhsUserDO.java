package com.shuaiwu.cloud.module.xhs.dal.dataobject.user;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.shuaiwu.cloud.framework.mybatis.core.dataobject.BaseDO;

/**
 * 小红书-用户管理 DO
 *
 * @author ws
 */
@TableName("xhs_user")
@KeySequence("xhs_user_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XhsUserDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 名称
     */
    private String name;

    /**
     * 平台账号
     */
    private String platformNo;

    /**
     * 性别
     */
    private String gender;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 头像
     */
    private String image;
    /**
     * 个人说明
     */
    private String explainStr;
    /**
     * 关注数
     */
    private String watchNum;
    /**
     * 粉丝数
     */
    private String fansNum;
    /**
     * 获赞与收藏数
     */
    private String starsNum;
    /**
     * 账号状态（0正常 1异常）
     */
    private String status;
    /**
     * 账号状态图片，TODO 使用图片解析程序获取账号状态，存储到status中
     */
    private String statusImage;
    /**
     * 登录状态（0未登录 1已登录）
     */
    private String loginStatus;
    /**
     * 登录cookie
     */
    private String cookie;
}
