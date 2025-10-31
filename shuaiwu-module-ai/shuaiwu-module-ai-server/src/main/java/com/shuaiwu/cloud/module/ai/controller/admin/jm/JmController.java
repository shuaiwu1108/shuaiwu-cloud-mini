package com.shuaiwu.cloud.module.ai.controller.admin.jm;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.shuaiwu.cloud.framework.common.pojo.CommonResult;
import com.shuaiwu.cloud.module.ai.controller.admin.jm.vo.JmReqVO;
import com.shuaiwu.cloud.module.ai.controller.admin.jm.vo.JmSearchReqVO;
import com.shuaiwu.cloud.module.ai.util.Sign;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Tag(name = "管理后台 - 即梦AI")
@RestController
@RequestMapping("/ai/jm")
@Validated
@Slf4j
public class JmController {

    @Value("${jm.info.ak:test}")
    private String ak;
    @Value("${jm.info.sk:test}")
    private String sk;

    @PostMapping("/info-t2i")
    @Operation(summary = "内容生成")
    public CommonResult<Map<String, Object>> t2i(@Valid JmReqVO reqVO) {
        // 请求域名
        String endpoint = "visual.volcengineapi.com";
        String path = "/"; // 路径，不包含 Query// 请求接口信息
        String service = "cv";
        String region = "cn-north-1";
        String schema = "https";
        Sign sign = new Sign(region, service, schema, endpoint, path, ak, sk);
        // 参考接口文档Query参数
        String action = "CVSync2AsyncSubmitTask";
        String version = "2022-08-31";
        Date date = new Date();
        // 参考接口文档Body参数
        JSONObject req = new JSONObject();
        req.putOpt("req_key", reqVO.getReqKey());
        req.putOpt("prompt", reqVO.getPrompt());

        try {
            String response = sign.doRequest("POST", new HashMap<>(), req.toString().getBytes(StandardCharsets.UTF_8), date, action, version);
            JSONObject json = JSONUtil.parseObj(response);
            JSONObject data = json.getJSONObject("data");
            return CommonResult.success(data);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return CommonResult.success(null);
    }

    @GetMapping("/search_task")
    @Operation(summary = "查询任务")
    public CommonResult<Map<String, Object>> searchTask(JmSearchReqVO reqVO) {
        // 请求域名
        String endpoint = "visual.volcengineapi.com";
        String path = "/"; // 路径，不包含 Query// 请求接口信息
        String service = "cv";
        String region = "cn-north-1";
        String schema = "https";
        Sign sign = new Sign(region, service, schema, endpoint, path, ak, sk);
        // 参考接口文档Query参数
        String action = "CVSync2AsyncGetResult";
        String version = "2022-08-31";
        Date date = new Date();
        // 参考接口文档Body参数
        JSONObject req = new JSONObject();
        req.putOpt("req_key", reqVO.getReqKey());
        req.putOpt("task_id", reqVO.getTaskId());
        req.putOpt("req_json", reqVO.getReqJson());

        try {
            String response = sign.doRequest("POST", new HashMap<>(), req.toString().getBytes(StandardCharsets.UTF_8), date, action, version);
            JSONObject json = JSONUtil.parseObj(response);
            JSONObject data = json.getJSONObject("data");
            return CommonResult.success(data);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return CommonResult.success(null);
    }
}
