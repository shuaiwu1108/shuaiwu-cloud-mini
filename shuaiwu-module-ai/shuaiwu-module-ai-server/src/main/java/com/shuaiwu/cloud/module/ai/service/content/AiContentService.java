package com.shuaiwu.cloud.module.ai.service.content;

import java.util.*;
import jakarta.validation.*;
import com.shuaiwu.cloud.module.ai.controller.admin.content.vo.*;
import com.shuaiwu.cloud.module.ai.dal.dataobject.content.AiContentDO;
import com.shuaiwu.cloud.framework.common.pojo.PageResult;
import com.shuaiwu.cloud.framework.common.pojo.PageParam;

/**
 * 作品管理 Service 接口
 *
 * @author shuaiwu
 */
public interface AiContentService {

    /**
     * 创建作品管理
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createContent(@Valid AiContentSaveReqVO createReqVO);

    /**
     * 更新作品管理
     *
     * @param updateReqVO 更新信息
     */
    void updateContent(@Valid AiContentSaveReqVO updateReqVO);

    /**
     * 删除作品管理
     *
     * @param id 编号
     */
    void deleteContent(Long id);

    /**
    * 批量删除作品管理
    *
    * @param ids 编号
    */
    void deleteContentListByIds(List<Long> ids);

    /**
     * 获得作品管理
     *
     * @param id 编号
     * @return 作品管理
     */
    AiContentDO getContent(Long id);

    /**
     * 获得作品管理分页
     *
     * @param pageReqVO 分页查询
     * @return 作品管理分页
     */
    PageResult<AiContentDO> getContentPage(AiContentPageReqVO pageReqVO);

}