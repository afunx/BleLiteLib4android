package com.afunx.ble.blelitelib.utils;

import android.bluetooth.BluetoothProfile;

/**
 * Created by afunx on 15/12/2016.
 */

public class BleGattStateParser {
    public static String parse(final int state) {
        switch (state) {
            case BluetoothProfile.STATE_DISCONNECTED:
                return "STATE DISCONNECTED";
            case BluetoothProfile.STATE_CONNECTING:
                return "STATE CONNECTING";
            case BluetoothProfile.STATE_CONNECTED:
                return "STATE CONNECTED";
            case BluetoothProfile.STATE_DISCONNECTING:
                return "STATE DISCONNECTING";
        }
        return String.format("UNKNOWN (0x%04x)", state & 0xFFFF);
    }
}
