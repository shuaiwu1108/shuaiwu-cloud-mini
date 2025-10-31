package com.shuaiwu.cloud.module.ai.dal.mysql.content;

import java.util.*;

import com.shuaiwu.cloud.framework.common.pojo.PageResult;
import com.shuaiwu.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.shuaiwu.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.shuaiwu.cloud.module.ai.dal.dataobject.content.AiContentDO;
import org.apache.ibatis.annotations.Mapper;
import com.shuaiwu.cloud.module.ai.controller.admin.content.vo.*;

/**
 * 作品管理 Mapper
 *
 * @author shuaiwu
 */
@Mapper
public interface AiContentMapper extends BaseMapperX<AiContentDO> {

    default PageResult<AiContentDO> selectPage(AiContentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiContentDO>()
                .likeIfPresent(AiContentDO::getName, reqVO.getName())
                .eqIfPresent(AiContentDO::getType, reqVO.getType())
                .betweenIfPresent(AiContentDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AiContentDO::getId));
    }

}