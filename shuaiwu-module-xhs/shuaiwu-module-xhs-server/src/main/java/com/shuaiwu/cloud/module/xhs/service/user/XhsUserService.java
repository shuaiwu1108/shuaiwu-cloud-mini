package com.shuaiwu.cloud.module.xhs.service.user;

import java.util.*;

import com.shuaiwu.cloud.module.xhs.controller.admin.note.vo.NoteSaveReqVO;
import jakarta.validation.*;
import com.shuaiwu.cloud.module.xhs.controller.admin.user.vo.*;
import com.shuaiwu.cloud.module.xhs.dal.dataobject.user.XhsUserDO;
import com.shuaiwu.cloud.framework.common.pojo.PageResult;
import com.shuaiwu.cloud.framework.common.pojo.PageParam;

/**
 * 小红书-用户管理 Service 接口
 *
 * @author ws
 */
public interface XhsUserService {

    /**
     * 创建小红书-用户管理
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createUser(@Valid XhsUserSaveReqVO createReqVO);

    /**
     * 更新小红书-用户管理
     *
     * @param updateReqVO 更新信息
     */
    void updateUser(@Valid XhsUserSaveReqVO updateReqVO);

    /**
     * 删除小红书-用户管理
     *
     * @param id 编号
     */
    void deleteUser(Long id);

    /**
    * 批量删除小红书-用户管理
    *
    * @param ids 编号
    */
    void deleteUserListByIds(List<Long> ids);

    /**
     * 获得小红书-用户管理
     *
     * @param id 编号
     * @return 小红书-用户管理
     */
    XhsUserDO getUser(Long id);

    /**
     * 获得小红书-用户管理分页
     *
     * @param pageReqVO 分页查询
     * @return 小红书-用户管理分页
     */
    PageResult<XhsUserDO> getUserPage(XhsUserPageReqVO pageReqVO);

    XhsUserDO getUserByPhone(String phone);

    /**
     * 通过手机号获取验证码
     * @param phone
     * @return
     */
    String getXhsVerifyCode(String phone);

    /**
     * 小红书用户登录
     * @return 登录结果
     */
    XhsUserSaveReqVO loginXhs(XhsUserLoginReqVO reqVO);

    /**
     * 同步小红书用户作品数据
     * @param reqVO 同步请求
     * @return 同步结果
     */
    List<NoteSaveReqVO> syncUserNotes(XhsUserSyncNotesReqVO reqVO);

    List<XhsUserDO> getUserList();
}