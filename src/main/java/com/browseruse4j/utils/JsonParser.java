package com.browseruse4j.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

/**
 * JSON解析工具类
 * 提供JSON处理功能
 */
public final class JsonParser {
    
    private static final Logger LOGGER = LoggerUtils.getLogger(JsonParser.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    private JsonParser() {
        // 工具类，隐藏构造器
    }
    
    /**
     * 对象转JSON字符串
     * @param object 对象
     * @return JSON字符串
     */
    public static String toJson(final Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LoggerUtils.error(LOGGER, "对象转JSON失败", e);
            return "{}";
        }
    }
    
    /**
     * JSON字符串转对象
     * @param json JSON字符串
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 对象实例
     */
    public static <T> T fromJson(final String json, final Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            LoggerUtils.error(LOGGER, "JSON转对象失败", e);
            return null;
        }
    }
    
    /**
     * 检查字符串是否为有效JSON
     * @param json JSON字符串
     * @return 是否有效
     */
    public static boolean isValidJson(final String json) {
        try {
            OBJECT_MAPPER.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }
} 