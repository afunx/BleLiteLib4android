package com.afunx.ble.blelitelib.proxy;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.support.annotation.NonNull;

import java.util.UUID;

/**
 * Created by afunx on 13/12/2016.
 */

public interface BleGattClientProxy {

    /**
     * Listener for characteristic notification
     */
    interface OnCharacteristicNotificationListener {
        /**
         * callback when characteristic notification arrived
         * (the callback will work on non main thread usually)
         *
         * @param msg msg received
         */
        void onCharacteristicNotification(final byte[] msg);
    }

    /**
     * set callback for disconnected
     *
     * @param disconnectedCallback disconnected callback
     * @param timeoutMilli         timeout in milliseconds
     */
    void setDisconnectCallback(Runnable disconnectedCallback, long timeoutMilli);

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
     * write characteristic no response
     *
     * @param gattCharacteristic the characteristic
     * @param msg                the message in bytes
     * @param interval           interval after write in milliseconds
     * @return whether write characteristic no response is suc
     */
    boolean writeCharacteristicNoResponse(@NonNull BluetoothGattCharacteristic gattCharacteristic, @NonNull byte[] msg, long interval);


    /**
     * write characteristic no response divided package by 20 bytes
     *
     * @param gattCharacteristic the characteristic
     * @param msg                the message in bytes
     * @return whether write characteristic no response is suc
     */
    boolean writeCharacteristicNoResponse20(@NonNull BluetoothGattCharacteristic gattCharacteristic, @NonNull byte[] msg);

    /**
     * write characteristic no response by specific packet size and packet interval
     *
     * @param gattCharacteristic the characteristic
     * @param msg                the message in bytes
     * @param packetSize         packet size (how many bytes send with one packet)
     * @param packetInterval     packet interval (how many sleep ms between packets)
     * @return whether write characteristic no response is suc
     */
    boolean writeCharacteristicNoResponsePacket(@NonNull BluetoothGattCharacteristic gattCharacteristic, @NonNull byte[] msg, int packetSize, int packetInterval);

    /**
     * register characteristic notification listener
     *
     * @param characteristic the characteristic
     * @param listener       the listener
     * @return whether register suc
     */
    boolean registerCharacteristicNotification(@NonNull BluetoothGattCharacteristic characteristic, OnCharacteristicNotificationListener listener);

    /**
     * unregister characteristic notification listener
     *
     * @param uuid the characteristic's uuid to be unregistered
     */
    void unregisterCharacteristicNotification(@NonNull UUID uuid);

    /**
     * close and release resources
     */
    void close();
}
