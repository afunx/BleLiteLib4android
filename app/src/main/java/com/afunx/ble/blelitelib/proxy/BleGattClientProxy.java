package com.afunx.ble.blelitelib.proxy;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.support.annotation.NonNull;

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
    boolean connect(@NonNull String bleAddr, long timeout);

    /**
     * discover bluetooth gatt service according to uuid
     *
     * @param uuid    service uuid
     * @param timeout timeout in milliseconds
     * @return bluetooth gatt service
     */
    BluetoothGattService discoverService(@NonNull UUID uuid, long timeout);

    /**
     * discover bluetooth gatt characteristic according to gatt service and uuid
     *
     * @param gattService gatt service
     * @param uuid        uuid of gatt characteristic
     * @return bluetooth gatt characteristic
     */
    BluetoothGattCharacteristic discoverCharacteristic(@NonNull BluetoothGattService gattService, @NonNull UUID uuid);

    /**
     * request mtu
     *
     * @param mtu     the mtu
     * @param timeout timeout in milliseconds
     * @return whether request mtu is suc
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    boolean requestMtu(int mtu, long timeout);

    /**
     * read characteristic
     *
     * @param gattCharacteristic the characteristic
     * @param timeout            timeout in milliseconds
     * @return null when fail or bytes read
     */
    byte[] readCharacteristic(@NonNull BluetoothGattCharacteristic gattCharacteristic, long timeout);

    /**
     * write characteristic
     *
     * @param gattCharacteristic the characteristic
     * @param msg                the message in bytes
     * @param timeout            timeout in milliseconds
     * @return whether write characteristic is suc
     */
    boolean writeCharacteristic(@NonNull BluetoothGattCharacteristic gattCharacteristic, @NonNull byte[] msg, long timeout);

    /**
     * close and release resources
     */
    void close();
}
