package com.browseruse4j.impl;

import com.browseruse4j.config.ConfigLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * SimpleTaskScheduler单元测试
 */
@ExtendWith(MockitoExtension.class)
class SimpleTaskSchedulerTest {
    
    @Mock
    private ConfigLoader configLoader;
    
    private SimpleTaskScheduler scheduler;
    
    @BeforeEach
    void setUp() {
        when(configLoader.getInt("task.pool.size", 10)).thenReturn(4);
        when(configLoader.getInt("task.timeout", 60000)).thenReturn(5000);
        
        scheduler = new SimpleTaskScheduler(configLoader);
    }
    
    @AfterEach
    void tearDown() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
    
    @Test
    void testConstructorWithDefaultConfig() {
        // Given & When
        SimpleTaskScheduler defaultScheduler = new SimpleTaskScheduler();
        
        try {
            // Then
            assertThat(defaultScheduler).isNotNull();
            assertThat(defaultScheduler.isShutdown()).isFalse();
        } finally {
            defaultScheduler.shutdown();
        }
    }
    
    @Test
    void testExecuteAsync() throws Exception {
        // Given
        AtomicInteger counter = new AtomicInteger(0);
        Runnable task = counter::incrementAndGet;
        
        // When
        CompletableFuture<Void> future = scheduler.executeAsync(task);
        
        // Then
        future.get(1, TimeUnit.SECONDS);
        assertThat(counter.get()).isEqualTo(1);
    }
    
    @Test
    void testExecuteAsyncWithException() throws Exception {
        // Given
        Runnable failingTask = () -> {
            throw new RuntimeException("Test exception");
        };
        
        // When
        CompletableFuture<Void> future = scheduler.executeAsync(failingTask);
        
        // Then
        assertThat(future.isCompletedExceptionally()).isTrue();
    }
    
    @Test
    void testExecuteAsyncTasks() throws Exception {
        // Given
        AtomicInteger counter = new AtomicInteger(0);
        List<Runnable> tasks = Arrays.asList(
                counter::incrementAndGet,
                counter::incrementAndGet,
                counter::incrementAndGet
        );
        
        // When
        CompletableFuture<Void> future = scheduler.executeAsyncTasks(tasks);
        
        // Then
        future.get(2, TimeUnit.SECONDS);
        assertThat(counter.get()).isEqualTo(3);
    }
    
    @Test
    void testExecuteAsyncTasksWithEmptyList() throws Exception {
        // Given
        List<Runnable> emptyTasks = Collections.emptyList();
        
        // When
        CompletableFuture<Void> future = scheduler.executeAsyncTasks(emptyTasks);
        
        // Then
        future.get(1, TimeUnit.SECONDS);
        assertThat(future.isDone()).isTrue();
        assertThat(future.isCompletedExceptionally()).isFalse();
    }
    
    @Test
    void testExecuteAsyncTasksWithNull() throws Exception {
        // Given & When
        CompletableFuture<Void> future = scheduler.executeAsyncTasks(null);
        
        // Then
        future.get(1, TimeUnit.SECONDS);
        assertThat(future.isDone()).isTrue();
        assertThat(future.isCompletedExceptionally()).isFalse();
    }
    
    @Test
    void testConcurrentExecution() throws Exception {
        // Given
        int taskCount = 10;
        CountDownLatch latch = new CountDownLatch(taskCount);
        AtomicInteger counter = new AtomicInteger(0);
        
        List<Runnable> tasks = Collections.nCopies(taskCount, () -> {
            counter.incrementAndGet();
            latch.countDown();
        });
        
        // When
        CompletableFuture<Void> future = scheduler.executeAsyncTasks(tasks);
        
        // Then
        assertThat(latch.await(3, TimeUnit.SECONDS)).isTrue();
        future.get(1, TimeUnit.SECONDS);
        assertThat(counter.get()).isEqualTo(taskCount);
    }
    
    @Test
    void testGetActiveTaskCount() {
        // Given & When
        int activeCount = scheduler.getActiveTaskCount();
        
        // Then
        assertThat(activeCount).isGreaterThanOrEqualTo(0);
    }
    
    @Test
    void testGetQueueSize() {
        // Given & When
        int queueSize = scheduler.getQueueSize();
        
        // Then
        assertThat(queueSize).isGreaterThanOrEqualTo(0);
    }
    
    @Test
    void testShutdown() {
        // Given
        assertThat(scheduler.isShutdown()).isFalse();
        
        // When
        scheduler.shutdown();
        
        // Then
        assertThat(scheduler.isShutdown()).isTrue();
    }
    
    @Test
    void testShutdownWithRunningTasks() throws Exception {
        // Given
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(1);
        
        Runnable longRunningTask = () -> {
            try {
                startLatch.countDown();
                endLatch.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        
        // When
        scheduler.executeAsync(longRunningTask);
        startLatch.await(1, TimeUnit.SECONDS); // 等待任务开始
        
        scheduler.shutdown();
        endLatch.countDown(); // 释放长时间运行的任务
        
        // Then
        assertThat(scheduler.isShutdown()).isTrue();
    }
} 