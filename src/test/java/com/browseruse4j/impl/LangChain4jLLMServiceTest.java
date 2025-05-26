package com.browseruse4j.impl;

import com.browseruse4j.config.ConfigLoader;
import com.browseruse4j.core.LLMService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

/**
 * LangChain4jLLMService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class LangChain4jLLMServiceTest {

    @Mock
    private ConfigLoader configLoader;

    private LLMService llmService;

    @BeforeEach
    void setUp() {
        // Mock配置 - 使用lenient模式避免严格的stubbing检查
        lenient().when(configLoader.getString("llm.api.key", "")).thenReturn("test-api-key");
        lenient().when(configLoader.getString("llm.model.name", "gpt-3.5-turbo")).thenReturn("gpt-3.5-turbo");
        lenient().when(configLoader.getString("llm.base.url", "https://api.openai.com/v1")).thenReturn("https://api.openai.com/v1");
        lenient().when(configLoader.getInt("llm.timeout.seconds", 30)).thenReturn(30);
        lenient().when(configLoader.getDouble("llm.temperature", 0.7)).thenReturn(0.7);
        lenient().when(configLoader.getInt("llm.max.tokens", 1000)).thenReturn(1000);
        
        llmService = new LangChain4jLLMService(configLoader);
    }

    @Test
    void testConstructorWithValidConfig() {
        // 测试正常构造
        assertThat(llmService).isNotNull();
    }

    @Test
    void testConstructorWithEmptyApiKey() {
        // 测试API Key为空的情况
        when(configLoader.getString("llm.api.key", "")).thenReturn("");
        
        assertThatThrownBy(() -> new LangChain4jLLMService(configLoader))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("API Key不能为空");
    }

    @Test
    void testGenerateTextWithValidPrompt() {
        // 测试正常的文本生成
        String prompt = "Hello, how are you?";
        String response = llmService.generateText(prompt);
        
        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();
        assertThat(response).contains("Hello");
    }

    @Test
    void testGenerateTextWithEmptyPrompt() {
        // 测试空提示词
        assertThatThrownBy(() -> llmService.generateText(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("提示词不能为空");
    }

    @Test
    void testGenerateTextWithNullPrompt() {
        // 测试null提示词
        assertThatThrownBy(() -> llmService.generateText(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("提示词不能为空");
    }

    @Test
    void testIsAvailableWithValidConfig() {
        // 测试服务可用性检查
        // 在Mock环境下，我们假设服务可用
        boolean available = llmService.isAvailable();
        assertThat(available).isTrue();
    }

    @Test
    void testConstructorWithNullConfig() {
        // 测试null配置
        assertThatThrownBy(() -> new LangChain4jLLMService(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("配置加载器不能为空");
    }
} 