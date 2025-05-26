package com.browseruse4j.impl;

import com.browseruse4j.core.TaskScheduler;
import com.browseruse4j.config.ConfigLoader;
import com.browseruse4j.utils.LoggerUtils;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.*;

/**
 * 简单任务调度器实现
 * 基于ThreadPoolExecutor实现异步任务调度
 */
public class SimpleTaskScheduler implements TaskScheduler {
    
    private static final Logger LOGGER = LoggerUtils.getLogger(SimpleTaskScheduler.class);
    
    // 配置常量
    private static final String CONFIG_TASK_POOL_SIZE = "task.pool.size";
    private static final String CONFIG_TASK_TIMEOUT = "task.timeout";
    
    private static final int DEFAULT_POOL_SIZE = 10;
    private static final int DEFAULT_TIMEOUT = 60000;
    
    private final ConfigLoader configLoader;
    private final ExecutorService executorService;
    private final int taskTimeout;
    
    public SimpleTaskScheduler() {
        this(new ConfigLoader());
    }
    
    public SimpleTaskScheduler(final ConfigLoader configLoader) {
        this.configLoader = configLoader;
        
        int poolSize = configLoader.getInt(CONFIG_TASK_POOL_SIZE, DEFAULT_POOL_SIZE);
        this.taskTimeout = configLoader.getInt(CONFIG_TASK_TIMEOUT, DEFAULT_TIMEOUT);
        
        this.executorService = new ThreadPoolExecutor(
                poolSize / 2, // 核心线程数
                poolSize,     // 最大线程数
                60L,          // 空闲线程存活时间
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100), // 任务队列
                new ThreadFactory() {
                    private int counter = 0;
                    @Override
                    public Thread newThread(final Runnable r) {
                        Thread thread = new Thread(r, "BrowserUse4j-Task-" + (++counter));
                        thread.setDaemon(true);
                        return thread;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
        );
        
        LoggerUtils.info(LOGGER, "任务调度器初始化完成，线程池大小: " + poolSize + ", 超时时间: " + taskTimeout + "ms");
    }
    
    @Override
    public CompletableFuture<Void> executeAsync(final Runnable task) {
        try {
            LoggerUtils.debug(LOGGER, "提交异步任务: " + task.getClass().getSimpleName());
            
            return CompletableFuture.runAsync(task, executorService)
                    .orTimeout(taskTimeout, TimeUnit.MILLISECONDS)
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            LoggerUtils.error(LOGGER, "异步任务执行失败: " + task.getClass().getSimpleName(), throwable);
                        } else {
                            LoggerUtils.debug(LOGGER, "异步任务执行完成: " + task.getClass().getSimpleName());
                        }
                    });
        } catch (Exception e) {
            LoggerUtils.error(LOGGER, "提交异步任务失败: " + task.getClass().getSimpleName(), e);
            CompletableFuture<Void> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(e);
            return failedFuture;
        }
    }
    
    @Override
    public CompletableFuture<Void> executeAsyncTasks(final List<Runnable> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            LoggerUtils.debug(LOGGER, "任务列表为空，直接返回");
            return CompletableFuture.completedFuture(null);
        }
        
        try {
            LoggerUtils.info(LOGGER, "提交批量异步任务，数量: " + tasks.size());
            
            CompletableFuture<Void>[] futures = tasks.stream()
                    .map(this::executeAsync)
                    .toArray(CompletableFuture[]::new);
            
            return CompletableFuture.allOf(futures)
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            LoggerUtils.error(LOGGER, "批量异步任务执行失败", throwable);
                        } else {
                            LoggerUtils.info(LOGGER, "批量异步任务执行完成，数量: " + tasks.size());
                        }
                    });
        } catch (Exception e) {
            LoggerUtils.error(LOGGER, "提交批量异步任务失败", e);
            CompletableFuture<Void> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(e);
            return failedFuture;
        }
    }
    
    @Override
    public void shutdown() {
        try {
            LoggerUtils.info(LOGGER, "关闭任务调度器...");
            
            executorService.shutdown();
            
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                LoggerUtils.info(LOGGER, "强制关闭任务调度器...");
                executorService.shutdownNow();
                
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    LoggerUtils.error(LOGGER, "任务调度器无法正常关闭", null);
                }
            }
            
            LoggerUtils.info(LOGGER, "任务调度器关闭完成");
        } catch (InterruptedException e) {
            LoggerUtils.error(LOGGER, "关闭任务调度器时被中断", e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 获取当前活跃任务数
     * @return 活跃任务数
     */
    public int getActiveTaskCount() {
        if (executorService instanceof ThreadPoolExecutor) {
            return ((ThreadPoolExecutor) executorService).getActiveCount();
        }
        return 0;
    }
    
    /**
     * 获取队列中等待的任务数
     * @return 等待任务数
     */
    public int getQueueSize() {
        if (executorService instanceof ThreadPoolExecutor) {
            return ((ThreadPoolExecutor) executorService).getQueue().size();
        }
        return 0;
    }
    
    /**
     * 检查调度器是否已关闭
     * @return 是否已关闭
     */
    public boolean isShutdown() {
        return executorService.isShutdown();
    }
} 