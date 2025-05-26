package com.browseruse4j.core;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 任务调度器接口
 * 定义异步任务处理的基本操作
 */
public interface TaskScheduler {
    
    /**
     * 执行异步任务
     * @param task 要执行的任务
     * @return CompletableFuture
     */
    CompletableFuture<Void> executeAsync(Runnable task);
    
    /**
     * 执行多个异步任务
     * @param tasks 任务列表
     * @return CompletableFuture
     */
    CompletableFuture<Void> executeAsyncTasks(List<Runnable> tasks);
    
    /**
     * 关闭调度器
     */
    void shutdown();
} 