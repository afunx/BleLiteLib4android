package com.afunx.ble.blelitelib.proxy;

import android.bluetooth.BluetoothGattService;

import com.afunx.ble.blelitelib.operation.BleOperation;

import java.util.UUID;

/**
 * Created by afunx on 13/12/2016.
 */

public interface BleGattClientProxy {

    /**
     * try to connect to ble device
     *
     * @param bleAddr the ble device's ble address
     * @param timeout timeout in milliseconds
     * @return connect suc or not in timeout
     */
    boolean connect(String bleAddr, long timeout);

    /**
     * discover bluetooth gatt service according to uuid
     *
     * @param uuid    service uuid
     * @param timeout timeout in milliseconds
     * @return bluetooth gatt service
     */
    BluetoothGattService discoverService(UUID uuid, long timeout);

    /**
     * close and release resources
     */
    void close();
}
