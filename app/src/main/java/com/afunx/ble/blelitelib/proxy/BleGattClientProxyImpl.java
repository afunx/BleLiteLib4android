package com.afunx.ble.blelitelib.proxy;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.LongSparseArray;

import com.afunx.ble.blelitelib.connector.BleConnector;
import com.afunx.ble.blelitelib.log.BleLiteLog;
import com.afunx.ble.blelitelib.operation.BleCloseOperation;
import com.afunx.ble.blelitelib.operation.BleConnectOperation;
import com.afunx.ble.blelitelib.operation.BleOperation;
import com.afunx.ble.blelitelib.proxy.scheme.BleGattReconnectScheme;
import com.afunx.ble.blelitelib.proxy.scheme.BleGattReconnectSchemeDefaultImpl;
import com.afunx.ble.blelitelib.threadpool.BleThreadpool;
import com.afunx.ble.blelitelib.utils.BleGattStateParser;
import com.afunx.ble.blelitelib.utils.BleGattStatusParser;

/**
 * Created by afunx on 13/12/2016.
 */

public class BleGattClientProxyImpl implements BleGattClientProxy {

    private static final String TAG = "BleGattClientProxyImpl";

    private volatile BleConnector mBleConnector;
    private volatile boolean mIsClosed = false;
    private final LongSparseArray<BleOperation> mOperations = new LongSparseArray<>();
    private final BleGattReconnectScheme mReconnectScheme = new BleGattReconnectSchemeDefaultImpl();
    private final Context mAppContext;

    private final Object mLock4Connect = new Object();
    private final Object mLock4Close = new Object();

    public BleGattClientProxyImpl(Context appContext) {
        mAppContext = appContext.getApplicationContext();
    }

    /**
     * register and unregister BleOperation
     */

    private void register(BleOperation operation) {
        BleLiteLog.d(TAG, "register() operation: " + operation);
        mOperations.put(operation.getOperatcionCode(), operation);
    }

    private void unregister(long operationCode) {
        BleLiteLog.d(TAG, "unregister() operationCode: " + operationCode);
        mOperations.remove(operationCode);
    }

    /**
     * get various BleOperation
     */

    private BleConnectOperation getConnectOperation() {
        final BleOperation operation = mOperations.get(BleOperation.BLE_CONNECT);
        return operation != null ? (BleConnectOperation) operation : null;
    }

    private BluetoothGatt getBluetoothGatt() {
        return mBleConnector != null ? mBleConnector.getBluetoothGatt() : null;
    }

    /**
     * BluetoothGattCallback
     */

    private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            BleLiteLog.i(TAG, "onConnectionStateChange() status: " + BleGattStatusParser.parse(status)
                    + ", newState: " + BleGattStateParser.parse(newState));
            final BleConnectOperation connectOperation = getConnectOperation();

            switch (newState) {
                case BluetoothProfile.STATE_DISCONNECTED:
                    synchronized (mLock4Close) {
                        if (!mIsClosed) {
                            int retryCount = mReconnectScheme.addAndGetRetryCount();
                            final long sleepTimestamp = mReconnectScheme.getSleepTimestamp(retryCount);
                            Runnable reConnectRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(sleepTimestamp);
                                    } catch (InterruptedException ignore) {
                                    }
                                    final BleConnectOperation connectOperation = getConnectOperation();
                                    if (connectOperation != null) {
                                        BleLiteLog.i(TAG, "onConnectionStateChange() reconnect...");
                                        connectOperation.doRunnableSelfAsync(true);
                                    } else {
                                        BleLiteLog.i(TAG, "onConnectionStateChange() connectOperation is null");
                                    }
                                }
                            };
                            BleThreadpool.getInstance().submit(reConnectRunnable);
                        }
                    }
                    break;
                case BluetoothProfile.STATE_CONNECTING:
                    break;
                case BluetoothProfile.STATE_CONNECTED:
                    mReconnectScheme.clearRetryCount();
                    if (connectOperation != null) {
                        connectOperation.notifyLock();
                    }
                    break;
                case BluetoothProfile.STATE_DISCONNECTING:
                    break;
            }

        }
    };

    private void setBleConnector(BleConnector connector) {
        mBleConnector = connector;
    }

    @Override
    public boolean connect(String bleAddr, long timeout) {
        synchronized (mLock4Connect) {
            boolean isConnectSuc = __connect(bleAddr, timeout);
            return isConnectSuc;
        }
    }

    @Override
    public void close() {
        synchronized (mLock4Close) {
            __close();
        }
    }

    private boolean __connect(String bleAddr, long timeout) {
        synchronized (mLock4Close) {
            mIsClosed = false;
        }
        // clear retry count
        mReconnectScheme.clearRetryCount();
        // create operation
        final Context context = mAppContext;
        BleConnectOperation connectOperation = getConnectOperation();
        if (connectOperation == null) {
            connectOperation = BleConnectOperation.createInstance(context, bleAddr, mBluetoothGattCallback);
            // register
            register(connectOperation);
        } else {
            throw new IllegalStateException("call close() before call connect(String, long) once more");
        }
        long startTimestamp = System.currentTimeMillis();
        // execute operation
        connectOperation.doRunnableSelfAsync(true);
        connectOperation.waitLock(timeout);
        long consume = System.currentTimeMillis() - startTimestamp;
        // set connector
        setBleConnector(connectOperation.getConnector());
        boolean isConnectSuc = connectOperation.isNotified();
        BleLiteLog.i(TAG, "__connect() suc: " + isConnectSuc + ", consume: " + consume + " ms");
        if (!isConnectSuc) {
            // don't forget to close gatt if connect fail
            close();
        }
        return isConnectSuc;
    }

    private void __close() {
        if (!mIsClosed) {
            mIsClosed = true;
            BleLiteLog.i(TAG, "__close() closing");
            // unregister
            unregister(BleOperation.BLE_CONNECT);
            // create operation
            final BluetoothGatt bluetoothGatt = getBluetoothGatt();
            if (bluetoothGatt == null) {
                BleLiteLog.i(TAG, "__close() bluetoothGatt is null");
            }
            BleCloseOperation closeOperation = BleCloseOperation.createInstance(bluetoothGatt);
            // execute operation
            closeOperation.doRunnableSelfAsync(false);
            BleLiteLog.i(TAG, "__close() closed");
        } else {
            BleLiteLog.i(TAG, "__close() it is closed already");
        }
    }

}
