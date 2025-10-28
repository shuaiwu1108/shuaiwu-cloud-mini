package com.shuaiwu.cloud.module.xhs.dal.mysql.note;

import java.util.*;

import com.shuaiwu.cloud.framework.common.pojo.PageResult;
import com.shuaiwu.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.shuaiwu.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.shuaiwu.cloud.module.xhs.dal.dataobject.note.NoteDO;
import org.apache.ibatis.annotations.Mapper;
import com.shuaiwu.cloud.module.xhs.controller.admin.note.vo.*;

/**
 * 小红书笔记 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface NoteMapper extends BaseMapperX<NoteDO> {

    default PageResult<NoteDO> selectPage(NotePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<NoteDO>()
                .eqIfPresent(NoteDO::getUserId, reqVO.getUserId())
                .likeIfPresent(NoteDO::getName, reqVO.getName())
                .eqIfPresent(NoteDO::getImage, reqVO.getImage())
                .betweenIfPresent(NoteDO::getReleaseTime, reqVO.getReleaseTime())
                .eqIfPresent(NoteDO::getContent, reqVO.getContent())
                .eqIfPresent(NoteDO::getType, reqVO.getType())
                .eqIfPresent(NoteDO::getViews, reqVO.getViews())
                .eqIfPresent(NoteDO::getComments, reqVO.getComments())
                .eqIfPresent(NoteDO::getLikes, reqVO.getLikes())
                .eqIfPresent(NoteDO::getCollections, reqVO.getCollections())
                .eqIfPresent(NoteDO::getForwards, reqVO.getForwards())
                .betweenIfPresent(NoteDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(NoteDO::getId));
    }

}