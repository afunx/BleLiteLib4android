package com.afunx.ble.blelitelib.operation;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.support.annotation.NonNull;

/**
 * Created by afunx on 19/12/2016.
 */

public class BleReadCharacteristicOperation extends BleOperationAbs<byte[]> {

    private final BluetoothGatt mBluetoothGatt;
    private final BluetoothGattCharacteristic mGattCharacteristic;

    public static BleReadCharacteristicOperation createInstance(@NonNull BluetoothGatt bluetoothGatt, @NonNull BluetoothGattCharacteristic gattCharacteristic) {
        return new BleReadCharacteristicOperation(bluetoothGatt, gattCharacteristic);
    }

    private BleReadCharacteristicOperation(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic gattCharacteristic) {
        mBluetoothGatt = bluetoothGatt;
        mGattCharacteristic = gattCharacteristic;
    }

    @Override
    protected void clearConcurentOperation() {

    }

    @Override
    public void run() {
        mBluetoothGatt.readCharacteristic(mGattCharacteristic);
    }

    @Override
    public long getOperatcionCode() {
        return BLE_READ_CHARACTERISTIC;
    }
}
