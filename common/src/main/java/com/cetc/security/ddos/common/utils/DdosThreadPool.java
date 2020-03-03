package com.cetc.security.ddos.common.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhangtao on 2015/6/15.
 */
public class DdosThreadPool {
    static final int MAX_THREAD_NUM = 10;
    static final ExecutorService threadPool = Executors.newFixedThreadPool(MAX_THREAD_NUM);

    public static void start(Runnable runnable) {
        threadPool.execute(runnable);
    }

    public static void shutdown() {
        threadPool.shutdown();
    }
}
