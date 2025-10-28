package com.shuaiwu.cloud.module.xhs.service.user;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.shuaiwu.cloud.framework.common.util.date.DateUtils;
import com.shuaiwu.cloud.framework.common.util.io.FileUtils;
import com.shuaiwu.cloud.framework.common.util.selenium.SeleniumUtils;
import com.shuaiwu.cloud.module.infra.api.file.FileApi;
import com.shuaiwu.cloud.module.xhs.controller.admin.note.vo.NoteSaveReqVO;
import com.shuaiwu.cloud.module.xhs.enums.XhsEnum;
import com.shuaiwu.cloud.module.xhs.util.WebDriverFactory;


import com.shuaiwu.cloud.module.xhs.service.note.NoteService;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shuaiwu.cloud.module.xhs.controller.admin.user.vo.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shuaiwu.cloud.module.xhs.dal.dataobject.user.XhsUserDO;
import com.shuaiwu.cloud.framework.common.pojo.PageResult;
import com.shuaiwu.cloud.framework.common.util.object.BeanUtils;

import com.shuaiwu.cloud.module.xhs.dal.mysql.user.XhsUserMapper;

import static com.shuaiwu.cloud.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.shuaiwu.cloud.module.xhs.enums.ErrorCodeConstants.*;

/**
 * 小红书-用户管理 Service 实现类
 *
 * @author ws
 */
@Service
@Validated
@Slf4j
public class XhsUserServiceImpl implements XhsUserService {

    @Resource
    private XhsUserMapper userMapper;
    @Autowired
    private NoteService noteService;
    @Resource
    private FileApi fileApi;

    @Override
    public Long createUser(XhsUserSaveReqVO createReqVO) {
        // 插入
        XhsUserDO user = BeanUtils.toBean(createReqVO, XhsUserDO.class);
        if (userMapper.selectByPhone(createReqVO.getPhone())) {
            throw exception(USER_IS_EXISTS);
        }
        userMapper.insert(user);

        // 返回
        return user.getId();
    }

    @Override
    public void updateUser(XhsUserSaveReqVO updateReqVO) {
        // 校验存在
        validateUserExists(updateReqVO.getId());
        // 更新
        XhsUserDO updateObj = BeanUtils.toBean(updateReqVO, XhsUserDO.class);
        userMapper.updateById(updateObj);
    }

    @Override
    public void deleteUser(Long id) {
        // 校验存在
        validateUserExists(id);
        // 删除
        userMapper.deleteById(id);
    }

    @Override
    public void deleteUserListByIds(List<Long> ids) {
        // 删除
        userMapper.deleteByIds(ids);
    }


    private void validateUserExists(Long id) {
        if (userMapper.selectById(id) == null) {
            throw exception(USER_NOT_EXISTS);
        }
    }

    @Override
    public XhsUserDO getUser(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public PageResult<XhsUserDO> getUserPage(XhsUserPageReqVO pageReqVO) {
        return userMapper.selectPage(pageReqVO);
    }

    @Override
    public XhsUserDO getUserByPhone(String phone) {
        // 创建查询条件
        LambdaQueryWrapper<XhsUserDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(XhsUserDO::getPhone, phone);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public XhsUserSaveReqVO loginXhs(XhsUserLoginReqVO reqVO) {
        ChromeDriver driver = null;
        try {
            // 使用WebDriverFactory创建ChromeDriver
            driver = WebDriverFactory.createChromeDriver();

            // 访问登录页面
            driver.get("https://creator.xiaohongshu.com/login");
            Thread.sleep(3000);

            // 输入手机号
            WebElement phoneInput = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='手机号']")));
            for (char digit : reqVO.getPhone().toCharArray()) {
                phoneInput.sendKeys(String.valueOf(digit));
                Thread.sleep(100 + new Random().nextInt(100));
            }

            // 输入验证码
            WebElement codeInput = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='验证码']")));
            for (char digit : reqVO.getVerifyCode().toCharArray()) {
                codeInput.sendKeys(String.valueOf(digit));
                Thread.sleep(100 + new Random().nextInt(100));
            }

            Thread.sleep(1000);

            // 点击登录按钮
            WebElement loginButton = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class, 'beer-login-btn')]")));
            driver.executeScript("arguments[0].scrollIntoView(true);", loginButton);
            Thread.sleep(500);
            loginButton.click();

            // 等待登录结果
            Thread.sleep(5000);

            // 检查是否成功跳转到首页
            if (Objects.requireNonNull(driver.getCurrentUrl()).contains("/new/home")) {
                // 获取登录后的cookie
                Set<Cookie> cookies = driver.manage().getCookies();
                // 将Cookie转换为JSON格式
                List<Map<String, Object>> cookieList = new ArrayList<>();
                for (Cookie cookie : cookies) {
                    Map<String, Object> cookieMap = new HashMap<>();
                    cookieMap.put("name", cookie.getName());
                    cookieMap.put("value", cookie.getValue());
                    cookieMap.put("domain", cookie.getDomain());
                    cookieMap.put("path", cookie.getPath());
                    cookieMap.put("expiry", cookie.getExpiry() != null ? cookie.getExpiry().getTime() : null);
                    cookieMap.put("secure", cookie.isSecure());
                    cookieMap.put("httpOnly", cookie.isHttpOnly());
                    cookieList.add(cookieMap);
                }

                // 转换为JSON字符串
                String cookieJson = new com.alibaba.fastjson.JSONObject()
                    .fluentPut("cookies", cookieList)
                    .toJSONString();

                // 获取登录后的用户名
                String userName = driver.findElement(By.className("account-name")).getText();
                String watchNum = driver.findElement(By.xpath("//span[text()=\"关注数\"]/preceding-sibling::span[1]")).getText();
                String fansNum = driver.findElement(By.xpath("//span[text()=\"粉丝数\"]/preceding-sibling::span[1]")).getText();
                String starsNum = driver.findElement(By.xpath("//span[text()=\"获赞与收藏\"]/preceding-sibling::span[1]")).getText();
                String xhsNo = driver.findElement(By.xpath("//div[contains(text(), \"小红书账号:\")]")).getText();
                String explainStr = driver.findElement(By.xpath("//div[contains(text(), \"小红书账号:\")]/following-sibling::div[2]")).getText();
                String statusImage = driver.findElement(By.xpath("//div[@class=\"account-name\"]/following-sibling::img[1]")).getAttribute("src");
                String image = driver.findElement(By.xpath("//div[@class=\"personal\"]/div/div/img")).getAttribute("src");

                // 更新用户Cookie和登录状态
                XhsUserSaveReqVO updateUser = new XhsUserSaveReqVO();
                updateUser.setName(userName);
                updateUser.setCookie(cookieJson);
                updateUser.setLoginStatus(XhsEnum.IS_LOGIN.getCode()); // 设置为已登录状态

                updateUser.setWatchNum(watchNum);
                updateUser.setFansNum(fansNum);
                updateUser.setStarsNum(starsNum);
                updateUser.setPlatformNo(xhsNo.split(":")[1].strip());
                updateUser.setExplainStr(explainStr);
                updateUser.setStatusImage(statusImage);
                updateUser.setImage(image);
                return updateUser;
            }

            return null;
        } catch (Exception e) {
            throw new RuntimeException("Selenium执行失败: " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    @Override
    public String getXhsVerifyCode(String phone) {
        ChromeDriver driver = null;
        try {
            // 使用WebDriverFactory创建ChromeDriver
            driver = WebDriverFactory.createChromeDriver();

            // 访问登录页面并等待页面完全加载
            driver.get("https://creator.xiaohongshu.com/login");
            Thread.sleep(3000); // 确保页面完全加载

            // 执行JavaScript移除navigator.webdriver标记
            driver.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

            // 查找并等待手机号输入框可交互
            WebElement phoneInput = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='手机号']")));
            // 模拟人工输入
            for (char digit : phone.toCharArray()) {
                phoneInput.sendKeys(String.valueOf(digit));
                Thread.sleep(100 + new Random().nextInt(100)); // 随机延迟模拟人工输入
            }

            Thread.sleep(1000);

            // 查找并等待验证码按钮可点击
            WebElement verifyCodeButton = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//div[text()='发送验证码']")));
            verifyCodeButton.click();

            Thread.sleep(3000);

            return "验证码已发送";
        } catch (Exception e) {
            throw new RuntimeException("获取验证码失败: " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    @Override
    public List<NoteSaveReqVO> syncUserNotes(XhsUserSyncNotesReqVO reqVO) {
        WebDriver driver = null;
        try {
            // 获取用户信息
            XhsUserDO user = this.getUserByPhone(reqVO.getPhone());
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            
            // 检查用户是否已登录
            if (!"1".equals(user.getLoginStatus()) || user.getCookie() == null) {
                throw new RuntimeException("用户未登录或Cookie不存在");
            }
            
            // 使用WebDriverFactory创建ChromeDriver
            driver = WebDriverFactory.createChromeDriver();
            
            // 先访问小红书主页
            driver.get("https://creator.xiaohongshu.com");
            Thread.sleep(2000);
            
            // 解析并添加Cookie
            JSONObject cookieJson = JSON.parseObject(user.getCookie());
            JSONArray cookiesArray = cookieJson.getJSONArray("cookies");
            
            log.info("数据库中的Cookie数量: {}", cookiesArray.size());
            
            // 记录需要添加的Cookie
            List<Cookie> cookiesToAdd = new ArrayList<>();

            for (int i = 0; i < cookiesArray.size(); i++) {
                JSONObject cookieObj = cookiesArray.getJSONObject(i);
                // 创建Cookie对象并添加到列表
                Cookie cookie = new Cookie(
                        cookieObj.getString("name"),
                        cookieObj.getString("value"),
                    cookieObj.getString("domain"),
                    cookieObj.getString("path"),
                    cookieObj.getDate("expiry"),
                    cookieObj.getBoolean("secure"),
                    cookieObj.getBoolean("httpOnly")
                );
                cookiesToAdd.add(cookie);
            }
            
            // 添加所有Cookie到浏览器
            for (Cookie cookie : cookiesToAdd) {
                driver.manage().addCookie(cookie);
            }
            
            // 访问作者创作平台的主页
            driver.get("https://creator.xiaohongshu.com/new/home");
            Thread.sleep(5000); // 等待页面加载
            
            // 创建WebDriverWait实例
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.pollingEvery(Duration.ofSeconds(1));
            
            // 等待页面完全加载
            wait.until(webDriver -> Objects.equals(((ChromeDriver) webDriver)
                    .executeScript("return document.readyState"), "complete"));
            
            String userName = driver.findElement(By.className("account-name")).getText();
            log.info("【{}】使用Cookie登录成功!", userName);
            
            // 访问笔记管理页面
            driver.get("https://creator.xiaohongshu.com/new/note-manager");
            Thread.sleep(5000); // 等待页面加载
            
            // 等待并获取内容容器
            WebElement contentContainer = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[@class='content']")));
            
            // 确保内容容器可见
            wait.until(ExpectedConditions.visibilityOf(contentContainer));
            
            log.info("笔记列表容器加载完成，开始加载更多内容");
            
            // 下拉加载更多内容，确保每次滚动后等待新内容加载
            SeleniumUtils.handleScrollLoad(driver, ".content", ".note", 30);
            
            // 等待并获取所有作品元素
            List<WebElement> noteElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.xpath("//div[@class='note']")));
            
            // 确保找到了笔记元素
            if (noteElements.isEmpty()) {
                log.warn("未找到任何笔记元素");
                return null;
            }
            
            log.info("成功加载笔记列表，共找到 {} 个笔记", noteElements.size());
            
            // 创建笔记列表存储对象
            List<NoteSaveReqVO> noteList = new ArrayList<>();
            
            // 遍历处理每个笔记元素
            for (WebElement noteElement : noteElements) {
                NoteSaveReqVO n = new NoteSaveReqVO();
                n.setUserId(user.getId());
                try {
                    // 等待并获取平台noteId
                    String dataImpression = wait.until(d -> noteElement.getAttribute("data-impression"));
                    JSONObject entries = JSONObject.parseObject(dataImpression);
                    JSONObject noteTarget = entries.getJSONObject("noteTarget");
                    JSONObject noteObj = noteTarget.getJSONObject("value");
                    String noteId = noteObj.getString("noteId");
                    n.setPlatformNoteId(noteId);

                    // 等待并获取标题
                    WebElement titleElement = wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(
                            noteElement, By.cssSelector(".info > div > div")));
                    n.setName(titleElement.getText());

                    // 等待并获取封面图
                    WebElement imageElement = wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(
                            noteElement, By.cssSelector("img.content")));

                    String imageUrl = imageElement.getAttribute("src");
                    if (StrUtil.isNotBlank(imageUrl)) {
                        String file = fileApi.createFile(FileUtils.getFileContent(imageUrl));
                        n.setImage(file);
                    }

                    // 等待并获取发布时间
                    WebElement timeElement = wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(
                            noteElement, By.cssSelector(".time_status > div")));
                    String releaseTime = timeElement.getText();
                    // 发布于 2024年05月20日 11:09
                    String[] s = releaseTime.strip().split(" ");
                    LocalDateTime time = DateUtils.of(DateUtils.formatDate(String.join(" ", s[1], s[2]), "yyyy年MM月dd日 HH:mm"));
                    n.setReleaseTime(time);

                    // 检查是否是视频笔记
                    try {
                        wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(
                                noteElement, By.cssSelector("img.play")));
                        n.setType(XhsEnum.VIDEO_NOTE.getCode());
                    } catch (Exception e) {
                        n.setType(XhsEnum.IMAGE_NOTE.getCode());
                    }

                    // 获取所有统计图标
                    List<WebElement> iconList = noteElement.findElements(By.cssSelector(".icon"));

                    if (iconList.size() == 5) {
                        try {
                            for (int i = 0; i < 5; i++) {
                                WebElement currentIcon = iconList.get(i);
                                // 等待每个图标中的span元素加载完成
                                WebElement iconSpan = wait.until(ExpectedConditions.visibilityOf(
                                        currentIcon.findElement(By.cssSelector("span"))));

                                // 获取并清理数字文本
                                String value = iconSpan.getText().replaceAll("[^0-9]", "");
                                if (value.isEmpty()) {
                                    log.warn("图标数值为空，跳过解析: index={}, title={}", i, n.getName());
                                    continue;
                                }

                                int numValue = Integer.parseInt(value);
                                switch (i) {
                                    case 0 -> n.setViews(numValue);        // 浏览量
                                    case 1 -> n.setComments(numValue);     // 评论数
                                    case 2 -> n.setLikes(numValue);        // 点赞数
                                    case 3 -> n.setCollections(numValue);  // 收藏数
                                    case 4 -> n.setForwards(numValue);     // 转发数
                                }
                            }
                        } catch (NumberFormatException e) {
                            log.warn("解析数字失败，笔记标题: {}, 错误: {}", n.getName(), e.getMessage());
                        }
                    }

                    noteList.add(n);
                    log.info("成功解析笔记: {}", JSONUtil.toJsonStr(n));
                } catch (Exception e) {
                    log.error("解析笔记失败: {}", e.getMessage());
                }
            }
            log.info("解析完成，共解析【{}】个作品", noteList.size());
            return noteList;
        } catch (Exception e) {
            log.error("同步作品失败: {}", e.getMessage());
            throw new RuntimeException("同步作品失败: " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    @Override
    public List<XhsUserDO> getUserList() {
        return userMapper.selectList();
    }

}