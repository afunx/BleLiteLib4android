package com.afunx.ble.blelitelib.operation;

import android.bluetooth.BluetoothGattCallback;
import android.os.Looper;
import android.util.LongSparseArray;
import android.util.SparseArray;

import com.afunx.ble.blelitelib.connector.BleConnector;
import com.afunx.ble.blelitelib.log.BleLiteLog;
import com.afunx.ble.blelitelib.proxy.BleGattClientProxy;
import com.afunx.ble.blelitelib.threadpool.BleThreadpool;

import java.util.concurrent.Future;

/**
 * Created by afunx on 13/12/2016.
 */

public abstract class BleOperationAbs<T> implements BleOperation {

    private static final String TAG = "BleOperationAbs";
    private static final BleThreadpool sThreadpool = BleThreadpool.getInstance();

    private final LongSparseArray<Runnable> mRunnables = new LongSparseArray<>();
    private final BleOperationLock mLock = new BleOperationLock();
    private volatile T mResult;
    private volatile BleGattClientProxy mOwner;
    private volatile BleConnector mConnector;
    private volatile BluetoothGattCallback mBluetoothGattCallback;

    private static final SparseArray<Future<?>> mInterruptableTasks = new SparseArray<>();

    /**
     * <K,V> for <Long, Runnable> implement by LongSparseArray
     */

    //    @GuardBy("mRunnables")
    private Runnable getRunnable(long key, boolean remove) {
        Runnable runnable;
        synchronized (mRunnables) {
            runnable = mRunnables.get(key);
            if (remove) {
                mRunnables.remove(key);
            }
        }
        return runnable;
    }

    public final Runnable getRunnable(long key) {
        return getRunnable(key, false);
    }

    //    @GuardBy("mRunnables")
    public final void removeRunnable(long key) {
        synchronized (mRunnables) {
            mRunnables.remove(key);
        }
    }

    //    @GuardBy("mRunnables")
    public final void putRunnable(long key, Runnable runnable) {
        synchronized (mRunnables) {
            mRunnables.put(key, runnable);
        }
    }

    /**
     * Do runnables Async
     */

    public final void doRunnableAsync(long key, boolean remove) {
        final Runnable runnable = getRunnable(key, remove);
        if (runnable != null) {
            sThreadpool.submit(runnable);
        }
    }

    public final void doRunnableUIAsync(long key, boolean remove) {
        final Runnable runnable = getRunnable(key, remove);
        if (runnable != null) {
            sThreadpool.submitInMain(runnable);
        }
    }

    public final void doRunnableSelfAsync(boolean UI) {
        if (UI) {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                throw new IllegalStateException("It can't be called in UI Main Thread.");
            }
            sThreadpool.submitInMain(this);
        } else {
            sThreadpool.submit(this);
        }
    }

    public final void doRunnableSelfAsyncInterruptable(int code) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IllegalStateException("It can't be called in UI Main Thread.");
        }
        if (this instanceof BleInterruptable) {
            synchronized (mInterruptableTasks) {
                Future<?> oldFuture = mInterruptableTasks.get(code);
                if (oldFuture != null) {
                    oldFuture.cancel(true);
                }
                Future<?> future = sThreadpool.submit(this);
                mInterruptableTasks.put(code, future);
            }
        } else {
            throw new IllegalArgumentException("Only BleInterruptable could call the method.");
        }
    }

    /**
     * Getter and Setter
     */

    public final T getResult() {
        return mResult;
    }

    public final void setResult(T result) {
        mResult = result;
    }

    public final BleGattClientProxy getOwner() {
        return mOwner;
    }

    public final void setOwner(BleGattClientProxy owner) {
        mOwner = owner;
    }

    public final BleConnector getConnector() {
        return mConnector;
    }

    public final void setConnector(BleConnector connector) {
        mConnector = connector;
    }

    public final BluetoothGattCallback getBluetoothGattCallback() {
        return mBluetoothGattCallback;
    }

    public final void setBluetoothGattCallback(BluetoothGattCallback bluetoothGattCallback) {
        mBluetoothGattCallback = bluetoothGattCallback;
    }

    /**
     * BleOperationLock
     */

    public final void notifyLock() {
        mLock.notifyLock();
    }

    public final void waitLock(long milli) {
        mLock.waitLock(milli);
    }

    public final boolean isNotified() {
        return mLock.isNotified();
    }


    /**
     * Clear resources
     */
    private void clearBleOperationAbs() {
        mRunnables.clear();
        mResult = null;
        mOwner = null;
        mConnector = null;
        mBluetoothGattCallback = null;
    }

    protected abstract void clearConcurrentOperation();

    public final void clear() {
        BleLiteLog.v(TAG, "clear() called by " + this);
        clearConcurrentOperation();
        clearBleOperationAbs();
    }

    @Override
    public String toString() {
        int hashCode = hashCode();
        long code = getOperatcionCode();
        String simpleName = getClass().getSimpleName();
        return String.format("[%s-%d-%04x]", simpleName, hashCode, code);
    }
}