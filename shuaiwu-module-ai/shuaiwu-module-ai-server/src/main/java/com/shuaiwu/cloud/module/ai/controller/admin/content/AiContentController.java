package com.shuaiwu.cloud.module.ai.controller.admin.content;

import cn.hutool.json.JSONArray;
import com.shuaiwu.cloud.framework.common.util.http.HttpUtils;
import org.springframework.beans.factory.annotation.Value;
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

import com.shuaiwu.cloud.module.ai.controller.admin.content.vo.*;
import com.shuaiwu.cloud.module.ai.dal.dataobject.content.AiContentDO;
import com.shuaiwu.cloud.module.ai.service.content.AiContentService;

// 添加即梦AI相关导入
import com.shuaiwu.cloud.module.ai.controller.admin.jm.JmController;
import com.shuaiwu.cloud.module.ai.controller.admin.jm.vo.JmReqVO;
import cn.hutool.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

@Tag(name = "管理后台 - 作品管理")
@RestController
@RequestMapping("/ai/content")
@Validated
public class AiContentController {

    @Resource
    private AiContentService contentService;
    
    @Autowired
    private JmController jmController;

    @Value("${openrouter.info.url}")
    private String openrouterUrl;

    @Value("${openrouter.info.apiKey}")
    private String openrouterApikey;

    @Value("${openrouter.info.model}")
    private String openrouterModel;

    @PostMapping("/create")
    @Operation(summary = "创建作品管理")
    @PreAuthorize("@ss.hasPermission('ai:content:create')")
    public CommonResult<Long> createContent(@Valid @RequestBody AiContentSaveReqVO createReqVO) {
        return success(contentService.createContent(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新作品管理")
    @PreAuthorize("@ss.hasPermission('ai:content:update')")
    public CommonResult<Boolean> updateContent(@Valid @RequestBody AiContentSaveReqVO updateReqVO) {
        contentService.updateContent(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除作品管理")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ai:content:delete')")
    public CommonResult<Boolean> deleteContent(@RequestParam("id") Long id) {
        contentService.deleteContent(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除作品管理")
                @PreAuthorize("@ss.hasPermission('ai:content:delete')")
    public CommonResult<Boolean> deleteContentList(@RequestParam("ids") List<Long> ids) {
        contentService.deleteContentListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得作品管理")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('ai:content:query')")
    public CommonResult<AiContentRespVO> getContent(@RequestParam("id") Long id) {
        AiContentDO content = contentService.getContent(id);
        return success(BeanUtils.toBean(content, AiContentRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得作品管理分页")
    @PreAuthorize("@ss.hasPermission('ai:content:query')")
    public CommonResult<PageResult<AiContentRespVO>> getContentPage(@Valid AiContentPageReqVO pageReqVO) {
        PageResult<AiContentDO> pageResult = contentService.getContentPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiContentRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出作品管理 Excel")
    @PreAuthorize("@ss.hasPermission('ai:content:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportContentExcel(@Valid AiContentPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AiContentDO> list = contentService.getContentPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "作品管理.xls", "数据", AiContentRespVO.class,
                        BeanUtils.toBean(list, AiContentRespVO.class));
    }
    
    @GetMapping("/generate/{id}")
    @Operation(summary = "根据作品ID生成内容")
    @Parameter(name = "id", description = "作品编号", required = true)
    @PreAuthorize("@ss.hasPermission('ai:content:update')")
    public CommonResult<Boolean> generateContent(@PathVariable("id") Long id) {
        // 1. 查询作品管理数据
        AiContentDO content = contentService.getContent(id);
        if (content == null) {
            return success(false);
        }
        
        // 2. 生成prompt
        String prompt = content.getPrompt();
        if (prompt == null || prompt.isEmpty()) {
            JSONObject body = new JSONObject();
            body.putOnce("model", openrouterModel);
            JSONArray messages = new JSONArray();
            messages.add(new JSONObject().putOnce("role", "user").putOnce("content", content.getContent()));
            body.putOnce("messages", messages);
            String res = HttpUtils.post(openrouterUrl, Map.of("Authorization", openrouterApikey), body.toString());
            JSONObject resJson = new JSONObject(res);
            prompt = resJson.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getStr("content");
            content.setPrompt(prompt);
        }
        
        // 3. 调用即梦AI接口
        JmReqVO reqVO = new JmReqVO();
        reqVO.setReqKey("jimeng_t2i_v40"); // 文生图任务
        reqVO.setPrompt(prompt);
        
        CommonResult<Map<String, Object>> result = jmController.t2i(reqVO);
        
        // 4. 将taskid存入作品管理数据的remark字段
        if (result != null && result.getData() != null) {
            JSONObject data = new JSONObject(result.getData());
            String taskId = data.getStr("task_id");
            
            AiContentSaveReqVO updateReqVO = new AiContentSaveReqVO();
            updateReqVO.setId(id);
            updateReqVO.setName(content.getName());
            updateReqVO.setType(content.getType());
            updateReqVO.setContent(content.getContent());
            updateReqVO.setPrompt(content.getPrompt());
            updateReqVO.setFiles(content.getFiles());
            updateReqVO.setRemark(taskId); // 保存taskid到remark字段
            
            contentService.updateContent(updateReqVO);
            return success(true);
        }
        
        return success(false);
    }
}