package com.afunx.ble.blelitelib.operation;

/**
 * Created by afunx on 13/12/2016.
 */

public class BleOperationLock {
    private final Object mLock = new Object();
    private volatile boolean mIsNotified = false;

    //@GuardBy("mLock")
    void waitLock(long mills) {
        synchronized (mLock) {
            // check whether mIsNotified is set before waitLock
            if (!mIsNotified) {
                try {
                    mLock.wait(mills);
                } catch (InterruptedException ignore) {
                }
            }
        }
    }

    //@GuardBy("mLock")
    boolean isNotified() {
        synchronized (mLock) {
            return mIsNotified;
        }
    }

    //@GuardBy("mLock")
    void notifyLock() {
        synchronized (mLock) {
            mIsNotified = true;
            mLock.notify();
        }
    }

}
