package com.afunx.ble.blelitelib.proxy;

import com.afunx.ble.blelitelib.operation.BleOperation;

/**
 * Created by afunx on 13/12/2016.
 */

public interface BleGattClientProxy {
//    private Context mAppContext;
//    private BluetoothDevice mTarget;

//    private BleConnector mConnector;


//    void register(BleOperation operation);
//
//    void unregister(BleOperation operation);

    /**
     * try to connect to ble device
     *
     * @param bleAddr the ble device's ble address
     * @param timeout timeout in milliseconds
     * @return connect suc or not in timeout
     */
    boolean connect(String bleAddr, long timeout);

    /**
     * close and release resources
     */
    void close();
}