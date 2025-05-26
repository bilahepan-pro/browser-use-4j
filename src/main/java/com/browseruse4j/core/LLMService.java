package com.browseruse4j.core;

/**
 * 大模型服务接口
 * 定义LLM调用的基本操作
 */
public interface LLMService {
    
    /**
     * 生成文本
     * @param prompt 提示词
     * @return 生成的文本
     */
    String generateText(String prompt);
    
    /**
     * 检查服务是否可用
     * @return 是否可用
     */
    boolean isAvailable();
} 