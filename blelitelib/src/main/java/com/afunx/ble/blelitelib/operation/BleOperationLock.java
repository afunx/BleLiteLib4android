package com.afunx.ble.blelitelib.operation;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by afunx on 13/12/2016.
 */

public class BleOperationLock {

    private final Semaphore mSemaphore = new Semaphore(0);
    private volatile boolean mIsNotified = false;

    //@GuardBy("mSemaphore")
    void waitLock(long mills) {
        try {
            mSemaphore.tryAcquire(1, mills, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //@GuardBy("mSemaphore")
    boolean isNotified() {
        return mIsNotified;
    }

    //@GuardBy("mSemaphore")
    void notifyLock() {
        mIsNotified = true;
        mSemaphore.release();
    }

}
