package com.afunx.ble.blelitelib.operation;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.support.annotation.NonNull;

/**
 * Created by afunx on 19/12/2016.
 */

public class BleWriteCharacteristicOperation extends BleOperationAbs {

    private final BluetoothGatt mBluetoothGatt;
    private final BluetoothGattCharacteristic mCharacteristic;
    private final byte[] mMsg;

    public static BleWriteCharacteristicOperation createInstance(@NonNull BluetoothGatt bluetoothGatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] msg) {
        return new BleWriteCharacteristicOperation(bluetoothGatt, characteristic, msg);
    }

    private BleWriteCharacteristicOperation(@NonNull BluetoothGatt bluetoothGatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] msg) {
        mBluetoothGatt = bluetoothGatt;
        mCharacteristic = characteristic;
        mMsg = msg;
    }

    @Override
    protected void clearConcurentOperation() {

    }

    @Override
    public void run() {
        mCharacteristic.setValue(mMsg);
        mBluetoothGatt.writeCharacteristic(mCharacteristic);
    }

    @Override
    public long getOperatcionCode() {
        return BLE_WRITE_CHARACTERISTIC;
    }
}
