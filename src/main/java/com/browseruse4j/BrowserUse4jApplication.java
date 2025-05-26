package com.browseruse4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Browser Use 4J 应用程序主类
 * 
 * @author Browser Use 4J Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class BrowserUse4jApplication {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BrowserUse4jApplication.class);
    
    private BrowserUse4jApplication() {
        // 工具类，隐藏构造器
    }
    
    /**
     * 应用程序入口点
     * 
     * @param args 命令行参数
     */
    public static void main(final String[] args) {
        LOGGER.info("Browser Use 4J 应用程序启动中...");
        LOGGER.info("Java版本: {}", System.getProperty("java.version"));
        LOGGER.info("操作系统: {}", System.getProperty("os.name"));
        LOGGER.info("Browser Use 4J 应用程序启动完成！");
    }
} 