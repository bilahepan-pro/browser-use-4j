package com.browseruse4j.core;

import java.util.List;
import java.util.Map;

/**
 * HTML解析器接口
 * 提供网页内容解析功能
 */
public interface HtmlParser {
    
    /**
     * 解析HTML文档
     * @param html HTML内容
     * @return 解析结果
     */
    ParseResult parseHtml(String html);
    
    /**
     * 提取指定选择器的元素
     * @param html HTML内容
     * @param selector CSS选择器
     * @return 元素列表
     */
    List<Element> selectElements(String html, String selector);
    
    /**
     * 提取文本内容
     * @param html HTML内容
     * @return 纯文本内容
     */
    String extractText(String html);
    
    /**
     * 提取所有链接
     * @param html HTML内容
     * @return 链接列表
     */
    List<String> extractLinks(String html);
    
    /**
     * 解析结果
     */
    interface ParseResult {
        String getTitle();
        String getText();
        List<Element> getElements();
        Map<String, String> getMetadata();
    }
    
    /**
     * HTML元素
     */
    interface Element {
        String getTagName();
        String getText();
        String getAttribute(String name);
        Map<String, String> getAttributes();
    }
} 