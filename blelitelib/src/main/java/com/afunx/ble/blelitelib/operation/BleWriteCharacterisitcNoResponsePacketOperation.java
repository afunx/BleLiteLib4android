package com.afunx.ble.blelitelib.operation;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.support.annotation.NonNull;
import android.util.Log;

import com.afunx.ble.blelitelib.log.BleLiteLog;
import com.afunx.ble.blelitelib.utils.HexUtils;

import java.util.Arrays;

/**
 * Created by afunx on 21/06/2017.
 */

public class BleWriteCharacterisitcNoResponsePacketOperation extends BleOperationAbs {
    private static final String TAG = "BleWriteCharacterisitcNoResponsePacketOperation";
    private final BluetoothGatt mBluetoothGatt;
    private final BluetoothGattCharacteristic mCharacteristic;
    private final byte[] mMsg;

    private final int mPacketSize;
    private final int mPacketInterval;

    public static BleWriteCharacterisitcNoResponsePacketOperation createInstance(@NonNull BluetoothGatt bluetoothGatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] msg, int packetSize, int packetInterval) {
        return new BleWriteCharacterisitcNoResponsePacketOperation(bluetoothGatt, characteristic, msg, packetSize, packetInterval);
    }

    private BleWriteCharacterisitcNoResponsePacketOperation(@NonNull BluetoothGatt bluetoothGatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] msg, int packetSize, int packetInterval) {
        mBluetoothGatt = bluetoothGatt;
        mCharacteristic = characteristic;
        mMsg = msg;
        mPacketSize = packetSize;
        mPacketInterval = packetInterval;
    }

    @Override
    protected void clearConcurrentOperation() {

    }

    @Override
    public void run() {
        mCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        final int packetSize = mPacketSize;
        final int packetInterval = mPacketInterval;
        final int size = mMsg.length;
        byte[] bytePacket;
        int count;

        for (int offset = 0; offset < size; offset += packetSize) {
            if (offset + packetSize <= size) {
                count = packetSize;
            } else {
                count = size - offset;
            }
            bytePacket = Arrays.copyOfRange(mMsg, offset, offset + count);
            mCharacteristic.setValue(bytePacket);
            mBluetoothGatt.writeCharacteristic(mCharacteristic);
            BleLiteLog.i(TAG, "writeCharacteristic() " + HexUtils.bytes2HexString(bytePacket, 0, count));
            if (packetInterval > 0) {
                try {
                    Thread.sleep(packetInterval);
                } catch (InterruptedException ignore) {
                    return;
                }
            }
        }
    }

    @Override
    public int getOperatcionCode() {
        return BLE_WRITE_CHARACTERISITC_NO_RESPONSE_PACKET;
    }
}
