package com.afunx.ble.blelitelib.scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.os.Build;

import java.util.UUID;

/**
 * Created by afunx on 08/11/2016.
 */

public class BleScanner {

    private static BluetoothAdapter getAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * Starts a scan for Bluetooth LE devices.
     * <p>
     * Results of the scan are reported using the
     * {@link BluetoothAdapter.LeScanCallback#onLeScan} callback.
     * <p>
     * {@link BluetoothAdapter#startLeScan(BluetoothAdapter.LeScanCallback)}
     *
     * @param serviceUuids Array of services to look for
     * @param callback     callback the callback LE scan results are delivered
     * @return true, if the scan was started successfully
     */
    public static boolean startLeScan(final UUID[] serviceUuids, final BluetoothAdapter.LeScanCallback callback) {
        BluetoothAdapter adapter = getAdapter();
        return adapter.startLeScan(serviceUuids, callback);
    }

    /**
     * Starts a scan for Bluetooth LE devices.
     * <p>
     * Results of the scan are reported using the
     * {@link BluetoothAdapter.LeScanCallback#onLeScan} callback.
     * <p>
     * {@link BluetoothAdapter#startLeScan(BluetoothAdapter.LeScanCallback)}
     *
     * @param callback callback the callback LE scan results are delivered
     * @return true, if the scan was started successfully
     */
    public static boolean startLeScan(final BluetoothAdapter.LeScanCallback callback) {
        BluetoothAdapter adapter = getAdapter();
        return adapter.startLeScan(callback);
    }

    /**
     * Stops an ongoing Bluetooth LE device scan.
     *
     * @param callback used to identify which scan to stop must be the same handle
     *                 used to start the scan
     *                 {@link BluetoothAdapter#stopLeScan(BluetoothAdapter.LeScanCallback)}
     */
    public static void stopLeScan(BluetoothAdapter.LeScanCallback callback) {
        BluetoothAdapter adapter = getAdapter();
        adapter.stopLeScan(callback);
    }
}
