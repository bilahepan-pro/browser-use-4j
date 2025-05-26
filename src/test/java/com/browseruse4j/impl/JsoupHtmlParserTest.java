package com.browseruse4j.impl;

import com.browseruse4j.core.HtmlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * JsoupHtmlParser 单元测试
 */
class JsoupHtmlParserTest {

    private HtmlParser htmlParser;
    private String sampleHtml;

    @BeforeEach
    void setUp() {
        htmlParser = new JsoupHtmlParser();
        sampleHtml = "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
                "<title>测试页面</title>" +
                "<meta name=\"description\" content=\"这是一个测试页面\">" +
            "</head>" +
            "<body>" +
                "<h1 id=\"title\">欢迎</h1>" +
                "<p class=\"content\">这是段落内容</p>" +
                "<a href=\"https://example.com\">示例链接</a>" +
                "<a href=\"/internal\">内部链接</a>" +
                "<div>" +
                    "<span>嵌套文本</span>" +
                "</div>" +
            "</body>" +
            "</html>";
    }

    @Test
    void testParseHtml() {
        // 测试HTML解析
        HtmlParser.ParseResult result = htmlParser.parseHtml(sampleHtml);
        
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("测试页面");
        assertThat(result.getText()).contains("欢迎", "这是段落内容", "示例链接");
        assertThat(result.getMetadata()).containsKey("description");
        assertThat(result.getMetadata().get("description")).isEqualTo("这是一个测试页面");
    }

    @Test
    void testSelectElements() {
        // 测试CSS选择器
        List<HtmlParser.Element> elements = htmlParser.selectElements(sampleHtml, "p.content");
        
        assertThat(elements).hasSize(1);
        assertThat(elements.get(0).getTagName()).isEqualTo("p");
        assertThat(elements.get(0).getText()).isEqualTo("这是段落内容");
        assertThat(elements.get(0).getAttribute("class")).isEqualTo("content");
    }

    @Test
    void testSelectMultipleElements() {
        // 测试选择多个元素
        List<HtmlParser.Element> links = htmlParser.selectElements(sampleHtml, "a");
        
        assertThat(links).hasSize(2);
        assertThat(links.get(0).getAttribute("href")).isEqualTo("https://example.com");
        assertThat(links.get(1).getAttribute("href")).isEqualTo("/internal");
    }

    @Test
    void testExtractText() {
        // 测试文本提取
        String text = htmlParser.extractText(sampleHtml);
        
        assertThat(text).isNotNull();
        assertThat(text).contains("欢迎");
        assertThat(text).contains("这是段落内容");
        assertThat(text).contains("示例链接");
        assertThat(text).contains("嵌套文本");
        // 不应包含HTML标签
        assertThat(text).doesNotContain("<h1>");
        assertThat(text).doesNotContain("<p>");
    }

    @Test
    void testExtractLinks() {
        // 测试链接提取
        List<String> links = htmlParser.extractLinks(sampleHtml);
        
        assertThat(links).hasSize(2);
        assertThat(links).contains("https://example.com");
        assertThat(links).contains("/internal");
    }

    @Test
    void testParseEmptyHtml() {
        // 测试空HTML
        HtmlParser.ParseResult result = htmlParser.parseHtml("");
        
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEmpty();
        assertThat(result.getText()).isEmpty();
    }

    @Test
    void testParseNullHtml() {
        // 测试null HTML
        assertThatThrownBy(() -> htmlParser.parseHtml(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("HTML内容不能为空");
    }

    @Test
    void testSelectElementsWithInvalidSelector() {
        // 测试无效选择器
        List<HtmlParser.Element> elements = htmlParser.selectElements(sampleHtml, "nonexistent");
        
        assertThat(elements).isEmpty();
    }

    @Test
    void testSelectElementsWithNullSelector() {
        // 测试null选择器
        assertThatThrownBy(() -> htmlParser.selectElements(sampleHtml, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("选择器不能为空");
    }

    @Test
    void testExtractLinksFromHtmlWithoutLinks() {
        // 测试没有链接的HTML
        String htmlWithoutLinks = "<html><body><p>没有链接的页面</p></body></html>";
        List<String> links = htmlParser.extractLinks(htmlWithoutLinks);
        
        assertThat(links).isEmpty();
    }
} 