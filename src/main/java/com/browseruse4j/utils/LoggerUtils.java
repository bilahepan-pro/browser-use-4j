package com.browseruse4j.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类
 * 提供统一的日志记录功能
 */
public final class LoggerUtils {
    
    private LoggerUtils() {
        // 工具类，隐藏构造器
    }
    
    /**
     * 获取Logger实例
     * @param clazz 类
     * @return Logger实例
     */
    public static Logger getLogger(final Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
    
    /**
     * 记录信息日志
     * @param logger Logger实例
     * @param message 消息
     */
    public static void info(final Logger logger, final String message) {
        if (logger.isInfoEnabled()) {
            logger.info(message);
        }
    }
    
    /**
     * 记录错误日志
     * @param logger Logger实例
     * @param message 消息
     * @param throwable 异常
     */
    public static void error(final Logger logger, final String message, final Throwable throwable) {
        if (logger.isErrorEnabled()) {
            logger.error(message, throwable);
        }
    }
    
    /**
     * 记录调试日志
     * @param logger Logger实例
     * @param message 消息
     */
    public static void debug(final Logger logger, final String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }
} 