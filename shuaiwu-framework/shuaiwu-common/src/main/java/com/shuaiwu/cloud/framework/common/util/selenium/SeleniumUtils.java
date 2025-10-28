package com.shuaiwu.cloud.framework.common.util.selenium;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * selenium 工具类
 */
@Slf4j
public class SeleniumUtils {

    // 最大等待时间：10分钟（与 Go 一致）
    private static final long MAX_WAIT_MINUTES = 2;
    // 循环检查间隔：1秒（与 Go 一致）
    private static final long CHECK_INTERVAL_SECONDS = 1;

    /**
     * 处理滚动加载，直到无新内容加载
     * @param driver
     * @param targetElement 滚动容器
     * @param itemSelector 列表项的CSS选择器
     * @param timeoutSeconds 超时时间（单位秒）
     */
    public static void handleScrollLoad(WebDriver driver, String targetElement, String itemSelector, int timeoutSeconds) {
        // 获取滚动容器元素
        WebElement container = driver.findElement(By.cssSelector(targetElement));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(1));

        int currentItemCount;
        long endTime = System.currentTimeMillis() + timeoutSeconds * 1000L;

        while (System.currentTimeMillis() < endTime) {
            // 获取当前列表项数量
            List<WebElement> items = container.findElements(By.cssSelector(itemSelector));
            currentItemCount = items.size();

            // 记录当前数量并滚动到底部
            js.executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", container);

            // 等待新内容加载
            try {
                // 等待列表项数量增加或超时
                int finalCurrentItemCount = currentItemCount;
                wait.until(d -> d.findElements(By.cssSelector(itemSelector)).size() > finalCurrentItemCount);
            } catch (Exception e) {
                break;
            }
        }
    }

    public static WebElement waitForPublishButtonClickable(WebDriver driver, String xPath) throws Exception {
        // 记录开始时间，用于计算等待时长
        long startTime = System.currentTimeMillis();
        // 目标按钮的 CSS 选择器（与 Go 中的 "button.publishBtn" 一致）
        // 最大等待时间（毫秒）：10分钟 = 10 * 60 * 1000
        long maxWaitMillis = Duration.ofMinutes(MAX_WAIT_MINUTES).toMillis();

        log.info("开始等待按钮可点击");

        // 循环检查：直至超时或找到可点击的按钮
        while (System.currentTimeMillis() - startTime < maxWaitMillis) {
            WebElement button = null;
            try {
                // 1. 查找按钮元素（对应 Go 的 page.Element(selector)）
                button = driver.findElement(By.xpath(xPath));
            } catch (Exception e) {
                // 元素未找到时，忽略异常，继续循环等待
                TimeUnit.SECONDS.sleep(CHECK_INTERVAL_SECONDS);
                continue;
            }

            // 2. 检查按钮是否可见（对应 Go 的 btn.Visible()）
            if (!button.isDisplayed()) {
                TimeUnit.SECONDS.sleep(CHECK_INTERVAL_SECONDS);
                continue;
            }

            // 3. 检查按钮是否禁用（先判断 disabled 属性，对应 Go 的 btn.Attribute("disabled")）
            String disabledAttr = button.getAttribute("disabled");
            // disabled 属性存在且为 "true" 或 ""（HTML 中 disabled 只要存在就代表禁用），则跳过
            if (Objects.nonNull(disabledAttr) && (disabledAttr.equals("true") || disabledAttr.isEmpty())) {
                TimeUnit.SECONDS.sleep(CHECK_INTERVAL_SECONDS);
                continue;
            }

            // 4. 检查 class 是否包含 "disabled"（对应 Go 的 strings.Contains(*cls, "disabled")）
            String buttonClass = button.getAttribute("class");
            if (Objects.nonNull(buttonClass) && buttonClass.contains("disabled")) {
                // 即使 class 含 disabled，但无 disabled 属性，仍按 Go 逻辑返回按钮（尝试点击）
                log.warn("按钮 class 含 'disabled'，但无 disabled 属性，尝试返回按钮");
                return button;
            }

            // 所有条件满足：按钮存在、可见、非禁用
            log.info("发布按钮已可点击");
            return button;
        }

        // 循环结束仍未找到，抛出超时异常
        throw new Exception("等待按钮可点击超时（最大等待时间：" + MAX_WAIT_MINUTES + "分钟）");
    }
}
