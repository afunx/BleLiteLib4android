package com.afunx.ble.blelitelib.proxy.scheme;

import com.afunx.ble.blelitelib.log.BleLiteLog;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by afunx on 15/12/2016.
 */

public class BleGattReconnectSchemeDefaultImpl implements BleGattReconnectScheme {

    private static final String TAG = "BleGattReconnectSchemeDefaultImpl";
    private final AtomicInteger mCount = new AtomicInteger();
    private final Timer mTimer = new Timer();
    private volatile Runnable mDisconnectedCallback;
    private volatile TimerTask mTimerTask = null;
    private volatile long mTimeoutMilli = 0;
    private volatile boolean mTryAgain = true;

    @Override
    public void clearRetryCount() {
        removeTimerTask();
        mCount.set(0);
    }

    @Override
    public int addAndGetRetryCount() {
        return mCount.addAndGet(1);
    }

    @Override
    public long getSleepTimestamp(int retryCount) {
        BleLiteLog.i(TAG, "getSleepTimestamp() retryCount: " + retryCount + ", mDisconnectedCallback: " + mDisconnectedCallback);
        if (retryCount == 1) {
            if (mDisconnectedCallback != null) {
                addTimerTask(mTimeoutMilli);
            }
        }
        // 2000ms, 4000ms, 8000ms, ...
        return 1000 * (2 << (retryCount - 1));
    }

    @Override
    public boolean tryAgain() {
        return mTryAgain;
    }

    @Override
    public void setDisconnectCallback(final Runnable disconnectedCallback, final long timeoutMilli) {
        if (timeoutMilli < 0) {
            throw new IllegalArgumentException("timeoutMilli should >= 0");
        }
        mTimeoutMilli = timeoutMilli;
        mDisconnectedCallback = disconnectedCallback;
    }

    @Override
    public synchronized void callDisconnectCallbackInstantly() {
        BleLiteLog.i(TAG, "callDisconnectCallbackInstantly()");
        if (mTimerTask != null) {
            boolean isCancelled = removeTimerTask();
            if (isCancelled) {
                BleLiteLog.i(TAG, "callDisconnectCallbackInstantly() run() for cancelled already");
                if (mDisconnectedCallback != null) {
                    mDisconnectedCallback.run();
                }
            }
        } else {
            BleLiteLog.i(TAG, "callDisconnectCallbackInstantly() run()");
            if (mDisconnectedCallback != null) {
                mDisconnectedCallback.run();
            }
        }
    }

    private synchronized void addTimerTask(final long delayMilli) {
        BleLiteLog.i(TAG, "addTimerTask() delayMilli: " + delayMilli);
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                BleLiteLog.i(TAG, "addTimerTask() run()");
                mTryAgain = false;
                if (mDisconnectedCallback != null) {
                    mDisconnectedCallback.run();
                }
            }
        };
        mTimer.schedule(mTimerTask, delayMilli);
    }

    private synchronized boolean removeTimerTask() {
        BleLiteLog.i(TAG, "removeTimerTask()");
        if (mTimerTask != null) {
            BleLiteLog.i(TAG, "removeTimerTask() cancel()");
            boolean isCancelled = mTimerTask.cancel();
            mTimerTask = null;
            return isCancelled;
        }
        mTryAgain = true;
        BleLiteLog.i(TAG, "removeTimerTask() mTryAgain = true");
        return false;
    }
}