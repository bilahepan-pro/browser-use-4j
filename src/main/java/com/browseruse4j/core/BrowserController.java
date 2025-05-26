package com.browseruse4j.core;

/**
 * 浏览器控制器接口
 * 定义浏览器自动化的基本操作
 */
public interface BrowserController {
    
    /**
     * 获取页面标题
     * @param url 页面URL
     * @return 页面标题
     */
    String getPageTitle(String url);
    
    /**
     * 导航到指定URL
     * @param url 目标URL
     */
    void navigateTo(String url);
    
    /**
     * 点击元素
     * @param selector CSS选择器
     */
    void clickElement(String selector);
    
    /**
     * 输入文本
     * @param selector CSS选择器
     * @param text 要输入的文本
     */
    void inputText(String selector, String text);
    
    /**
     * 截图
     * @param filePath 保存路径
     */
    void takeScreenshot(String filePath);
    
    /**
     * 关闭浏览器
     */
    void close();
} 