package com.shuaiwu.cloud.module.xhs.dal.mysql.user;

import java.util.*;

import com.shuaiwu.cloud.framework.common.pojo.PageResult;
import com.shuaiwu.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.shuaiwu.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.shuaiwu.cloud.module.xhs.dal.dataobject.user.XhsUserDO;
import org.apache.ibatis.annotations.Mapper;
import com.shuaiwu.cloud.module.xhs.controller.admin.user.vo.*;

/**
 * 小红书-用户管理 Mapper
 *
 * @author ws
 */
@Mapper
public interface XhsUserMapper extends BaseMapperX<XhsUserDO> {

    default PageResult<XhsUserDO> selectPage(XhsUserPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<XhsUserDO>()
                .likeIfPresent(XhsUserDO::getName, reqVO.getName())
                .eqIfPresent(XhsUserDO::getGender, reqVO.getGender())
                .eqIfPresent(XhsUserDO::getPhone, reqVO.getPhone())
                .eqIfPresent(XhsUserDO::getStatus, reqVO.getStatus())
                .eqIfPresent(XhsUserDO::getLoginStatus, reqVO.getLoginStatus())
                .orderByDesc(XhsUserDO::getId));
    }

    default boolean selectByPhone(String phone){
        return selectCount(new LambdaQueryWrapperX<XhsUserDO>()
                .eq(XhsUserDO::getPhone, phone)) > 0;
    }
}