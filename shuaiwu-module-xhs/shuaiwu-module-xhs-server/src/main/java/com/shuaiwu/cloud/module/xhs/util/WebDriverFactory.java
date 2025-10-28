package com.shuaiwu.cloud.module.xhs.util;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * WebDriver工厂类，用于创建和配置ChromeDriver实例
 */
public class WebDriverFactory {

    /**
     * 创建并配置ChromeDriver实例
     * 使用WebDriverManager自动管理ChromeDriver版本
     * 自动检测系统Chrome浏览器位置
     *
     * @return 配置好的ChromeDriver实例
     */
    public static ChromeDriver createChromeDriver() {
        // 使用WebDriverManager自动管理ChromeDriver
        WebDriverManager.chromedriver().setup();

        // 配置Chrome选项
        ChromeOptions options = new ChromeOptions();

        // options.addArguments("--headless"); // 临时关闭无头模式，用于调试

        // 添加浏览器特征
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36");
        // 信任所有HTTPS证书（避免HTTPS请求拦截失败）
        options.addArguments("--ignore-certificate-errors");
        // 可选：禁用扩展，减少干扰
        options.addArguments("--disable-extensions");
        // 禁用自动化控制检测（部分网站会屏蔽自动化工具）
        options.addArguments("--disable-blink-features=AutomationControlled");
        // 创建ChromeDriver实例
        ChromeDriver driver = new ChromeDriver(options);
        
        // 设置等待时间
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));

        return driver;
    }
}