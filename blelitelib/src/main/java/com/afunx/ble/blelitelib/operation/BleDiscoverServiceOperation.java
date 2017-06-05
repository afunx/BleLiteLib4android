package com.afunx.ble.blelitelib.operation;

import android.bluetooth.BluetoothGatt;
import android.support.annotation.NonNull;

/**
 * Created by afunx on 19/12/2016.
 */

public class BleDiscoverServiceOperation extends BleOperationAbs {

    private final BluetoothGatt mBluetoothGatt;

    public static BleDiscoverServiceOperation createInstance(@NonNull BluetoothGatt bluetoothGatt) {
        return new BleDiscoverServiceOperation(bluetoothGatt);
    }

    private BleDiscoverServiceOperation(@NonNull BluetoothGatt bluetoothGatt) {
        mBluetoothGatt = bluetoothGatt;
    }

    @Override
    protected void clearConcurrentOperation() {

    }

    @Override
    public void run() {
        mBluetoothGatt.discoverServices();
    }

    @Override
    public long getOperatcionCode() {
        return BLE_DISCOVER_SERVICE;
    }
}
