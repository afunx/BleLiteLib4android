package com.afunx.ble.blelitelib.operation;

import android.bluetooth.BluetoothGatt;

import com.afunx.ble.blelitelib.log.BleLiteLog;

/**
 * Created by afunx on 14/12/2016.
 */

public class BleCloseOperation extends BleOperationAbs {
    private static final String TAG = "BleCloseOperation";
    private boolean mIsClosed = false;
    private final BluetoothGatt mBluetoothGatt;

    private BleCloseOperation(BluetoothGatt bluetoothGatt) {
        mBluetoothGatt = bluetoothGatt;
    }

    public static BleCloseOperation createInstance(BluetoothGatt bluetoothGatt) {
        return new BleCloseOperation(bluetoothGatt);
    }

    @Override
    protected void clearConcurrentOperation() {
    }

    @Override
    public void run() {
        if (mIsClosed) {
            BleLiteLog.w(TAG, "it is closed already");
        } else {
            mIsClosed = true;
            if (mBluetoothGatt != null) {
                mBluetoothGatt.close();
            }
        }
    }

    @Override
    public int getOperatcionCode() {
        return BLE_CLOSE;
    }
}
