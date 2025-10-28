package com.shuaiwu.cloud.module.xhs.controller.admin.user;

import com.shuaiwu.cloud.module.xhs.controller.admin.note.vo.NoteSaveReqVO;
import com.shuaiwu.cloud.module.xhs.service.note.NoteService;
import lombok.extern.slf4j.Slf4j;
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

import com.shuaiwu.cloud.module.xhs.controller.admin.user.vo.*;
import com.shuaiwu.cloud.module.xhs.dal.dataobject.user.XhsUserDO;
import com.shuaiwu.cloud.module.xhs.service.user.XhsUserService;

@Tag(name = "管理后台 - 小红书-用户管理")
@RestController
@RequestMapping("/xhs/user")
@Validated
@Slf4j
public class XhsUserController {

    @Resource
    private XhsUserService userService;
    @Resource
    private NoteService noteService;

    @PostMapping("/create")
    @Operation(summary = "创建小红书-用户管理")
    @PreAuthorize("@ss.hasPermission('xhs:user:create')")
    public CommonResult<Long> createUser(@Valid @RequestBody XhsUserSaveReqVO createReqVO) {
        return success(userService.createUser(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新小红书-用户管理")
    @PreAuthorize("@ss.hasPermission('xhs:user:update')")
    public CommonResult<Boolean> updateUser(@Valid @RequestBody XhsUserSaveReqVO updateReqVO) {
        userService.updateUser(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除小红书-用户管理")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('xhs:user:delete')")
    public CommonResult<Boolean> deleteUser(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除小红书-用户管理")
                @PreAuthorize("@ss.hasPermission('xhs:user:delete')")
    public CommonResult<Boolean> deleteUserList(@RequestParam("ids") List<Long> ids) {
        userService.deleteUserListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得小红书-用户管理")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('xhs:user:query')")
    public CommonResult<XhsUserRespVO> getUser(@RequestParam("id") Long id) {
        XhsUserDO user = userService.getUser(id);
        return success(BeanUtils.toBean(user, XhsUserRespVO.class));
    }

    @GetMapping("/list-all")
    @Operation(summary = "获得小红书-用户管理列表")
    @PreAuthorize("@ss.hasPermission('xhs:user:query')")
    public CommonResult<List<XhsUserRespVO>> getUserList() {
        List<XhsUserDO> list = userService.getUserList();
        return success(BeanUtils.toBean(list, XhsUserRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得小红书-用户管理分页")
    @PreAuthorize("@ss.hasPermission('xhs:user:query')")
    public CommonResult<PageResult<XhsUserRespVO>> getUserPage(@Valid XhsUserPageReqVO pageReqVO) {
        PageResult<XhsUserDO> pageResult = userService.getUserPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, XhsUserRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出小红书-用户管理 Excel")
    @PreAuthorize("@ss.hasPermission('xhs:user:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportUserExcel(@Valid XhsUserPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<XhsUserDO> list = userService.getUserPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "小红书-用户管理.xls", "数据", XhsUserRespVO.class,
                        BeanUtils.toBean(list, XhsUserRespVO.class));
    }

    @GetMapping("/{userId}/verify-code")
    @Operation(summary = "获取小红书验证码")
    @Parameter(name = "userId", description = "用户编号", required = true)
    @PreAuthorize("@ss.hasPermission('xhs:user:edit')")
    public CommonResult<String> getVerifyCode(@PathVariable("userId") Long userId) {
        XhsUserDO user = userService.getUser(userId);
        if (user == null) {
            return CommonResult.error(404, "用户不存在");
        }
        
        try {
            String verifyCode = userService.getXhsVerifyCode(user.getPhone());
            return success(verifyCode);
        } catch (Exception e) {
            return CommonResult.error(500, "获取验证码失败：" + e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "小红书用户登录")
    @PreAuthorize("@ss.hasPermission('xhs:user:edit')")
    public CommonResult<String> login(@Valid @RequestBody XhsUserLoginReqVO reqVO) {
        // 验证用户是否存在
        XhsUserDO user = userService.getUserByPhone(reqVO.getPhone());
        if (user == null) {
            return CommonResult.error(404, "用户不存在");
        }
        
        // 验证手机号是否匹配
        if (!user.getPhone().equals(reqVO.getPhone())) {
            return CommonResult.error(400, "用户手机号不匹配");
        }
        
        try {
            XhsUserSaveReqVO result = userService.loginXhs(reqVO);
            result.setId(user.getId());
            userService.updateUser(result);

            log.info("【{}】登录成功!", result.getName());
            log.info("用户小红书账号【{}】", result.getPlatformNo());
            return CommonResult.success("登录成功，Cookie已保存");
        } catch (Exception e) {
            return CommonResult.error(500, "登录失败：" + e.getMessage());
        }
    }

    @PostMapping("/sync-notes")
    @Operation(summary = "同步小红书用户作品数据")
    @PreAuthorize("@ss.hasPermission('xhs:user:edit')")
    public CommonResult<String> syncNotes(@Valid @RequestBody XhsUserSyncNotesReqVO reqVO) {
        try {
            List<NoteSaveReqVO> noteList = userService.syncUserNotes(reqVO);
            noteService.createOrUpdateNotes(noteList);
            return CommonResult.success("成功同步作品");
        } catch (Exception e) {
            return CommonResult.error(500, "同步作品失败：" + e.getMessage());
        }
    }
    
}