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
    protected void clearConcurentOperation() {
    }

    @Override
    public void run() {
        if (mIsClosed) {
            BleLiteLog.w(TAG, "it is closed already");
        } else {
            mIsClosed = true;
            if (mBluetoothGatt != null) {
                mBluetoothGatt.close();
                // wait 500 ms to let android close gatt
                waitLock(500);
            }
        }
    }

    @Override
    public long getOperatcionCode() {
        return BLE_CLOSE;
    }
}
