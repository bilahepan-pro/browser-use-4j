package com.browseruse4j.config;

import com.browseruse4j.utils.LoggerUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置加载器
 * 支持从环境变量和配置文件加载配置
 */
public final class ConfigLoader {
    
    private static final Logger LOGGER = LoggerUtils.getLogger(ConfigLoader.class);
    private static final String DEFAULT_CONFIG_FILE = "application.properties";
    
    private final Properties properties;
    
    public ConfigLoader() {
        this(DEFAULT_CONFIG_FILE);
    }
    
    public ConfigLoader(final String configFile) {
        this.properties = new Properties();
        loadProperties(configFile);
    }
    
    /**
     * 加载配置文件
     * @param configFile 配置文件名
     */
    private void loadProperties(final String configFile) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (inputStream != null) {
                properties.load(inputStream);
                LoggerUtils.info(LOGGER, "成功加载配置文件: " + configFile);
            } else {
                LoggerUtils.info(LOGGER, "配置文件不存在: " + configFile + "，使用默认配置");
            }
        } catch (IOException e) {
            LoggerUtils.error(LOGGER, "加载配置文件失败: " + configFile, e);
        }
    }
    
    /**
     * 获取字符串配置值
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public String getString(final String key, final String defaultValue) {
        // 优先从环境变量获取
        String envValue = System.getenv(key.toUpperCase().replace('.', '_'));
        if (envValue != null) {
            return envValue;
        }
        
        // 从系统属性获取
        String sysValue = System.getProperty(key);
        if (sysValue != null) {
            return sysValue;
        }
        
        // 从配置文件获取
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * 获取整数配置值
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public int getInt(final String key, final int defaultValue) {
        String value = getString(key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            LoggerUtils.error(LOGGER, "配置值不是有效整数: " + key + "=" + value, e);
            return defaultValue;
        }
    }
    
    /**
     * 获取布尔配置值
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public boolean getBoolean(final String key, final boolean defaultValue) {
        String value = getString(key, String.valueOf(defaultValue));
        return Boolean.parseBoolean(value);
    }
    
    /**
     * 获取双精度浮点数配置值
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public double getDouble(final String key, final double defaultValue) {
        String value = getString(key, String.valueOf(defaultValue));
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            LoggerUtils.error(LOGGER, "配置值不是有效双精度数: " + key + "=" + value, e);
            return defaultValue;
        }
    }
} 