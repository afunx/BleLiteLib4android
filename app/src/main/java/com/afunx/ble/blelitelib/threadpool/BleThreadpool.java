package com.afunx.ble.blelitelib.threadpool;

import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by afunx on 12/12/2016.
 */
public class BleThreadpool {

    private Handler mMainHandler;
    private ExecutorService mExecutor;

    private static BleThreadpool instance = new BleThreadpool();

    public static BleThreadpool getInstance() {
        return instance;
    }

    private BleThreadpool() {
        mMainHandler = new Handler(Looper.getMainLooper());
        mExecutor = Executors.newCachedThreadPool();
    }

    public <T> Future<T> submit(Callable<T> task) {
        return mExecutor.submit(task);
    }

    public Future<?> submit(Runnable task) {
        return mExecutor.submit(task);
    }

    public void submitInMain(Runnable task) {
        mMainHandler.post(task);
    }

    public void shutdown() {
        mExecutor.shutdown();
    }

    public List<Runnable> shutdownNow() {
        return mExecutor.shutdownNow();
    }
}