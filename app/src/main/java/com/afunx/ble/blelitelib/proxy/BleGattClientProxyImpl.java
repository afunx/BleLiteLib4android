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

/**
 * Created by afunx on 13/12/2016.
 */

public class BleGattClientProxyImpl implements BleGattClientProxy {

    private static final String TAG = "BleGattClientProxyImpl";
    private static final long BLE_CONNECT_ERR_INTERVAL_MILLISECONDS = 2000;

    private volatile BleConnector mBleConnector;
    private volatile boolean mIsClosed = false;
    private final LongSparseArray<BleOperation> mOperations = new LongSparseArray<>();
    private final Context mAppContext;

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

    private void unregister(BleOperation operation) {
        BleLiteLog.d(TAG, "unregister() operation: " + operation);
        mOperations.remove(operation.getOperatcionCode());
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

    /**
     * BluetoothGattCallback
     */

    private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            BleLiteLog.i(TAG, "onConnectionStateChange() status:" + status
                    + ",newState:" + newState);
            final BleConnectOperation connectOperation = getConnectOperation();
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    if (connectOperation != null) {
                        connectOperation.notifyLock();
                    }
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    break;
            }
        }
    };

    private void setBleConnector(BleConnector connector) {
        mBleConnector = connector;
    }

    @Override
    public synchronized boolean connect(String bleAddr, long timeout) {
        mIsClosed = false;
        // create operation
        final Context context = mAppContext;
        BleConnectOperation connectOperation = BleConnectOperation.createInstance(context, bleAddr, mBluetoothGattCallback);
        // register
        register(connectOperation);
        long startTimestamp = System.currentTimeMillis();
        // execute operation
        connectOperation.doRunnableSelfAsync(true);
        connectOperation.waitLock(timeout);
        long consume = System.currentTimeMillis() - startTimestamp;
        // unregister
        unregister(connectOperation);
        boolean isConnectSuc = connectOperation.isNotified();
        if (isConnectSuc) {
            setBleConnector(connectOperation.getConnector());
        }
        BleLiteLog.i(TAG,"connect() suc: " + isConnectSuc + ", consume: " + consume + " ms");
        return isConnectSuc;
    }

    @Override
    public synchronized void close() {
        if (!mIsClosed) {
            mIsClosed = true;
            BleLiteLog.i(TAG, "close() closing");
            // create operation
            final BluetoothGatt bluetoothGatt = mBleConnector != null ? mBleConnector.getBluetoothGatt() : null;
            if (bluetoothGatt == null) {
                BleLiteLog.i(TAG, "close() bluetoothGatt is null");
            }
            BleCloseOperation closeOperation = BleCloseOperation.createInstance(bluetoothGatt);
            // register
            register(closeOperation);
            // execute operation
            closeOperation.doRunnableSelfAsync(false);
            BleLiteLog.i(TAG, "close() closed");
        }
    }
}
