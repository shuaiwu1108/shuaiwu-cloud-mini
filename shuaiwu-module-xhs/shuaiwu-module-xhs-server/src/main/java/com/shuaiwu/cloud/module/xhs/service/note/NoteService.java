package com.shuaiwu.cloud.module.xhs.service.note;

import java.util.*;
import jakarta.validation.*;
import com.shuaiwu.cloud.module.xhs.controller.admin.note.vo.*;
import com.shuaiwu.cloud.module.xhs.dal.dataobject.note.NoteDO;
import com.shuaiwu.cloud.framework.common.pojo.PageResult;
import com.shuaiwu.cloud.framework.common.pojo.PageParam;

/**
 * 小红书笔记 Service 接口
 *
 * @author 芋道源码
 */
public interface NoteService {

    /**
     * 创建小红书笔记
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createNote(@Valid NoteSaveReqVO createReqVO);

    void createNotes(List<NoteSaveReqVO> createReqVOList);

    void createOrUpdateNotes(List<NoteSaveReqVO> createReqVOList);

    /**
     * 更新小红书笔记
     *
     * @param updateReqVO 更新信息
     */
    void updateNote(@Valid NoteSaveReqVO updateReqVO);

    /**
     * 删除小红书笔记
     *
     * @param id 编号
     */
    void deleteNote(Long id);

    /**
    * 批量删除小红书笔记
    *
    * @param ids 编号
    */
    void deleteNoteListByIds(List<Long> ids);

    /**
     * 获得小红书笔记
     *
     * @param id 编号
     * @return 小红书笔记
     */
    NoteDO getNote(Long id);

    /**
     * 获得小红书笔记分页
     *
     * @param pageReqVO 分页查询
     * @return 小红书笔记分页
     */
    PageResult<NoteDO> getNotePage(NotePageReqVO pageReqVO);

    void getOnlineNoteDetail(NoteDO noteDO);

}