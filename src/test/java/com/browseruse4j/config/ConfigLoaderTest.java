package com.browseruse4j.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ConfigLoader单元测试
 */
class ConfigLoaderTest {
    
    @Test
    void testGetStringWithDefaultValue() {
        // Given
        ConfigLoader configLoader = new ConfigLoader();
        String key = "non.existent.key";
        String defaultValue = "default";
        
        // When
        String result = configLoader.getString(key, defaultValue);
        
        // Then
        assertThat(result).isEqualTo(defaultValue);
    }
    
    @Test
    void testGetStringFromProperties() {
        // Given
        ConfigLoader configLoader = new ConfigLoader();
        String key = "browser.headless";
        String defaultValue = "false";
        
        // When
        String result = configLoader.getString(key, defaultValue);
        
        // Then
        assertThat(result).isEqualTo("true"); // 从application.properties读取
    }
    
    @Test
    void testGetIntWithDefaultValue() {
        // Given
        ConfigLoader configLoader = new ConfigLoader();
        String key = "non.existent.key";
        int defaultValue = 100;
        
        // When
        int result = configLoader.getInt(key, defaultValue);
        
        // Then
        assertThat(result).isEqualTo(defaultValue);
    }
    
    @Test
    void testGetIntFromProperties() {
        // Given
        ConfigLoader configLoader = new ConfigLoader();
        String key = "browser.timeout";
        int defaultValue = 10000;
        
        // When
        int result = configLoader.getInt(key, defaultValue);
        
        // Then
        assertThat(result).isEqualTo(30000); // 从application.properties读取
    }
    
    @Test
    void testGetBooleanWithDefaultValue() {
        // Given
        ConfigLoader configLoader = new ConfigLoader();
        String key = "non.existent.key";
        boolean defaultValue = false;
        
        // When
        boolean result = configLoader.getBoolean(key, defaultValue);
        
        // Then
        assertThat(result).isEqualTo(defaultValue);
    }
    
    @Test
    void testGetBooleanFromProperties() {
        // Given
        ConfigLoader configLoader = new ConfigLoader();
        String key = "browser.headless";
        boolean defaultValue = false;
        
        // When
        boolean result = configLoader.getBoolean(key, defaultValue);
        
        // Then
        assertThat(result).isTrue(); // 从application.properties读取
    }
    
    @Test
    void testGetIntWithInvalidValue() {
        // Given
        System.setProperty("test.invalid.int", "not_a_number");
        ConfigLoader configLoader = new ConfigLoader();
        String key = "test.invalid.int";
        int defaultValue = 42;
        
        try {
            // When
            int result = configLoader.getInt(key, defaultValue);
            
            // Then
            assertThat(result).isEqualTo(defaultValue);
        } finally {
            // Cleanup
            System.clearProperty("test.invalid.int");
        }
    }
} 