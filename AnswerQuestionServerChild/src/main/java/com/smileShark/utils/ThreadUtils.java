package com.smileShark.utils;

import java.util.concurrent.*;

public class ThreadUtils {
    public static final ExecutorService executorService = new CustomThreadPoolExecutor(
            800, // 核心线程数
            1000, // 最大线程数
            120L, // 空闲线程存活时间
            TimeUnit.SECONDS, // 时间单位
            new LinkedBlockingQueue<>(Integer.MAX_VALUE) ,// 任务队列大小
            new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
    );

    public static class CustomThreadPoolExecutor extends ThreadPoolExecutor {
        public CustomThreadPoolExecutor(
                int corePoolSize,
                int maximumPoolSize,
                long keepAliveTime,
                TimeUnit unit,
                BlockingQueue<Runnable> workQueue,
                RejectedExecutionHandler handler
        ) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        }

        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            super.beforeExecute(t, r);
            printThreadPoolStatus("提交任务");
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
            printThreadPoolStatus("完成任务");
        }

        private void printThreadPoolStatus(String action) {
//            System.out.println(action + " - 线程池队列大小: " + getQueue().size());
//            System.out.println(action + " - 线程池活跃线程线程池已完成任务数数: " + getActiveCount());
//            System.out.println(action + " - : " + getCompletedTaskCount());
        }
    }
}
