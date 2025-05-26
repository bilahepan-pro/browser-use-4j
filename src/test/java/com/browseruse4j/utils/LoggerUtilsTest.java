package com.browseruse4j.utils;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LoggerUtils单元测试
 */
class LoggerUtilsTest {
    
    @Test
    void testGetLogger() {
        // Given
        Class<?> testClass = LoggerUtilsTest.class;
        
        // When
        Logger logger = LoggerUtils.getLogger(testClass);
        
        // Then
        assertThat(logger).isNotNull();
        assertThat(logger.getName()).isEqualTo(testClass.getName());
    }
    
    @Test
    void testInfoLogging() {
        // Given
        Logger logger = LoggerUtils.getLogger(LoggerUtilsTest.class);
        String message = "测试信息日志";
        
        // When & Then - 不应该抛出异常
        LoggerUtils.info(logger, message);
    }
    
    @Test
    void testErrorLogging() {
        // Given
        Logger logger = LoggerUtils.getLogger(LoggerUtilsTest.class);
        String message = "测试错误日志";
        Exception exception = new RuntimeException("测试异常");
        
        // When & Then - 不应该抛出异常
        LoggerUtils.error(logger, message, exception);
    }
    
    @Test
    void testDebugLogging() {
        // Given
        Logger logger = LoggerUtils.getLogger(LoggerUtilsTest.class);
        String message = "测试调试日志";
        
        // When & Then - 不应该抛出异常
        LoggerUtils.debug(logger, message);
    }
} 