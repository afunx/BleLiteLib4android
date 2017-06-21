package com.afunx.ble.blelitelib.operation;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.support.annotation.NonNull;

/**
 * Created by afunx on 15/06/2017.
 */

public class BleWriteCharacteristicNoResponseOperation extends BleOperationAbs {

    private final BluetoothGatt mBluetoothGatt;
    private final BluetoothGattCharacteristic mCharacteristic;
    private final byte[] mMsg;

    public static BleWriteCharacteristicNoResponseOperation createInstance(@NonNull BluetoothGatt bluetoothGatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] msg) {
        return new BleWriteCharacteristicNoResponseOperation(bluetoothGatt, characteristic, msg);
    }

    private BleWriteCharacteristicNoResponseOperation(@NonNull BluetoothGatt bluetoothGatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] msg) {
        mBluetoothGatt = bluetoothGatt;
        mCharacteristic = characteristic;
        mMsg = msg;
    }

    @Override
    protected void clearConcurrentOperation() {

    }

    @Override
    public void run() {
        mCharacteristic.setValue(mMsg);
        mCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        mBluetoothGatt.writeCharacteristic(mCharacteristic);
    }

    @Override
    public long getOperatcionCode() {
        return BLE_WRITE_CHARACTERISTIC_NO_RESPONSE;
    }
}
