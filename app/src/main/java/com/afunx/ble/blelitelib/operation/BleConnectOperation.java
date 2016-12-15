package com.afunx.ble.blelitelib.operation;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;

import com.afunx.ble.blelitelib.connector.BleConnector;
import com.afunx.ble.blelitelib.utils.BleUtils;

/**
 * Created by afunx on 13/12/2016.
 */

public class BleConnectOperation extends BleOperationAbs {
    private final Context mAppContext;
    private final String mBleAddr;
    private final BluetoothGattCallback mBluetoothGattCallback;

    public static BleConnectOperation createInstance(Context appContext, String bleAddr, BluetoothGattCallback bluetoothGattCallback) {
        return new BleConnectOperation(appContext, bleAddr, bluetoothGattCallback);
    }

    private BleConnectOperation(Context appContext, String bleAddr, BluetoothGattCallback bluetoothGattCallback) {
        mAppContext = appContext.getApplicationContext();
        mBleAddr = bleAddr;
        mBluetoothGattCallback = bluetoothGattCallback;
    }

    @Override
    protected void clearConcurentOperation() {
    }

    @Override
    public void run() {
        BluetoothDevice bluetoothDevice = BleUtils.getRemoteDevice(mBleAddr);
//        bluetoothDevice.fetchUuidsWithSdp();
        BleConnector connector = getConnector();
        if (connector == null) {
            connector = new BleConnector.Builder().build(mAppContext, bluetoothDevice)
                    .setGattCallback(mBluetoothGattCallback).create();
            setConnector(connector);
        }

        connector.connect();
    }

    @Override
    public long getOperatcionCode() {
        return BLE_CONNECT;
    }


}
