package com.afunx.ble.blelitelib.operation;

/**
 * Created by afunx on 13/12/2016.
 */

public interface BleOperation extends Runnable {
    /**
     * Runnable Code
     */
    int BLE_CONNECT = 0x0001;
    int BLE_CLOSE = 0x0002;
    int BLE_DISCOVER_SERVICE = 0x0003;
    int BLE_READ_CHARACTERISTIC = 0x0004;
    int BLE_REQUEST_MTU = 0x0005;
    int BLE_WRITE_CHARACTERISTIC = 0x0006;
    int BLE_WRITE_DESCRIPTOR = 0x0007;
    int BLE_WRITE_CHARACTERISTIC_NO_RESPONSE = 0x0008;
    int BLE_WRITE_CHARACTERISTIC_NO_RESPONSE_20 = 0x0009;
    int BLE_WRITE_CHARACTERISITC_NO_RESPONSE_PACKET = 0x000a;
    int BLE_WRITE_CHARACTERISITC_NO_RESPONSE_PACKET2 = 0x000b;
    int BLE_WRITE_CHARACTERISTIC_NO_RESPONSE_INTERRUPT = 0x000c;

    abstract void run();

    abstract int getOperatcionCode();
}
