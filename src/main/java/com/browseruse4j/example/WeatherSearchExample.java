package com.browseruse4j.example;

import com.browseruse4j.config.ConfigLoader;
import com.browseruse4j.core.BrowserController;
import com.browseruse4j.core.HtmlParser;
import com.browseruse4j.core.LLMService;
import com.browseruse4j.impl.JsoupHtmlParser;
import com.browseruse4j.impl.LangChain4jLLMService;
import com.browseruse4j.impl.PlaywrightBrowserController;
import com.browseruse4j.utils.LoggerUtils;
import org.slf4j.Logger;

/**
 * 天气搜索示例
 * 演示使用浏览器搜索杭州天气并使用LLM分析结果
 */
public final class WeatherSearchExample {
    
    private static final Logger LOGGER = LoggerUtils.getLogger(WeatherSearchExample.class);
    
    private WeatherSearchExample() {
        // 工具类，隐藏构造器
    }
    
    /**
     * 主方法
     * @param args 命令行参数
     */
    public static void main(final String[] args) {
        LoggerUtils.info(LOGGER, "开始天气搜索示例...");
        
        // 初始化配置
        ConfigLoader configLoader = new ConfigLoader();
        
        // 初始化组件
        BrowserController browserController = null;
        LLMService llmService = null;
        HtmlParser htmlParser = new JsoupHtmlParser();
        
        try {
            // 初始化浏览器控制器
            browserController = new PlaywrightBrowserController(configLoader);
            LoggerUtils.info(LOGGER, "浏览器控制器初始化完成");
            
            // 初始化LLM服务
            llmService = new LangChain4jLLMService(configLoader);
            LoggerUtils.info(LOGGER, "LLM服务初始化完成");
            
            // 执行天气搜索
            searchHangzhouWeather(browserController, htmlParser, llmService);
            
        } catch (Exception e) {
            LoggerUtils.error(LOGGER, "天气搜索示例执行失败", e);
        } finally {
            // 清理资源
            if (browserController != null) {
                try {
                    browserController.close();
                    LoggerUtils.info(LOGGER, "浏览器已关闭");
                } catch (Exception e) {
                    LoggerUtils.error(LOGGER, "关闭浏览器失败", e);
                }
            }
        }
        
        LoggerUtils.info(LOGGER, "天气搜索示例完成");
    }
    
    /**
     * 搜索杭州天气
     * @param browserController 浏览器控制器
     * @param htmlParser HTML解析器
     * @param llmService LLM服务
     */
    private static void searchHangzhouWeather(final BrowserController browserController,
                                            final HtmlParser htmlParser,
                                            final LLMService llmService) {
        try {
            LoggerUtils.info(LOGGER, "开始搜索杭州天气...");
            
            // 1. 打开Google搜索页面
            String googleUrl = "https://www.google.com";
            browserController.navigateTo(googleUrl);
            LoggerUtils.info(LOGGER, "已打开Google搜索页面");
            
            // 等待页面加载
            Thread.sleep(2000);
            
            // 2. 在搜索框中输入"杭州天气"
            String searchQuery = "杭州天气";
            browserController.inputText("textarea[name='q']", searchQuery);
            LoggerUtils.info(LOGGER, "已输入搜索关键词: " + searchQuery);
            
            // 等待输入完成
            Thread.sleep(1000);
            
            // 3. 点击搜索按钮或按回车
            browserController.clickElement("input[name='btnK']");
            LoggerUtils.info(LOGGER, "已点击搜索按钮");
            
            // 等待搜索结果加载
            Thread.sleep(3000);
            
            // 4. 获取页面标题
            String pageTitle;
            if (browserController instanceof PlaywrightBrowserController) {
                pageTitle = ((PlaywrightBrowserController) browserController).getCurrentPageTitle();
            } else {
                // 对于其他实现，使用当前URL
                pageTitle = browserController.getPageTitle(googleUrl);
            }
            LoggerUtils.info(LOGGER, "搜索结果页面标题: " + pageTitle);
            
            // 5. 截图保存
            String screenshotPath = "hangzhou_weather_search.png";
            browserController.takeScreenshot(screenshotPath);
            LoggerUtils.info(LOGGER, "已保存搜索结果截图: " + screenshotPath);
            
            // 6. 获取页面内容并解析
            // 注意：在实际实现中，我们需要获取页面HTML内容
            // 这里我们模拟一些天气信息
            String weatherInfo = extractWeatherInfo();
            LoggerUtils.info(LOGGER, "提取的天气信息: " + weatherInfo);
            
            // 7. 使用LLM分析天气信息
            String prompt = "请分析以下杭州天气信息，并给出简洁的天气总结和建议：\n" + weatherInfo;
            
            if (llmService.isAvailable()) {
                String analysis = llmService.generateText(prompt);
                LoggerUtils.info(LOGGER, "LLM分析结果: " + analysis);
                
                // 输出最终结果
                System.out.println("\n=== 杭州天气搜索结果 ===");
                System.out.println("搜索页面: " + pageTitle);
                System.out.println("天气信息: " + weatherInfo);
                System.out.println("AI分析: " + analysis);
                System.out.println("截图保存: " + screenshotPath);
                System.out.println("========================\n");
            } else {
                LoggerUtils.info(LOGGER, "LLM服务不可用，跳过分析步骤");
                System.out.println("\n=== 杭州天气搜索结果 ===");
                System.out.println("搜索页面: " + pageTitle);
                System.out.println("天气信息: " + weatherInfo);
                System.out.println("截图保存: " + screenshotPath);
                System.out.println("注意: LLM服务不可用");
                System.out.println("========================\n");
            }
            
        } catch (Exception e) {
            LoggerUtils.error(LOGGER, "搜索杭州天气失败", e);
            throw new RuntimeException("搜索杭州天气失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 模拟提取天气信息
     * 在实际实现中，这里会解析真实的页面内容
     * @return 天气信息
     */
    private static String extractWeatherInfo() {
        // 模拟从搜索结果中提取的天气信息
        return "杭州今日天气：晴转多云，气温15-25°C，东南风3-4级，湿度65%，空气质量良好。" +
               "明日天气：多云转阴，气温12-22°C，可能有小雨。";
    }
} 