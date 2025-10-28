package com.shuaiwu.cloud.module.xhs.controller.admin.note;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;

import com.shuaiwu.cloud.framework.common.pojo.PageParam;
import com.shuaiwu.cloud.framework.common.pojo.PageResult;
import com.shuaiwu.cloud.framework.common.pojo.CommonResult;
import com.shuaiwu.cloud.framework.common.util.object.BeanUtils;
import static com.shuaiwu.cloud.framework.common.pojo.CommonResult.success;

import com.shuaiwu.cloud.framework.excel.core.util.ExcelUtils;

import com.shuaiwu.cloud.framework.apilog.core.annotation.ApiAccessLog;
import static com.shuaiwu.cloud.framework.apilog.core.enums.OperateTypeEnum.*;

import com.shuaiwu.cloud.module.xhs.controller.admin.note.vo.*;
import com.shuaiwu.cloud.module.xhs.dal.dataobject.note.NoteDO;
import com.shuaiwu.cloud.module.xhs.service.note.NoteService;

@Tag(name = "管理后台 - 小红书笔记")
@RestController
@RequestMapping("/xhs/note")
@Validated
public class NoteController {

    @Resource
    private NoteService noteService;

    @PostMapping("/create")
    @Operation(summary = "创建小红书笔记")
    @PreAuthorize("@ss.hasPermission('xhs:note:create')")
    public CommonResult<Long> createNote(@Valid @RequestBody NoteSaveReqVO createReqVO) {
        return success(noteService.createNote(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新小红书笔记")
    @PreAuthorize("@ss.hasPermission('xhs:note:update')")
    public CommonResult<Boolean> updateNote(@Valid @RequestBody NoteSaveReqVO updateReqVO) {
        noteService.updateNote(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除小红书笔记")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('xhs:note:delete')")
    public CommonResult<Boolean> deleteNote(@RequestParam("id") Long id) {
        noteService.deleteNote(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除小红书笔记")
                @PreAuthorize("@ss.hasPermission('xhs:note:delete')")
    public CommonResult<Boolean> deleteNoteList(@RequestParam("ids") List<Long> ids) {
        noteService.deleteNoteListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得小红书笔记")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('xhs:note:query')")
    public CommonResult<NoteRespVO> getNote(@RequestParam("id") Long id) {
        NoteDO note = noteService.getNote(id);
        return success(BeanUtils.toBean(note, NoteRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得小红书笔记分页")
    @PreAuthorize("@ss.hasPermission('xhs:note:query')")
    public CommonResult<PageResult<NoteRespVO>> getNotePage(@Valid NotePageReqVO pageReqVO) {
        PageResult<NoteDO> pageResult = noteService.getNotePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, NoteRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出小红书笔记 Excel")
    @PreAuthorize("@ss.hasPermission('xhs:note:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportNoteExcel(@Valid NotePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<NoteDO> list = noteService.getNotePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "小红书笔记.xls", "数据", NoteRespVO.class,
                        BeanUtils.toBean(list, NoteRespVO.class));
    }

}