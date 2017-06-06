package com.afunx.ble.blelitelib.operation;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattDescriptor;
import android.support.annotation.NonNull;

/**
 * Created by afunx on 06/06/2017.
 */

public class BleWriteDescriptorOperation extends BleOperationAbs {

    private final BluetoothGatt mBluetoothGatt;

    private final BluetoothGattDescriptor mDescriptor;

    private final byte[] mMsg;

    public static BleWriteDescriptorOperation createInstance(@NonNull BluetoothGatt bluetoothGatt, @NonNull BluetoothGattDescriptor descriptor, @NonNull byte[] msg) {
        return new BleWriteDescriptorOperation(bluetoothGatt, descriptor, msg);
    }

    private BleWriteDescriptorOperation(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor descriptor, byte[] msg) {
        mBluetoothGatt = bluetoothGatt;
        mDescriptor = descriptor;
        mMsg = msg;
    }

    @Override
    protected void clearConcurrentOperation() {

    }

    @Override
    public void run() {
        mDescriptor.setValue(mMsg);
        mBluetoothGatt.writeDescriptor(mDescriptor);
    }

    @Override
    public long getOperatcionCode() {
        return BLE_WRITE_DESCRIPTOR;
    }

}
