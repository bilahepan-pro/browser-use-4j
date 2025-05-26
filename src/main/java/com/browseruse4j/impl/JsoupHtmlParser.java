package com.browseruse4j.impl;

import com.browseruse4j.core.HtmlParser;
import com.browseruse4j.utils.LoggerUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 基于Jsoup的HTML解析器实现
 */
public final class JsoupHtmlParser implements HtmlParser {
    
    private static final Logger LOGGER = LoggerUtils.getLogger(JsoupHtmlParser.class);
    
    @Override
    public ParseResult parseHtml(final String html) {
        if (html == null) {
            throw new IllegalArgumentException("HTML内容不能为空");
        }
        
        LoggerUtils.debug(LOGGER, "开始解析HTML，长度: " + html.length());
        
        try {
            Document document = Jsoup.parse(html);
            return new JsoupParseResult(document);
        } catch (Exception e) {
            LoggerUtils.error(LOGGER, "HTML解析失败", e);
            throw new RuntimeException("HTML解析失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Element> selectElements(final String html, final String selector) {
        if (html == null) {
            throw new IllegalArgumentException("HTML内容不能为空");
        }
        if (selector == null || selector.trim().isEmpty()) {
            throw new IllegalArgumentException("选择器不能为空");
        }
        
        LoggerUtils.debug(LOGGER, "选择元素，选择器: " + selector);
        
        try {
            Document document = Jsoup.parse(html);
            Elements elements = document.select(selector);
            
            return elements.stream()
                    .map(JsoupElement::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LoggerUtils.error(LOGGER, "元素选择失败", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public String extractText(final String html) {
        if (html == null) {
            throw new IllegalArgumentException("HTML内容不能为空");
        }
        
        try {
            Document document = Jsoup.parse(html);
            return document.text();
        } catch (Exception e) {
            LoggerUtils.error(LOGGER, "文本提取失败", e);
            return "";
        }
    }
    
    @Override
    public List<String> extractLinks(final String html) {
        if (html == null) {
            throw new IllegalArgumentException("HTML内容不能为空");
        }
        
        try {
            Document document = Jsoup.parse(html);
            Elements links = document.select("a[href]");
            
            return links.stream()
                    .map(link -> link.attr("href"))
                    .filter(href -> !href.isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LoggerUtils.error(LOGGER, "链接提取失败", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Jsoup解析结果实现
     */
    private static final class JsoupParseResult implements ParseResult {
        private final Document document;
        
        public JsoupParseResult(final Document document) {
            this.document = document;
        }
        
        @Override
        public String getTitle() {
            return document.title();
        }
        
        @Override
        public String getText() {
            return document.text();
        }
        
        @Override
        public List<Element> getElements() {
            return document.getAllElements().stream()
                    .map(JsoupElement::new)
                    .collect(Collectors.toList());
        }
        
        @Override
        public Map<String, String> getMetadata() {
            Map<String, String> metadata = new HashMap<>();
            
            // 提取meta标签
            document.select("meta[name]").forEach(meta -> {
                String name = meta.attr("name");
                String content = meta.attr("content");
                if (!name.isEmpty() && !content.isEmpty()) {
                    metadata.put(name, content);
                }
            });
            
            // 提取meta property标签（如Open Graph）
            document.select("meta[property]").forEach(meta -> {
                String property = meta.attr("property");
                String content = meta.attr("content");
                if (!property.isEmpty() && !content.isEmpty()) {
                    metadata.put(property, content);
                }
            });
            
            return metadata;
        }
    }
    
    /**
     * Jsoup元素实现
     */
    private static final class JsoupElement implements Element {
        private final org.jsoup.nodes.Element element;
        
        public JsoupElement(final org.jsoup.nodes.Element element) {
            this.element = element;
        }
        
        @Override
        public String getTagName() {
            return element.tagName();
        }
        
        @Override
        public String getText() {
            return element.text();
        }
        
        @Override
        public String getAttribute(final String name) {
            return element.attr(name);
        }
        
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> attributes = new HashMap<>();
            element.attributes().forEach(attr -> 
                attributes.put(attr.getKey(), attr.getValue())
            );
            return attributes;
        }
    }
} 