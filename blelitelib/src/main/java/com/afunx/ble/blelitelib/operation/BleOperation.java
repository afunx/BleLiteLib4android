package com.afunx.ble.blelitelib.operation;

/**
 * Created by afunx on 13/12/2016.
 */

public interface BleOperation extends Runnable {
    /**
     * Runnable Code
     */
    long BLE_CONNECT = 0x0001;
    long BLE_CLOSE = 0x0002;
    long BLE_DISCOVER_SERVICE = 0x0003;
    long BLE_READ_CHARACTERISTIC = 0x0004;
    long BLE_REQUEST_MTU = 0x0005;
    long BLE_WRITE_CHARACTERISTIC = 0x0006;
    long BLE_WRITE_DESCRIPTOR = 0x0007;
    long BLE_WRITE_CHARACTERISTIC_NO_RESPONSE = 0x0008;
    long BLE_WRITE_CHARACTERISTIC_NO_RESPONSE_20 = 0x0009;
    long BLE_WRITE_CHARACTERISITC_NO_RESPONSE_PACKET = 0x000a;
    long BLE_WRITE_CHARACTERISITC_NO_RESPONSE_PACKET2 = 0x000b;
    long BLE_WRITE_CHARACTERISTIC_NO_RESPONSE_INTERRUPT = 0x000c;

    abstract void run();

    abstract long getOperatcionCode();
}
