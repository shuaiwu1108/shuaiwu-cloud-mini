package com.shuaiwu.cloud.module.ai.service.content;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import com.shuaiwu.cloud.module.ai.controller.admin.content.vo.*;
import com.shuaiwu.cloud.module.ai.dal.dataobject.content.AiContentDO;
import com.shuaiwu.cloud.framework.common.pojo.PageResult;
import com.shuaiwu.cloud.framework.common.pojo.PageParam;
import com.shuaiwu.cloud.framework.common.util.object.BeanUtils;

import com.shuaiwu.cloud.module.ai.dal.mysql.content.AiContentMapper;

import static com.shuaiwu.cloud.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.shuaiwu.cloud.framework.common.util.collection.CollectionUtils.convertList;
import static com.shuaiwu.cloud.framework.common.util.collection.CollectionUtils.diffList;
import static com.shuaiwu.cloud.module.ai.enums.ErrorCodeConstants.*;

/**
 * 作品管理 Service 实现类
 *
 * @author shuaiwu
 */
@Service
@Validated
public class AiContentServiceImpl implements AiContentService {

    @Resource
    private AiContentMapper contentMapper;

    @Override
    public Long createContent(AiContentSaveReqVO createReqVO) {
        // 插入
        AiContentDO content = BeanUtils.toBean(createReqVO, AiContentDO.class);
        contentMapper.insert(content);

        // 返回
        return content.getId();
    }

    @Override
    public void updateContent(AiContentSaveReqVO updateReqVO) {
        // 校验存在
        validateContentExists(updateReqVO.getId());
        // 更新
        AiContentDO updateObj = BeanUtils.toBean(updateReqVO, AiContentDO.class);
        contentMapper.updateById(updateObj);
    }

    @Override
    public void deleteContent(Long id) {
        // 校验存在
        validateContentExists(id);
        // 删除
        contentMapper.deleteById(id);
    }

    @Override
        public void deleteContentListByIds(List<Long> ids) {
        // 删除
        contentMapper.deleteByIds(ids);
        }


    private void validateContentExists(Long id) {
        if (contentMapper.selectById(id) == null) {
            throw exception(CONTENT_NOT_EXISTS);
        }
    }

    @Override
    public AiContentDO getContent(Long id) {
        return contentMapper.selectById(id);
    }

    @Override
    public PageResult<AiContentDO> getContentPage(AiContentPageReqVO pageReqVO) {
        return contentMapper.selectPage(pageReqVO);
    }

}