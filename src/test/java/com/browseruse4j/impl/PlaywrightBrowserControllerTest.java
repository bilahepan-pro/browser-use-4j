package com.browseruse4j.impl;

import com.browseruse4j.config.ConfigLoader;
import com.microsoft.playwright.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * PlaywrightBrowserController单元测试
 */
@ExtendWith(MockitoExtension.class)
class PlaywrightBrowserControllerTest {
    
    @Mock
    private ConfigLoader configLoader;
    
    @Mock
    private Playwright playwright;
    
    @Mock
    private BrowserType browserType;
    
    @Mock
    private Browser browser;
    
    @Mock
    private Page page;
    
    private PlaywrightBrowserController controller;
    
    @BeforeEach
    void setUp() {
        // 配置mock行为
        when(configLoader.getBoolean("browser.headless", true)).thenReturn(true);
        when(configLoader.getInt("browser.timeout", 30000)).thenReturn(30000);
        
        when(playwright.chromium()).thenReturn(browserType);
        when(browserType.launch(any(BrowserType.LaunchOptions.class))).thenReturn(browser);
        when(browser.newPage()).thenReturn(page);
    }
    
    @Test
    void testConstructorWithConfigLoader() {
        // Given & When
        try (MockedStatic<Playwright> playwrightMock = mockStatic(Playwright.class)) {
            playwrightMock.when(Playwright::create).thenReturn(playwright);
            
            controller = new PlaywrightBrowserController(configLoader);
            
            // Then
            assertThat(controller).isNotNull();
            verify(configLoader).getBoolean("browser.headless", true);
            verify(configLoader).getInt("browser.timeout", 30000);
        }
    }
    
    @Test
    void testNavigateTo() {
        // Given
        try (MockedStatic<Playwright> playwrightMock = mockStatic(Playwright.class)) {
            playwrightMock.when(Playwright::create).thenReturn(playwright);
            controller = new PlaywrightBrowserController(configLoader);
            
            String url = "https://example.com";
            
            // When
            controller.navigateTo(url);
            
            // Then
            verify(page).navigate(url);
            verify(page).waitForLoadState();
        }
    }
    
    @Test
    void testGetPageTitle() {
        // Given
        try (MockedStatic<Playwright> playwrightMock = mockStatic(Playwright.class)) {
            playwrightMock.when(Playwright::create).thenReturn(playwright);
            controller = new PlaywrightBrowserController(configLoader);
            
            String url = "https://example.com";
            String expectedTitle = "Example Domain";
            when(page.title()).thenReturn(expectedTitle);
            
            // When
            String actualTitle = controller.getPageTitle(url);
            
            // Then
            assertThat(actualTitle).isEqualTo(expectedTitle);
            verify(page).navigate(url);
            verify(page).title();
        }
    }
    
    @Test
    void testClickElement() {
        // Given
        try (MockedStatic<Playwright> playwrightMock = mockStatic(Playwright.class)) {
            playwrightMock.when(Playwright::create).thenReturn(playwright);
            controller = new PlaywrightBrowserController(configLoader);
            
            String selector = "#button";
            
            // When
            controller.clickElement(selector);
            
            // Then
            verify(page).click(selector);
        }
    }
    
    @Test
    void testInputText() {
        // Given
        try (MockedStatic<Playwright> playwrightMock = mockStatic(Playwright.class)) {
            playwrightMock.when(Playwright::create).thenReturn(playwright);
            controller = new PlaywrightBrowserController(configLoader);
            
            String selector = "#input";
            String text = "test text";
            
            // When
            controller.inputText(selector, text);
            
            // Then
            verify(page).fill(selector, text);
        }
    }
    
    @Test
    void testTakeScreenshot() {
        // Given
        try (MockedStatic<Playwright> playwrightMock = mockStatic(Playwright.class)) {
            playwrightMock.when(Playwright::create).thenReturn(playwright);
            controller = new PlaywrightBrowserController(configLoader);
            
            String filePath = "screenshot.png";
            
            // When
            controller.takeScreenshot(filePath);
            
            // Then
            verify(page).screenshot(any(Page.ScreenshotOptions.class));
        }
    }
    
    @Test
    void testClose() {
        // Given
        try (MockedStatic<Playwright> playwrightMock = mockStatic(Playwright.class)) {
            playwrightMock.when(Playwright::create).thenReturn(playwright);
            controller = new PlaywrightBrowserController(configLoader);
            
            // When
            controller.close();
            
            // Then
            verify(page).close();
            verify(browser).close();
            verify(playwright).close();
        }
    }
    
    @Test
    void testGetPage() {
        // Given
        try (MockedStatic<Playwright> playwrightMock = mockStatic(Playwright.class)) {
            playwrightMock.when(Playwright::create).thenReturn(playwright);
            controller = new PlaywrightBrowserController(configLoader);
            
            // When
            Page actualPage = controller.getPage();
            
            // Then
            assertThat(actualPage).isEqualTo(page);
        }
    }
    
    @Test
    void testNavigateToWithException() {
        // Given
        try (MockedStatic<Playwright> playwrightMock = mockStatic(Playwright.class)) {
            playwrightMock.when(Playwright::create).thenReturn(playwright);
            controller = new PlaywrightBrowserController(configLoader);
            
            String url = "invalid-url";
            doThrow(new RuntimeException("Navigation failed")).when(page).navigate(url);
            
            // When & Then
            assertThatThrownBy(() -> controller.navigateTo(url))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("页面导航失败");
        }
    }
} 