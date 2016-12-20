package com.afunx.ble.blelitelib.utils;

/**
 * Created by afunx on 15/12/2016.
 */

// refer to android/external/bluetooth/bluedroid/stack/include/gatt_api.h
public class BleGattStatusParser {
    public static String parse(final int error) {
        switch (error) {
            case 0x0000:
                return "GATT SUCCESS";
            case 0x0001:
                return "GATT INVALID HANDLE";
            case 0x0002:
                return "GATT READ NOT PERMIT";
            case 0x0003:
                return "GATT WRITE NOT PERMIT";
            case 0x0004:
                return "GATT INVALID PDU";
            case 0x0005:
                return "GATT INSUF AUTHENTICATION";
            case 0x0006:
                return "GATT REQ NOT SUPPORTED";
            case 0x0007:
                return "GATT INVALID OFFSET";
            case 0x0008:
                return "GATT INSUF AUTHORIZATION";
            case 0x0009:
                return "GATT PREPARE Q FULL";
            case 0x000a:
                return "GATT NOT FOUND";
            case 0x000b:
                return "GATT NOT LONG";
            case 0x000c:
                return "GATT INSUF KEY SIZE";
            case 0x000d:
                return "GATT INVALID ATTR LEN";
            case 0x000e:
                return "GATT ERR UNLIKELY";
            case 0x000f:
                return "GATT INSUF ENCRYPTION";
            case 0x0010:
                return "GATT UNSUPPORT GRP TYPE";
            case 0x0011:
                return "GATT INSUF RESOURCE";

            case 0x0080:
                return "GATT NO RESOURCES";
            case 0x0081:
                return "GATT INTERNAL ERROR";
            case 0x0082:
                return "GATT WRONG STATE";
            case 0x0083:
                return "GATT DB FULL";
            case 0x0084:
                return "GATT BUSY";
            case 0x0085:
                return "GATT ERROR";
            case 0x0086:
                return "GATT CMD STARTED";
            case 0x0087:
                return "GATT ILLEGAL PARAMETER";
            case 0x0088:
                return "GATT PENDING";
            case 0x0089:
                return "GATT AUTH FAIL";
            case 0x008a:
                return "GATT MORE";
            case 0x008b:
                return "GATT INVALID CFG";
            case 0x008c:
                return "GATT SERVICE STARTED";
            case 0x008d:
                return "GATT ENCRYPED NO MITM";
            case 0x008e:
                return "GATT NOT ENCRYPTED";
        }
        return String.format("UNKNOWN (0x%04x)", error & 0xFFFF);
    }
}
