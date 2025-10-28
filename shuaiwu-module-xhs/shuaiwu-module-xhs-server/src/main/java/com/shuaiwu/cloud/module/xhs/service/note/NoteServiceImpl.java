package com.shuaiwu.cloud.module.xhs.service.note;

import cn.hutool.core.collection.CollUtil;
import com.shuaiwu.cloud.framework.common.util.io.FileUtils;
import com.shuaiwu.cloud.framework.common.util.selenium.SeleniumUtils;
import com.shuaiwu.cloud.module.infra.api.file.FileApi;
import com.shuaiwu.cloud.module.xhs.dal.dataobject.user.XhsUserDO;
import com.shuaiwu.cloud.module.xhs.dal.mysql.user.XhsUserMapper;
import com.shuaiwu.cloud.module.xhs.enums.XhsEnum;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import com.shuaiwu.cloud.module.xhs.controller.admin.note.vo.*;
import com.shuaiwu.cloud.module.xhs.dal.dataobject.note.NoteDO;
import com.shuaiwu.cloud.framework.common.pojo.PageResult;
import com.shuaiwu.cloud.framework.common.util.object.BeanUtils;

import com.shuaiwu.cloud.module.xhs.dal.mysql.note.NoteMapper;
import com.shuaiwu.cloud.module.xhs.util.WebDriverFactory;

import static com.shuaiwu.cloud.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.shuaiwu.cloud.module.xhs.enums.ErrorCodeConstants.*;

// 添加 Selenium 相关导入
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 小红书笔记 Service 实现类
 *
 * @author 芋道源码
 */
@Slf4j
@Service
@Validated
public class NoteServiceImpl implements NoteService {

    @Resource
    private NoteMapper noteMapper;
    @Autowired
    private XhsUserMapper xhsUserMapper;
    @Autowired
    private FileApi fileApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createNote(NoteSaveReqVO createReqVO) {
        // 插入
        NoteDO note = BeanUtils.toBean(createReqVO, NoteDO.class);

        // 处理线上平台笔记
        createOnlineNoteDetail(note);

        noteMapper.insert(note);
        // 返回
        return note.getId();
    }

    private void createOnlineNoteDetail(NoteDO noteDO) {
        ChromeDriver driver = null;
        try {
            // 获取用户信息
            XhsUserDO user = xhsUserMapper.selectById(noteDO.getUserId());
            if (user == null || !"1".equals(user.getLoginStatus()) || user.getCookie() == null) {
                log.warn("用户未登录或Cookie不存在，无法更新线上笔记详情: userId={}", noteDO.getUserId());
                return;
            }

            // 使用WebDriverFactory创建ChromeDriver
            driver = WebDriverFactory.createChromeDriver();

            // 先访问小红书主页
            driver.get("https://creator.xiaohongshu.com");
            Thread.sleep(2000);

            // 解析并添加Cookie
            JSONObject cookieJson = JSON.parseObject(user.getCookie());
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

            // 访问发布笔记页
            if (XhsEnum.IMAGE_NOTE.getCode().equals(noteDO.getType())) {
                driver.get("https://creator.xiaohongshu.com/publish/publish?from=menu&target=image");
            } else if (XhsEnum.VIDEO_NOTE.getCode().equals(noteDO.getType())) {
                driver.get("https://creator.xiaohongshu.com/publish/publish?from=menu&target=video");
            }
            Thread.sleep(5000); // 等待页面加载

            // 创建WebDriverWait实例
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.pollingEvery(Duration.ofSeconds(1));

            try {
                // 等待页面完全加载
                wait.until(webDriver -> Objects.equals(((ChromeDriver) webDriver)
                        .executeScript("return document.readyState"), "complete"));

                // 上传图文、视频
                WebElement uploadElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".upload-input")));
                if (XhsEnum.IMAGE_NOTE.getCode().equals(noteDO.getType())) {
                    // 处理多个图片
                    if (noteDO.getFiles() != null && !noteDO.getFiles().isEmpty()) {
                        List<String> tempFilePaths = new ArrayList<>();
                        // 为每个文件创建临时文件
                        for (String fileUrl : noteDO.getFiles()) {
                            try {
                                // 下载文件内容
                                byte[] fileContent = FileUtils.getFileContent(fileUrl);
                                // 创建临时文件
                                File tempFile = FileUtils.createTempFile(fileContent, ".png");
                                tempFilePaths.add(tempFile.getAbsolutePath());
                                log.info("创建临时文件: {}", tempFile.getAbsolutePath());
                            } catch (Exception e) {
                                log.warn("下载或创建临时文件失败, fileUrl={}", fileUrl, e);
                            }
                        }

                        // 使用Selenium上传所有文件
                        if (!tempFilePaths.isEmpty()) {
                            // 将所有文件路径用换行符连接（适用于多文件上传）
                            String allFilePaths = String.join("\n", tempFilePaths);
                            uploadElement.sendKeys(allFilePaths);
                            log.info("已发送文件路径到上传输入框，文件数量: {}", tempFilePaths.size());
                        }
                    }

                    // 等待一段时间确保文件上传完成
                    Thread.sleep(3000 + new Random().nextInt(2000));
                } else if (XhsEnum.VIDEO_NOTE.getCode().equals(noteDO.getType())) {
                    try {
                        // 下载文件内容
                        byte[] fileContent = FileUtils.getFileContent(noteDO.getFiles().get(0));
                        // 创建临时文件
                        File tempFile = FileUtils.createTempFile(fileContent, ".mp4");
                        log.info("创建临时视频: {}", tempFile.getAbsolutePath());
                        uploadElement.sendKeys(tempFile.getAbsolutePath());
                        log.info("已发送视频路径到上传输入框，文件数量: {}", 1);

                        Thread.sleep(3000);

                        // 对于视频，等待发布按钮变为可点击即表示处理完成
                        SeleniumUtils.waitForPublishButtonClickable(driver, "//button[contains(@class, 'd-button d-button-large')]");
                    } catch (Exception e) {
                        log.warn("视频文件上传失败, fileUrl={}", noteDO.getFiles().get(0), e);
                    }

                    try {
                        // 4. 找到修改封面div
                        WebElement imageDiv = wait.until(ExpectedConditions.presenceOfElementLocated(
                                By.xpath("//div[text()='设置封面']/..")));
                        imageDiv.click();

                        Thread.sleep(3000);

                        // 找到input，并传递文件路径
                        WebElement imageInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                                By.xpath("//div[@id='workspace']/following-sibling::input[1]")
                        ));
                        // 下载文件内容
                        byte[] fileContent = FileUtils.getFileContent(noteDO.getImage());
                        // 创建临时文件
                        File tempFile = FileUtils.createTempFile(fileContent, ".png");
                        log.info("创建临时文件: {}", tempFile.getAbsolutePath());
                        imageInput.sendKeys(tempFile.getAbsolutePath());

                        // 点击确定
                        WebElement confirmButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                                By.xpath("//span[text()=' 确定 ']")
                        ));
                        confirmButton.click();
                        Thread.sleep(10000);
                    } catch (Exception e) {
                        log.warn("设置视频封面文件失败, fileUrl={}", noteDO.getImage(), e);
                    }
                }

                // 填写笔记标题
                try {
                    WebElement titleElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//input[@placeholder='填写标题会有更多赞哦～']")));
                    // 使用JavaScript清空并设置内容，避免被检测为自动化程序
                    Thread.sleep(2000 + new Random().nextInt(500)); // 随机延迟

                    // 模拟人工输入标题
                    String title = noteDO.getName();
                    for (char c : title.toCharArray()) {
                        String currentTitle = titleElement.getAttribute("value") + c;
                        driver.executeScript("arguments[0].value = arguments[1];", titleElement, currentTitle);
                        // 触发input事件，确保平台能够识别到输入变化
                        driver.executeScript("arguments[0].dispatchEvent(new Event('input', {bubbles: true}));", titleElement);
                        Thread.sleep(50 + new Random().nextInt(150)); // 随机延迟，模拟人工输入
                    }
                    
                    // 触发change和blur事件，确保平台能够识别到输入完成
                    driver.executeScript("arguments[0].dispatchEvent(new Event('change', {bubbles: true}));", titleElement);
                    driver.executeScript("arguments[0].blur();", titleElement);

                    log.info("笔记标题: {}", noteDO.getName());
                    Thread.sleep(2000 + new Random().nextInt(500));
                } catch (Exception e) {
                    log.warn("填写笔记标题失败: {}", e.getMessage());
                }

                // 笔记内容
                try {
                    WebElement contentElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//div[@class='edit-container']//div[contains(@class, 'ProseMirror')]")));
                    // 清空原有内容
                    driver.executeScript(
                            "arguments[0].innerHTML = '';", contentElement);
                    Thread.sleep(2000 + new Random().nextInt(500)); // 随机延迟

                    // 使用更可靠的方式设置内容
                    // 首先尝试直接设置innerHTML
                    driver.executeScript(
                            "arguments[0].innerHTML = arguments[1];", contentElement, noteDO.getContent());

                    // 然后触发change事件确保编辑器正确识别内容变化
                    driver.executeScript(
                            "arguments[0].dispatchEvent(new Event('input', {bubbles: true}));" +
                                    "arguments[0].dispatchEvent(new Event('change', {bubbles: true}));" +
                                    "arguments[0].blur();", contentElement);

                    log.info("笔记内容: {}", noteDO.getContent());
                    Thread.sleep(2000 + new Random().nextInt(500));
                } catch (Exception e) {
                    log.warn("填写笔记内容失败: {}", e.getMessage());
                }

                // 开始保存
                WebElement saveButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//span[text()='发布']")));
                // 使用JavaScript执行点击操作，避免被检测为自动化程序
                driver.executeScript("arguments[0].scrollIntoView(true);", saveButton);
                Thread.sleep(500 + new Random().nextInt(500)); // 随机延迟
                driver.executeScript("arguments[0].click();", saveButton);
                log.info("已点击发布按钮，等待响应...");

                Thread.sleep(2000);

                // 访问笔记管理页面
                driver.get("https://creator.xiaohongshu.com/new/note-manager");
                Thread.sleep(5000); // 等待页面加载

                // 等待并获取内容容器
                WebElement contentContainer = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//div[@class='content']")));

                // 确保内容容器可见
                wait.until(ExpectedConditions.visibilityOf(contentContainer));

                // 等待并获取所有作品元素
                List<WebElement> noteElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.xpath("//div[@class='note']")));

                // 确保找到了笔记元素
                if (noteElements.isEmpty()) {
                    log.warn("未找到任何笔记元素");
                    return;
                }

                log.info("成功加载笔记列表，共找到 {} 个笔记", noteElements.size());

                // 获取第一个笔记元素
                WebElement noteElement = noteElements.get(0);
                // 等待并获取平台noteId
                String dataImpression = wait.until(d -> noteElement.getAttribute("data-impression"));
                JSONObject entries = JSONObject.parseObject(dataImpression);
                JSONObject noteTarget = entries.getJSONObject("noteTarget");
                JSONObject noteObj = noteTarget.getJSONObject("value");
                String noteId = noteObj.getString("noteId");
                noteDO.setPlatformNoteId(noteId);
            } catch (Exception e) {
                log.error("保存线上平台笔记详情失败: platformNoteId={}, error={}", noteDO.getPlatformNoteId(), e.getMessage());
            }
        } catch (Exception e) {
            log.error("保存线上平台笔记详情失败: error={}", e.getMessage());
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

    @Override
    public void createNotes(List<NoteSaveReqVO> createReqVOList) {
        List<NoteDO> noteDOList = new ArrayList<>();
        for (NoteSaveReqVO createReqVO : createReqVOList) {
            NoteDO note = BeanUtils.toBean(createReqVO, NoteDO.class);
            noteDOList.add(note);
        }
        noteMapper.insertBatch(noteDOList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrUpdateNotes(List<NoteSaveReqVO> updateReqVOList) {
        if (CollUtil.isEmpty(updateReqVOList)) {
            return;
        }

        List<String> platformNoteIds = updateReqVOList.stream()
                .map(NoteSaveReqVO::getPlatformNoteId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<String, NoteDO> existingNotesMap = new HashMap<>();
        if (CollUtil.isNotEmpty(platformNoteIds)) {
            List<NoteDO> existingNotes = noteMapper.selectList(NoteDO::getPlatformNoteId, platformNoteIds);
            existingNotesMap = existingNotes.stream()
                    .collect(Collectors.toMap(NoteDO::getPlatformNoteId, note -> note));
        }

        List<NoteDO> toInsert = new ArrayList<>();
        List<NoteDO> toUpdate = new ArrayList<>();

        for (NoteSaveReqVO updateReqVO : updateReqVOList) {
            NoteDO note = BeanUtils.toBean(updateReqVO, NoteDO.class);
            
            if (updateReqVO.getPlatformNoteId() != null && 
                existingNotesMap.containsKey(updateReqVO.getPlatformNoteId())) {
                NoteDO existingNote = existingNotesMap.get(updateReqVO.getPlatformNoteId());
                note.setId(existingNote.getId());
                toUpdate.add(note);
            } else {
                toInsert.add(note);
            }
        }

        if (CollUtil.isNotEmpty(toInsert)) {
            noteMapper.insertBatch(toInsert);
        }
        if (CollUtil.isNotEmpty(toUpdate)) {
            noteMapper.updateBatch(toUpdate);
        }
        log.info("[createOrUpdateNotes][创建({}) 个笔记]", toInsert.size());
        log.info("[createOrUpdateNotes][更新({}) 个笔记]", toUpdate.size());
    }

    @Override
    public void updateNote(NoteSaveReqVO updateReqVO) {
        // 校验存在
        validateNoteExists(updateReqVO.getId());
        // 更新
        NoteDO updateObj = BeanUtils.toBean(updateReqVO, NoteDO.class);

        // 更新线上作品
        updateOnlineNoteDetail(updateObj);

        noteMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNote(Long id) {
        // 校验存在
        validateNoteExists(id);

        NoteDO noteDO = noteMapper.selectById(id);

        // 删除线上平台数据
        deleteOnlineNote(noteDO);

        // 删除
        noteMapper.deleteById(id);
    }

    /**
     * 删除线上平台的笔记数据
     * 
     * @param noteDO 笔记信息
     */
    private void deleteOnlineNote(NoteDO noteDO) {
        if (noteDO == null || noteDO.getPlatformNoteId() == null) {
            return;
        }

        ChromeDriver driver = null;
        try {
            // 获取用户信息
            XhsUserDO user = xhsUserMapper.selectById(noteDO.getUserId());
            if (user == null || !"1".equals(user.getLoginStatus()) || user.getCookie() == null) {
                log.warn("用户未登录或Cookie不存在，无法删除线上笔记: userId={}", noteDO.getUserId());
                return;
            }

            // 使用WebDriverFactory创建ChromeDriver
            driver = WebDriverFactory.createChromeDriver();

            // 先访问小红书主页
            driver.get("https://creator.xiaohongshu.com");
            Thread.sleep(2000);

            // 解析并添加Cookie
            JSONObject cookieJson = JSON.parseObject(user.getCookie());
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

            // 访问作者创作平台的笔记管理页面
            driver.get("https://creator.xiaohongshu.com/new/note-manager");
            Thread.sleep(2000);

            // 创建WebDriverWait实例
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.pollingEvery(Duration.ofSeconds(1));

            try {
                // 等待页面完全加载
                wait.until(webDriver -> Objects.equals(((ChromeDriver) webDriver)
                        .executeScript("return document.readyState"), "complete"));

                // 等待笔记列表容器加载
                WebElement contentContainer = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[@class='content']")));

                // 确保内容容器可见
                wait.until(ExpectedConditions.visibilityOf(contentContainer));

                // 下拉加载更多内容，确保能找到目标笔记
                SeleniumUtils.handleScrollLoad(driver, ".content", ".note", 30);

                // 查找目标笔记元素
                List<WebElement> noteElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.xpath("//div[@class='note']")));

                WebElement targetNoteElement = null;
                for (WebElement noteElement : noteElements) {
                    String dataImpression = noteElement.getAttribute("data-impression");
                    if (dataImpression != null) {
                        JSONObject entries = JSONObject.parseObject(dataImpression);
                        JSONObject noteTarget = entries.getJSONObject("noteTarget");
                        JSONObject noteObj = noteTarget.getJSONObject("value");
                        String noteId = noteObj.getString("noteId");
                        
                        if (noteDO.getPlatformNoteId().equals(noteId)) {
                            targetNoteElement = noteElement;
                            break;
                        }
                    }
                }

                if (targetNoteElement == null) {
                    log.warn("未找到线上平台的笔记: platformNoteId={}", noteDO.getPlatformNoteId());
                    return;
                }

                // 如果找到目标笔记元素，则滚动到该元素
                driver.executeScript("arguments[0].scrollIntoView(true);", targetNoteElement);
                Thread.sleep(1000);

                // 点击删除按钮
                WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath(".//span[contains(@class, 'data-del')]")));
                deleteButton.click();
                Thread.sleep(2000);

                // 确认删除
                WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[text()='确定']")));
                confirmButton.click();
                Thread.sleep(2000);

                log.info("成功删除线上平台笔记: noteName={}, platformNoteId={}", noteDO.getName(), noteDO.getPlatformNoteId());
            } catch (Exception e) {
                log.error("删除线上平台笔记失败: noteName={}, platformNoteId={}, error={}", noteDO.getName(), noteDO.getPlatformNoteId(), e.getMessage());
            }
        } catch (Exception e) {
            log.error("删除线上平台笔记失败: noteName={}, platformNoteId={}, error={}", noteDO.getName(), noteDO.getPlatformNoteId(), e.getMessage());
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNoteListByIds(List<Long> ids) {
        // 循环调用线上平台的删除
        for (Long id : ids) {
            NoteDO noteDO = noteMapper.selectById(id);
            deleteOnlineNote(noteDO);
        }

        // 删除
        noteMapper.deleteByIds(ids);
    }


    private void validateNoteExists(Long id) {
        if (noteMapper.selectById(id) == null) {
            throw exception(NOTE_NOT_EXISTS);
        }
    }

    @Override
    public NoteDO getNote(Long id) {
        NoteDO noteDO = noteMapper.selectById(id);

        if (noteDO != null) {
            noteDO.setUserName(xhsUserMapper.selectById(noteDO.getUserId()).getName());
        }
        return noteDO;
    }

    /**
     * 从线上平台获取笔记详细信息
     * 
     * @param noteDO 本地笔记信息
     */
    @Override
    public void getOnlineNoteDetail(NoteDO noteDO) {
        if (noteDO == null || noteDO.getPlatformNoteId() == null) {
            return;
        }

        WebDriver driver = null;
        try {
            // 获取用户信息
            XhsUserDO user = xhsUserMapper.selectById(noteDO.getUserId());
            if (user == null || !"1".equals(user.getLoginStatus()) || user.getCookie() == null) {
                log.warn("用户未登录或Cookie不存在，无法获取线上笔记详情: userId={}", noteDO.getUserId());
                return;
            }

            // 使用WebDriverFactory创建ChromeDriver
            driver = WebDriverFactory.createChromeDriver();

            // 先访问小红书主页
            driver.get("https://creator.xiaohongshu.com");
            Thread.sleep(2000);

            // 解析并添加Cookie
            JSONObject cookieJson = JSON.parseObject(user.getCookie());
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
            String noteUrl = "https://creator.xiaohongshu.com/publish/update?id=" + noteDO.getPlatformNoteId();
            driver.get(noteUrl);
            Thread.sleep(5000); // 等待页面加载

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

    /**
     * 更新线上平台笔记详细信息
     *
     * @param noteDO 本地笔记信息
     */
    private void updateOnlineNoteDetail(NoteDO noteDO) {
        if (noteDO == null || noteDO.getPlatformNoteId() == null) {
            return;
        }

        ChromeDriver driver = null;
        try {
            // 获取用户信息
            XhsUserDO user = xhsUserMapper.selectById(noteDO.getUserId());
            if (user == null || !"1".equals(user.getLoginStatus()) || user.getCookie() == null) {
                log.warn("用户未登录或Cookie不存在，无法更新线上笔记详情: userId={}", noteDO.getUserId());
                return;
            }

            // 使用WebDriverFactory创建ChromeDriver
            driver = WebDriverFactory.createChromeDriver();

            // 先访问小红书主页
            driver.get("https://creator.xiaohongshu.com");
            Thread.sleep(2000);

            // 解析并添加Cookie
            JSONObject cookieJson = JSON.parseObject(user.getCookie());
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
            String noteUrl = "https://creator.xiaohongshu.com/publish/update?id=" + noteDO.getPlatformNoteId();
            driver.get(noteUrl);
            Thread.sleep(5000); // 等待页面加载

            // 创建WebDriverWait实例
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.pollingEvery(Duration.ofSeconds(1));

            try {
                // 等待页面完全加载
                wait.until(webDriver -> Objects.equals(((ChromeDriver) webDriver)
                        .executeScript("return document.readyState"), "complete"));

                // 更新笔记标题
                try {
                    WebElement titleElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//input[@placeholder='填写标题会有更多赞哦～']")));
                    // 使用JavaScript清空并设置内容，避免被检测为自动化程序
                    driver.executeScript("arguments[0].value = '';", titleElement);
                    Thread.sleep(300 + new Random().nextInt(500)); // 随机延迟
                    
                    // 模拟人工输入标题
                    String title = noteDO.getName();
                    for (char c : title.toCharArray()) {
                        String currentTitle = titleElement.getAttribute("value") + c;
                        driver.executeScript("arguments[0].value = arguments[1];", titleElement, currentTitle);
                        // 触发input事件，确保平台能够识别到输入变化
                        driver.executeScript("arguments[0].dispatchEvent(new Event('input', {bubbles: true}));", titleElement);
                        Thread.sleep(50 + new Random().nextInt(150)); // 随机延迟，模拟人工输入
                    }
                    
                    // 触发change和blur事件，确保平台能够识别到输入完成
                    driver.executeScript("arguments[0].dispatchEvent(new Event('change', {bubbles: true}));", titleElement);
                    driver.executeScript("arguments[0].blur();", titleElement);
                    
                    log.info("更新笔记标题: {}", noteDO.getName());
                } catch (Exception e) {
                    log.warn("更新笔记标题失败: {}", e.getMessage());
                }

                // 更新笔记内容
                try {
                    WebElement contentElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//div[@class='edit-container']//div[contains(@class, 'ProseMirror')]")));
                    // 清空原有内容
                    driver.executeScript(
                            "arguments[0].innerHTML = '';", contentElement);
                    Thread.sleep(2000 + new Random().nextInt(500)); // 随机延迟
                    
                    // 使用更可靠的方式设置内容
                    // 首先尝试直接设置innerHTML
                    driver.executeScript(
                            "arguments[0].innerHTML = arguments[1];", contentElement, noteDO.getContent());
                    
                    // 然后触发change事件确保编辑器正确识别内容变化
                    driver.executeScript(
                            "arguments[0].dispatchEvent(new Event('input', {bubbles: true}));" +
                            "arguments[0].dispatchEvent(new Event('change', {bubbles: true}));" +
                            "arguments[0].blur();", contentElement);
                            
                    log.info("更新笔记内容: {}", noteDO.getContent());
                } catch (Exception e) {
                    log.warn("更新笔记内容失败: {}", e.getMessage());
                }

                // 更新文件列表
                if (XhsEnum.IMAGE_NOTE.getCode().equals(noteDO.getType())){
                    try {
                        // 循环删除文件，最后一个文件不删除
                        List<WebElement> fileList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                                By.xpath("//div[contains(@class, 'icon-close hoverShow')]")));

                        // 删除除最后一个文件外的所有文件
                        // 每次删除后需要重新获取元素列表，因为页面元素会发生变化
                        for (int i = 0; i < fileList.size() - 1; i++) {
                            // 重新定位元素，避免StaleElementReferenceException
                            List<WebElement> currentFileList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                                    By.xpath("//div[contains(@class, 'icon-close hoverShow')]")));

                            if (!currentFileList.isEmpty()) {
                                // 总是删除第一个元素，因为每次删除后索引会变化
                                // 使用JavaScript执行点击操作，避免被检测为自动化程序
                                driver.executeScript("arguments[0].click();", currentFileList.get(0));
                                Thread.sleep(1500 + new Random().nextInt(1000)); // 随机延迟，模拟人工操作
                            }
                        }

                        WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                                By.xpath("//div[contains(@class,'img-list')]/div[1]/input[1]")));

                        // 处理多个文件上传
                        if (noteDO.getFiles() != null && !noteDO.getFiles().isEmpty()) {
                            List<String> tempFilePaths = new ArrayList<>();

                            // 为每个文件创建临时文件
                            for (String fileUrl : noteDO.getFiles()) {
                                try {
                                    // 下载文件内容
                                    byte[] fileContent = FileUtils.getFileContent(fileUrl);
                                    // 创建临时文件
                                    File tempFile = FileUtils.createTempFile(fileContent, ".png");
                                    tempFilePaths.add(tempFile.getAbsolutePath());
                                    log.info("创建临时文件: {}", tempFile.getAbsolutePath());
                                } catch (Exception e) {
                                    log.warn("下载或创建临时文件失败, fileUrl={}", fileUrl, e);
                                }
                            }

                            // 使用Selenium上传所有文件
                            if (!tempFilePaths.isEmpty()) {
                                // 将所有文件路径用换行符连接（适用于多文件上传）
                                String allFilePaths = String.join("\n", tempFilePaths);
                                fileInput.sendKeys(allFilePaths);
                                log.info("已发送文件路径到上传输入框，文件数量: {}", tempFilePaths.size());
                            }
                        }

                        // 等待一段时间确保文件上传完成
                        Thread.sleep(3000 + new Random().nextInt(2000));

                        // 重新定位元素，避免StaleElementReferenceException
                        List<WebElement> currentFileList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                                By.xpath("//div[contains(@class, 'icon-close hoverShow')]")));

                        // 检查是否有足够的文件需要删除（至少2个文件才能删除第一个并保留最后一个）
                        if (currentFileList.size() >= 2) {
                            // 删除第一个元素
                            // 使用JavaScript执行点击操作，避免被检测为自动化程序
                            driver.executeScript("arguments[0].click();", currentFileList.get(0));
                            Thread.sleep(2000 + new Random().nextInt(1500)); // 随机延迟，等待删除完成

                            log.info("删除第一个元素成功，当前文件数量: {}", currentFileList.size());
                        } else if (currentFileList.size() == 1) {
                            log.info("只有一个文件，无需删除");
                        } else {
                            log.info("没有找到可删除的文件");
                        }
                        log.info("笔记文件列表: {}", noteDO.getFiles());
                    } catch (Exception e) {
                        log.warn("更新笔记文件列表失败: {}", e.getMessage(), e);
                    }
                } else if (XhsEnum.VIDEO_NOTE.getCode().equals(noteDO.getType())){
                    // 更新封面
                    WebElement backgroundDiv = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//div[@class='default column']")));

                    // 2. 创建 Actions 对象
                    Actions actions = new Actions(driver);

                    // 3. 执行鼠标悬停操作
                    actions.moveToElement(backgroundDiv).perform();

                    // 4. 找到修改封面div
                    WebElement imageDiv = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//div[@class='operator default column center noCover pointer']")));
                    imageDiv.click();

                    // 找到input，并传递文件路径
                    WebElement imageInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//div[@id='workspace']/following-sibling::input[1]")
                    ));
                    try {
                        // 下载文件内容
                        byte[] fileContent = FileUtils.getFileContent(noteDO.getImage());
                        // 创建临时文件
                        File tempFile = FileUtils.createTempFile(fileContent, ".png");
                        log.info("创建临时文件: {}", tempFile.getAbsolutePath());
                        imageInput.sendKeys(tempFile.getAbsolutePath());

                        // 点击确定
                        WebElement confirmButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                                By.xpath("//span[text()=' 确定 ']")
                        ));
                        confirmButton.click();
                    } catch (Exception e) {
                        log.warn("下载或创建临时文件失败, fileUrl={}", noteDO.getImage(), e);
                    }

                    Thread.sleep(2000);
                    WebElement videoInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector(".upload-input")));
                    try {
                        // 下载文件内容
                        byte[] fileContent = FileUtils.getFileContent(noteDO.getFiles().get(0));
                        // 创建临时文件
                        File tempFile = FileUtils.createTempFile(fileContent, ".mp4");
                        log.info("创建临时视频: {}", tempFile.getAbsolutePath());
                        videoInput.sendKeys(tempFile.getAbsolutePath());
                        log.info("已发送视频路径到上传输入框，文件数量: {}", 1);

                        Thread.sleep(3000);

                        // 对于视频，等待发布按钮变为可点击即表示处理完成
                        SeleniumUtils.waitForPublishButtonClickable(driver, "//button[contains(@class, 'd-button d-button-large')]");
                    } catch (Exception e) {
                        log.warn("更新笔记文件列表失败, fileUrl={}", noteDO.getFiles().get(0), e);
                    }
                }

                // 开始保存
                try {
                    WebElement saveButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//span[text()='发布']")));
                    // 使用JavaScript执行点击操作，避免被检测为自动化程序
                    driver.executeScript("arguments[0].scrollIntoView(true);", saveButton);
                    Thread.sleep(500 + new Random().nextInt(500)); // 随机延迟
                    driver.executeScript("arguments[0].click();", saveButton);
                    Thread.sleep(3000 + new Random().nextInt(1000)); // 随机延迟，等待保存完成
                    log.info("保存笔记成功: platformNoteId={}", noteDO.getPlatformNoteId());
                } catch (Exception e) {
                    log.warn("保存笔记失败: {}", e.getMessage());
                }
            } catch (Exception e) {
                log.error("更新线上平台笔记详情失败: platformNoteId={}, error={}", noteDO.getPlatformNoteId(), e.getMessage());
            }
        } catch (Exception e) {
            log.error("更新线上平台笔记详情失败: platformNoteId={}, error={}", noteDO.getPlatformNoteId(), e.getMessage());
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

    @Override
    public PageResult<NoteDO> getNotePage(NotePageReqVO pageReqVO) {
        List<XhsUserDO> xhsUserDOS = xhsUserMapper.selectList();
        Map<Long, XhsUserDO> xhsUserDOMap = xhsUserDOS.stream().collect(Collectors.toMap(XhsUserDO::getId, xhsUserDO -> xhsUserDO));
        PageResult<NoteDO> noteDOPageResult = noteMapper.selectPage(pageReqVO);
        noteDOPageResult.getList().forEach(note -> {
            note.setUserName(xhsUserDOMap.get(note.getUserId()).getName());
        });
        return noteDOPageResult;
    }

}