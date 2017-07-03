package com.afunx.ble.blelitelib.connector;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;

import com.afunx.ble.blelitelib.log.BleLiteLog;

/**
 * Created by afunx on 08/11/2016.
 */

public class BleConnector {

    private static final String TAG = "BleConnector";

    private final Context mAppContext;
    private final BluetoothDevice mBluetoothDevice;
    private final BluetoothGattCallback mBluetoothGattCallback;
    private BluetoothGatt mBluetoothGatt;
    private volatile boolean mIsClosed;

    private BleConnector(Context appContext, BluetoothDevice bluetoothDevice, BluetoothGatt bluetoothGatt, BluetoothGattCallback bluetoothGattCallback) {
        // get application context to avoid memory leak
        mAppContext = appContext.getApplicationContext();
        mBluetoothDevice = bluetoothDevice;
        mBluetoothGatt = bluetoothGatt;
        mBluetoothGattCallback = bluetoothGattCallback;
    }

    /**
     * connect to the specific bluetooth device
     */
    public synchronized void connect() {
        if (mIsClosed) {
            BleLiteLog.w(TAG, "connect() has been closed already, do nothing");
            return;
        }
        if (mBluetoothGatt == null) {
            BleLiteLog.i(TAG, "connect() connect...");
            BleConnectCompat connectCompat = new BleConnectCompat(mAppContext);
            BluetoothGatt bluetoothGatt = connectCompat.connectGatt(mBluetoothDevice, true, mBluetoothGattCallback);
            mBluetoothGatt = bluetoothGatt;
        } else {
            BleLiteLog.i(TAG, "connect() reconnect...");
            mBluetoothGatt.connect();
        }
    }

    /**
     * close bluetooth gatt
     */
    public synchronized void close() {
        if (!mIsClosed) {
            mIsClosed = true;
            if (mBluetoothGatt != null) {
                BleLiteLog.i(TAG, "close()");
                mBluetoothGatt.close();
            }
        }
    }

    /**
     * Getter and Setter
     */
    public synchronized BluetoothGatt getBluetoothGatt() {
        if (mIsClosed) {
            BleLiteLog.w(TAG, "getBluetoothGatt() has been closed already, return null");
            return null;
        } else {
            return mBluetoothGatt;
        }
    }

    /**
     * Builder
     */
    public final static class Builder {

        private Context mContext;
        private BluetoothDevice mBluetoothDevice;
        private BluetoothGatt mBluetoothGatt;
        private BluetoothGattCallback mBluetoothGattCallback;

        public Builder build(Context context, BluetoothDevice bluetoothDevice) {
            mContext = context;
            mBluetoothDevice = bluetoothDevice;
            return this;
        }

        public Builder build(Context context, BluetoothDevice bluetoothDevice, BluetoothGatt bluetoothGatt) {
            mContext = context;
            mBluetoothDevice = bluetoothDevice;
            mBluetoothGatt = bluetoothGatt;
            return this;
        }

        public Builder setGattCallback(BluetoothGattCallback bluetoothGattCallback) {
            mBluetoothGattCallback = bluetoothGattCallback;
            return this;
        }

        public BleConnector create() {
            return new BleConnector(mContext, mBluetoothDevice, mBluetoothGatt, mBluetoothGattCallback);
        }

    }
}
