package com.shuaiwu.cloud.module.infra.dal.mysql.demo.demo03.erp;

import com.shuaiwu.cloud.framework.common.pojo.PageResult;
import com.shuaiwu.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.shuaiwu.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.shuaiwu.cloud.module.infra.controller.admin.demo.demo03.erp.vo.Demo03StudentErpPageReqVO;
import com.shuaiwu.cloud.module.infra.dal.dataobject.demo.demo03.Demo03StudentDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface Demo03StudentErpMapper extends BaseMapperX<Demo03StudentDO> {

    default PageResult<Demo03StudentDO> selectPage(Demo03StudentErpPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<Demo03StudentDO>()
                .likeIfPresent(Demo03StudentDO::getName, reqVO.getName())
                .eqIfPresent(Demo03StudentDO::getSex, reqVO.getSex())
                .eqIfPresent(Demo03StudentDO::getDescription, reqVO.getDescription())
                .betweenIfPresent(Demo03StudentDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(Demo03StudentDO::getId));
    }

}