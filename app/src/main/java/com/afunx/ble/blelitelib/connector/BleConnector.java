package com.afunx.ble.blelitelib.connector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;

/**
 * Created by afunx on 08/11/2016.
 */

public class BleConnector {

    private final BluetoothDevice mBluetoothDevice;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCallback mBluetoothGattCallback;

    private BleConnector(BluetoothDevice bluetoothDevice) {
        this(bluetoothDevice,null);
    }

    private BleConnector(BluetoothDevice bluetoothDevice, BluetoothGatt bluetoothGatt) {
        mBluetoothDevice = bluetoothDevice;
        mBluetoothGatt = bluetoothGatt;
    }

    private static BluetoothAdapter getAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * Getter and Setter
     */
    public BleConnector setBluetoothGattCallback(BluetoothGattCallback bluetoothGattCallback) {
        mBluetoothGattCallback = bluetoothGattCallback;
        return this;
    }

    /**
     * Builder
     */
    public final static class Builder {
        public BleConnector build(BluetoothDevice bluetoothDevice) {
            return new BleConnector(bluetoothDevice);
        }
        public BleConnector build(BluetoothDevice bluetoothDevice, BluetoothGatt bluetoothGatt) {
            return new BleConnector(bluetoothDevice,bluetoothGatt);
        }
    }
}
