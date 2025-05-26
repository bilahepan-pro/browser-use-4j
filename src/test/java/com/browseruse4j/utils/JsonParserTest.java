package com.browseruse4j.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JsonParser单元测试
 */
class JsonParserTest {
    
    @Test
    void testToJson() {
        // Given
        TestObject testObject = new TestObject("test", 123);
        
        // When
        String json = JsonParser.toJson(testObject);
        
        // Then
        assertThat(json).isNotNull();
        assertThat(json).contains("test");
        assertThat(json).contains("123");
    }
    
    @Test
    void testFromJson() {
        // Given
        String json = "{\"name\":\"test\",\"value\":123}";
        
        // When
        TestObject result = JsonParser.fromJson(json, TestObject.class);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("test");
        assertThat(result.getValue()).isEqualTo(123);
    }
    
    @Test
    void testFromJsonWithInvalidJson() {
        // Given
        String invalidJson = "invalid json";
        
        // When
        TestObject result = JsonParser.fromJson(invalidJson, TestObject.class);
        
        // Then
        assertThat(result).isNull();
    }
    
    @Test
    void testIsValidJson() {
        // Given
        String validJson = "{\"name\":\"test\"}";
        String invalidJson = "invalid json";
        
        // When & Then
        assertThat(JsonParser.isValidJson(validJson)).isTrue();
        assertThat(JsonParser.isValidJson(invalidJson)).isFalse();
    }
    
    /**
     * 测试用的简单对象
     */
    public static class TestObject {
        private String name;
        private int value;
        
        public TestObject() {
        }
        
        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public int getValue() {
            return value;
        }
        
        public void setValue(int value) {
            this.value = value;
        }
    }
} 