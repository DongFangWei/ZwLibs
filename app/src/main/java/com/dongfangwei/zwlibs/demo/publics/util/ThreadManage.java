package com.dongfangwei.zwlibs.demo.publics.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadManage {
    private static ExecutorService sThreadPool;

    private static ExecutorService getThreadPool() {
        if (sThreadPool == null) {
            sThreadPool = new ThreadPoolExecutor(4, 16, 60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
        }
        return sThreadPool;
    }

    public static void execute(Runnable command) {
        getThreadPool().execute(command);
    }
}
