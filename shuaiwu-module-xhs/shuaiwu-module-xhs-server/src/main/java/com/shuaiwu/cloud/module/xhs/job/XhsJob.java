package com.shuaiwu.cloud.module.xhs.job;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shuaiwu.cloud.framework.common.util.io.FileUtils;
import com.shuaiwu.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.shuaiwu.cloud.module.infra.api.file.FileApi;
import com.shuaiwu.cloud.module.xhs.controller.admin.note.vo.NoteSaveReqVO;
import com.shuaiwu.cloud.module.xhs.controller.admin.user.vo.XhsUserSyncNotesReqVO;
import com.shuaiwu.cloud.module.xhs.dal.dataobject.note.NoteDO;
import com.shuaiwu.cloud.module.xhs.dal.dataobject.user.XhsUserDO;
import com.shuaiwu.cloud.module.xhs.dal.mysql.note.NoteMapper;
import com.shuaiwu.cloud.module.xhs.dal.mysql.user.XhsUserMapper;
import com.shuaiwu.cloud.module.xhs.enums.XhsEnum;
import com.shuaiwu.cloud.module.xhs.service.note.NoteService;
import com.shuaiwu.cloud.module.xhs.service.user.XhsUserService;
import com.shuaiwu.cloud.module.xhs.util.WebDriverFactory;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class XhsJob {

    @Resource
    private XhsUserMapper xhsUserMapper;

    @Resource
    private NoteMapper noteMapper;

    @Resource
    private XhsUserService xhsUserService;
    @Resource
    private NoteService noteService;

    @Resource
    private FileApi fileApi;

    /**
     * 维护Cookie
     * 每天16:30执行
     */
    @XxlJob("maintainCookie")
    public ReturnT<String> maintainCookie() {
        log.info("开始维护cookie");
        // 获取所有的用户
        List<XhsUserDO> xhsUsers = xhsUserMapper.selectList();
        for (XhsUserDO xhsUser : xhsUsers) {
            if (!"1".equals(xhsUser.getLoginStatus()) || xhsUser.getCookie() == null) {
                log.warn("用户未登录或Cookie不存在，无法获取线上笔记详情: userId={}", xhsUser.getId());
                return ReturnT.FAIL;
            }

            // 使用WebDriverFactory创建ChromeDriver
            WebDriver driver = WebDriverFactory.createChromeDriver();

            try {
                // 先访问小红书主页
                driver.get("https://creator.xiaohongshu.com");
                Thread.sleep(2000);

                // 解析并添加Cookie
                JSONObject cookieJson = JSON.parseObject(xhsUser.getCookie());
                JSONArray cookiesArray = cookieJson.getJSONArray("cookies");

                // 添加所有Cookie到浏览器
                for (int i = 0; i < cookiesArray.size(); i++) {
                    JSONObject cookieObj = cookiesArray.getJSONObject(i);
                    Cookie cookie = new Cookie(
                            cookieObj.getString("name"),
                            cookieObj.getString("value"),
                            cookieObj.getString("domain"),
                            cookieObj.getString("path"),
                            cookieObj.getDate("expiry"),
                            cookieObj.getBoolean("secure"),
                            cookieObj.getBoolean("httpOnly")
                    );
                    driver.manage().addCookie(cookie);
                }

                // 访问笔记详情页面
                String noteUrl = "https://creator.xiaohongshu.com/new/home";
                driver.get(noteUrl);
                Thread.sleep(5000); // 等待页面加载

                // 创建WebDriverWait实例
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
                wait.pollingEvery(Duration.ofSeconds(1));

                // 等待页面完全加载
                wait.until(webDriver -> Objects.equals(((ChromeDriver) webDriver)
                        .executeScript("return document.readyState"), "complete"));

                String userName = driver.findElement(By.className("account-name")).getText();
                log.info("【{}】使用Cookie登录成功!", userName);
                return ReturnT.SUCCESS;
            } catch (Exception e) {
                return new ReturnT<>(500, "失败：" + e.getMessage());
            } finally {
                if (driver != null) {
                    try {
                        driver.quit();
                    } catch (Exception e) {
                        log.warn("关闭WebDriver失败: error={}", e.getMessage());
                    }
                }
            }
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 同步线上笔记列表
     * 每天20:30执行
     * @return
     */
    @XxlJob("syncNote")
    public ReturnT<String> syncNote() {
        log.info("开始同步线上笔记列表");
        List<XhsUserDO> xhsUsers = xhsUserMapper.selectList();
        for (XhsUserDO xhsUser : xhsUsers) {
            if (!"1".equals(xhsUser.getLoginStatus()) || xhsUser.getCookie() == null) {
                log.warn("用户未登录或Cookie不存在，无法获取线上笔记列表: userId={}", xhsUser.getId());
                return ReturnT.FAIL;
            }

            try {
                List<NoteSaveReqVO> noteSaveReqVOS = xhsUserService.syncUserNotes(new XhsUserSyncNotesReqVO().setPhone(xhsUser.getPhone()));
                noteService.createOrUpdateNotes(noteSaveReqVOS);
            }catch (Exception e){
                return new ReturnT<>(500, "失败：" + e.getMessage());
            }
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 获取线上笔记详情
     * 每天22:30执行
     */
    @XxlJob("syncNoteDetail")
    public ReturnT<String> syncNoteDetail() {
        log.info("开始同步线上笔记详情");
        List<XhsUserDO> xhsUsers = xhsUserMapper.selectList();
        for (XhsUserDO xhsUser : xhsUsers) {
            if (!"1".equals(xhsUser.getLoginStatus()) || xhsUser.getCookie() == null) {
                log.warn("用户未登录或Cookie不存在，无法获取线上笔记详情: userId={}", xhsUser.getId());
                return ReturnT.FAIL;
            }

            // 获取用户的所有笔记
            List<NoteDO> notes = noteMapper.selectList(new LambdaQueryWrapperX<NoteDO>()
                    .eq(NoteDO::getUserId, xhsUser.getId()));

            WebDriver driver = null;
            try {
                // 使用WebDriverFactory创建ChromeDriver
                driver = WebDriverFactory.createChromeDriver();

                // 先访问小红书主页
                driver.get("https://creator.xiaohongshu.com");
                Thread.sleep(2000);

                // 解析并添加Cookie
                JSONObject cookieJson = JSON.parseObject(xhsUser.getCookie());
                JSONArray cookiesArray = cookieJson.getJSONArray("cookies");

                // 添加所有Cookie到浏览器
                for (int i = 0; i < cookiesArray.size(); i++) {
                    JSONObject cookieObj = cookiesArray.getJSONObject(i);
                    Cookie cookie = new Cookie(
                            cookieObj.getString("name"),
                            cookieObj.getString("value"),
                            cookieObj.getString("domain"),
                            cookieObj.getString("path"),
                            cookieObj.getDate("expiry"),
                            cookieObj.getBoolean("secure"),
                            cookieObj.getBoolean("httpOnly")
                    );
                    driver.manage().addCookie(cookie);
                }

                // 处理每个笔记
                for (NoteDO note : notes) {
                    getOnlineNoteDetail(driver, note);
                }

                log.info("笔记同步完成");
                return ReturnT.SUCCESS;
            } catch (Exception e) {
                log.error("同步线上笔记详情失败: error={}", e.getMessage());
                return new ReturnT<>(500, "失败：" + e.getMessage());
            } finally {
                if (driver != null) {
                    try {
                        driver.quit();
                    } catch (Exception e) {
                        log.warn("关闭WebDriver失败: error={}", e.getMessage());
                    }
                }
            }
        }
        log.info("笔记同步完成");
        return ReturnT.SUCCESS;
    }

    private void getOnlineNoteDetail(WebDriver driver, NoteDO noteDO) {
        if (noteDO == null || noteDO.getPlatformNoteId() == null) {
            return;
        }
        try {
            // 访问笔记详情页面
            String noteUrl = "https://creator.xiaohongshu.com/publish/update?id=" + noteDO.getPlatformNoteId();
            driver.get(noteUrl);

            // 创建WebDriverWait实例
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.pollingEvery(Duration.ofSeconds(1));

            try {
                // 等待页面完全加载
                wait.until(webDriver -> Objects.equals(((ChromeDriver) webDriver)
                        .executeScript("return document.readyState"), "complete"));

                // 尝试获取笔记的files列表
                try {
                    List<String> fileList = new ArrayList<>();
                    if (XhsEnum.IMAGE_NOTE.getCode().equals(noteDO.getType())) {
                        List<WebElement> files = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                                By.xpath("//div[@class='format-img']/img")));
                        for (WebElement file : files) {
                            String src = file.getAttribute("src");
                            String localFileUrl = fileApi.createFile(FileUtils.getFileContent(src), null, "image/png");
                            fileList.add(localFileUrl);
                        }
                    } else if (XhsEnum.VIDEO_NOTE.getCode().equals(noteDO.getType())) {
                        WebElement videoElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                                By.xpath("//video[contains(@class, 'exact-video video')]")));
                        String src = videoElement.getAttribute("src");
                        String localFileUrl = fileApi.createFile(FileUtils.getFileContent(src), null, "video/mp4");
                        fileList.add(localFileUrl);
                    }
                    noteDO.setFiles(fileList);
                    log.info("笔记文件列表, 数量: 【{}】", fileList.size());
                } catch (Exception e) {
                    log.warn("获取笔记文件列表失败: {}", e.getMessage());
                }

                // 尝试获取笔记content
                try {
                    WebElement contentElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//div[@class='edit-container']//div[contains(@class, 'ProseMirror')]")));
                    noteDO.setContent(contentElement.getAttribute("innerHTML"));
                    log.info("笔记内容: 【{}】", noteDO.getContent());
                } catch (Exception e) {
                    log.warn("获取笔记内容失败: {}", e.getMessage());
                }
            } catch (Exception e) {
                log.error("获取线上平台笔记详情失败: platformNoteId={}, error={}", noteDO.getPlatformNoteId(), e.getMessage());
            }
        } catch (Exception e) {
            log.error("获取线上平台笔记详情失败: platformNoteId={}, error={}", noteDO.getPlatformNoteId(), e.getMessage());
        }
    }
}
