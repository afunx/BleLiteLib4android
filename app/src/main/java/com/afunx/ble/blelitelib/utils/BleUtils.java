package com.afunx.ble.blelitelib.utils;

/**
 * Created by afunx on 08/11/2016.
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

public class BleUtils {

    /**
     * Check whether the bluetooth is enabled.
     * {@link BluetoothAdapter#isEnabled()}
     *
     * @return whether the bluetooth is enabled
     */
    public static boolean isBluetoothEnabled() {
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    /**
     * Enable bluetooth brutally.
     * Don't use without explicit user action to turn on Bluetooth.
     * {@link BluetoothAdapter#enable()}
     *
     * @return true to indicate adapter startup has begun, or false on
     * immediate error
     */
    public static boolean enableBluetooth() {
        return BluetoothAdapter.getDefaultAdapter().enable();
    }

    /**
     * Disable bluetooth brutally.
     * Don't use without explicit user action to turn off Bluetooth.
     * {@link BluetoothAdapter#disable()}
     *
     * @return true to indicate adapter shutdown has begun, or false on
     * immediate error
     */
    public static boolean disableBluetooth() {
        return BluetoothAdapter.getDefaultAdapter().disable();
    }

    /**
     * Try to enable bluetooth. If bluetooth has been enabled,
     * {@link Activity#onActivityResult(int, int, Intent)} will be invoked and resultCode will
     * be {@link Activity#RESULT_OK}
     * <p>
     * {@link BluetoothAdapter#ACTION_REQUEST_ENABLE}
     *
     * @param activity    the activity who ask for enable bluetooth
     * @param requestCode the request code
     */
    public static void tryEnableBluetooth(Activity activity, int requestCode) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, requestCode);
    }
}