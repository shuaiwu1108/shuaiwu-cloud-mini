package com.shuaiwu.cloud.module.ai.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.shuaiwu.cloud.framework.common.pojo.CommonResult;
import com.shuaiwu.cloud.framework.common.util.io.FileUtils;
import com.shuaiwu.cloud.module.ai.controller.admin.content.vo.AiContentPageReqVO;
import com.shuaiwu.cloud.module.ai.controller.admin.jm.JmController;
import com.shuaiwu.cloud.module.ai.controller.admin.jm.vo.JmSearchReqVO;
import com.shuaiwu.cloud.module.ai.dal.dataobject.content.AiContentDO;
import com.shuaiwu.cloud.module.ai.dal.mysql.content.AiContentMapper;
import com.shuaiwu.cloud.module.ai.service.content.AiContentService;
import com.shuaiwu.cloud.module.ai.controller.admin.content.vo.AiContentSaveReqVO;
import com.shuaiwu.cloud.module.infra.api.file.FileApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class JmTaskJob {

    @Autowired
    private AiContentService contentService;

    @Autowired
    private AiContentMapper aiContentMapper;
    
    @Autowired
    private JmController jmController;

    @Autowired
    private FileApi fileApi;
    
    // 每30秒执行一次任务查询
    @Scheduled(fixedDelay = 30000)
    public void executeTaskQuery() {
        try {
            // 查询所有有taskid但没有图片url的作品（即remark不为空且files为空的作品）
            List<AiContentDO> contents = aiContentMapper.selectList();
            if (CollUtil.isEmpty(contents)) {
                return;
            }
            
            for (AiContentDO content : contents) {
                // 如果remark不为空（有taskid）且files为空（还没有图片），则查询任务状态
                if (content.getRemark() != null && !content.getRemark().isEmpty() 
                    && (content.getFiles() == null || content.getFiles().isEmpty())) {
                    queryTaskAndUpdateContent(content);
                }
            }
        } catch (Exception e) {
            log.error("定时查询即梦AI任务状态失败", e);
        }
    }
    
    /**
     * 查询任务状态并更新作品数据
     * @param content 作品数据
     */
    private void queryTaskAndUpdateContent(AiContentDO content) {
        try {
            // 构造查询参数
            JmSearchReqVO searchReqVO = new JmSearchReqVO();
            searchReqVO.setReqKey("jimeng_t2i_v40"); // 文生图任务
            searchReqVO.setTaskId(content.getRemark()); // taskid
            searchReqVO.setReqJson("{\"return_url\":true}"); // 返回图片url
            
            // 调用即梦AI查询接口
            CommonResult<Map<String, Object>> result = jmController.searchTask(searchReqVO);
            
            if (result != null && result.getData() != null) {
                JSONObject data = new JSONObject(result.getData());
                String status = data.getStr("status");
                
                // 如果任务已完成，获取图片URL并更新作品数据
                if ("done".equals(status)) {
                    // 获取图片URL数组
                    JSONArray imageUrl = data.getJSONArray("image_urls");
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        List<String> imageUrls = new ArrayList<>();
                        // 循环图片URL数组，将图片保存到minio
                        for (int i = 0; i < imageUrl.size(); i++) {
                            String url = imageUrl.getStr(i);
                            String localFileUrl = fileApi.createFile(FileUtils.getFileContent(url), null, "image/png");
                            imageUrls.add(localFileUrl);
                        }

                        // 更新作品数据，将图片URL存入files字段
                        content.setFiles(imageUrls); // 保存图片URL
                        aiContentMapper.updateById(content);
                        log.info("任务{}已完成，图片URL已保存到作品ID:{}", content.getRemark(), content.getId());
                    }
                }
                // 其他状态（如processing）继续等待下次轮询
            }
        } catch (Exception e) {
            log.error("查询任务状态并更新作品数据失败，作品ID:{}", content.getId(), e);
        }
    }
}