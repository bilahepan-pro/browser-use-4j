package com.browseruse4j.impl;

import com.browseruse4j.core.BrowserController;
import com.browseruse4j.config.ConfigLoader;
import com.browseruse4j.utils.LoggerUtils;
import com.microsoft.playwright.*;
import org.slf4j.Logger;

import java.nio.file.Paths;

/**
 * Playwright浏览器控制器实现
 * 基于Playwright实现浏览器自动化操作
 */
public class PlaywrightBrowserController implements BrowserController {
    
    private static final Logger LOGGER = LoggerUtils.getLogger(PlaywrightBrowserController.class);
    
    // 配置常量
    private static final String CONFIG_BROWSER_HEADLESS = "browser.headless";
    private static final String CONFIG_BROWSER_TIMEOUT = "browser.timeout";
    private static final String CONFIG_SCREENSHOT_PATH = "browser.screenshot.path";
    
    private static final boolean DEFAULT_HEADLESS = true;
    private static final int DEFAULT_TIMEOUT = 30000;
    private static final String DEFAULT_SCREENSHOT_PATH = "screenshots";
    
    private final ConfigLoader configLoader;
    private Playwright playwright;
    private Browser browser;
    private Page page;
    
    public PlaywrightBrowserController() {
        this.configLoader = new ConfigLoader();
        initializeBrowser();
    }
    
    public PlaywrightBrowserController(final ConfigLoader configLoader) {
        this.configLoader = configLoader;
        initializeBrowser();
    }
    
    /**
     * 初始化浏览器
     */
    private void initializeBrowser() {
        try {
            LoggerUtils.info(LOGGER, "初始化Playwright浏览器...");
            
            playwright = Playwright.create();
            
            boolean headless = configLoader.getBoolean(CONFIG_BROWSER_HEADLESS, DEFAULT_HEADLESS);
            int timeout = configLoader.getInt(CONFIG_BROWSER_TIMEOUT, DEFAULT_TIMEOUT);
            
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                    .setHeadless(headless)
                    .setTimeout(timeout);
            
            browser = playwright.chromium().launch(launchOptions);
            page = browser.newPage();
            
            LoggerUtils.info(LOGGER, "Playwright浏览器初始化完成");
        } catch (Exception e) {
            LoggerUtils.error(LOGGER, "初始化Playwright浏览器失败", e);
            throw new RuntimeException("浏览器初始化失败", e);
        }
    }
    
    @Override
    public String getPageTitle(final String url) {
        try {
            LoggerUtils.info(LOGGER, "获取页面标题: " + url);
            // 如果当前页面URL与目标URL不同，则导航到目标页面
            if (!page.url().equals(url)) {
                navigateTo(url);
            }
            String title = page.title();
            LoggerUtils.info(LOGGER, "页面标题: " + title);
            return title;
        } catch (Exception e) {
            LoggerUtils.error(LOGGER, "获取页面标题失败: " + url, e);
            throw new RuntimeException("获取页面标题失败", e);
        }
    }
    
    /**
     * 获取当前页面标题（不导航）
     * @return 当前页面标题
     */
    public String getCurrentPageTitle() {
        try {
            String title = page.title();
            LoggerUtils.info(LOGGER, "当前页面标题: " + title);
            return title;
        } catch (Exception e) {
            LoggerUtils.error(LOGGER, "获取当前页面标题失败", e);
            throw new RuntimeException("获取当前页面标题失败", e);
        }
    }
    
    @Override
    public void navigateTo(final String url) {
        try {
            LoggerUtils.info(LOGGER, "导航到页面: " + url);
            page.navigate(url);
            page.waitForLoadState();
            LoggerUtils.info(LOGGER, "页面导航完成: " + url);
        } catch (Exception e) {
            LoggerUtils.error(LOGGER, "页面导航失败: " + url, e);
            throw new RuntimeException("页面导航失败", e);
        }
    }
    
    @Override
    public void clickElement(final String selector) {
        try {
            LoggerUtils.info(LOGGER, "点击元素: " + selector);
            page.click(selector);
            LoggerUtils.info(LOGGER, "元素点击完成: " + selector);
        } catch (Exception e) {
            LoggerUtils.error(LOGGER, "点击元素失败: " + selector, e);
            throw new RuntimeException("点击元素失败", e);
        }
    }
    
    @Override
    public void inputText(final String selector, final String text) {
        try {
            LoggerUtils.info(LOGGER, "输入文本到元素: " + selector + ", 文本: " + text);
            page.fill(selector, text);
            LoggerUtils.info(LOGGER, "文本输入完成: " + selector);
        } catch (Exception e) {
            LoggerUtils.error(LOGGER, "输入文本失败: " + selector, e);
            throw new RuntimeException("输入文本失败", e);
        }
    }
    
    @Override
    public void takeScreenshot(final String filePath) {
        try {
            LoggerUtils.info(LOGGER, "截图保存到: " + filePath);
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(filePath)));
            LoggerUtils.info(LOGGER, "截图完成: " + filePath);
        } catch (Exception e) {
            LoggerUtils.error(LOGGER, "截图失败: " + filePath, e);
            throw new RuntimeException("截图失败", e);
        }
    }
    
    @Override
    public void close() {
        try {
            LoggerUtils.info(LOGGER, "关闭浏览器...");
            
            if (page != null) {
                page.close();
            }
            
            if (browser != null) {
                browser.close();
            }
            
            if (playwright != null) {
                playwright.close();
            }
            
            LoggerUtils.info(LOGGER, "浏览器关闭完成");
        } catch (Exception e) {
            LoggerUtils.error(LOGGER, "关闭浏览器失败", e);
        }
    }
    
    /**
     * 获取当前页面对象（用于高级操作）
     * @return Page对象
     */
    public Page getPage() {
        return page;
    }
} 