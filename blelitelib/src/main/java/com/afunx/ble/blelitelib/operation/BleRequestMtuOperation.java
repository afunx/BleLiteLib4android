package com.afunx.ble.blelitelib.operation;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.os.Build;

/**
 * Created by afunx on 19/12/2016.
 */

public class BleRequestMtuOperation extends BleOperationAbs {

    private final BluetoothGatt mBluetoothGatt;
    private final int mMTU;

    public static BleRequestMtuOperation createInstance(BluetoothGatt bluetoothGatt, int mtu) {
        return new BleRequestMtuOperation(bluetoothGatt, mtu);
    }

    private BleRequestMtuOperation(BluetoothGatt bluetoothGatt, int mtu) {
        mBluetoothGatt = bluetoothGatt;
        mMTU = mtu;
    }


    @Override
    protected void clearConcurentOperation() {

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void run() {
        mBluetoothGatt.requestMtu(mMTU);
    }

    @Override
    public long getOperatcionCode() {
        return BLE_REQUEST_MTU;
    }
}
