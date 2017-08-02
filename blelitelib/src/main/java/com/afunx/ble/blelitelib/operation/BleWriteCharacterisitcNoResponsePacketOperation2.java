package com.afunx.ble.blelitelib.operation;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.support.annotation.NonNull;

import com.afunx.ble.blelitelib.log.BleLiteLog;
import com.afunx.ble.blelitelib.utils.HexUtils;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by afunx on 21/06/2017.
 */

public class BleWriteCharacterisitcNoResponsePacketOperation2 extends BleOperationAbs {
    private static final String TAG = "BleWriteCharacterisitcNoResponsePacketOperation2";
    private final BluetoothGatt mBluetoothGatt;
    private final BluetoothGattCharacteristic mCharacteristic;
    private final byte[] mMsg;

    private static final int PACKET_SIZE = 20;
    private static int[] PACKET_INTERVALS = new int[]{5, 5, 5, 35};

    private static final AtomicInteger mAutoId = new AtomicInteger(0);
    private static volatile long lastTimestamp = 0;

    public static void setPacketIntervals(int[] packetIntervals) {
        PACKET_INTERVALS = packetIntervals;
    }

    public static BleWriteCharacterisitcNoResponsePacketOperation2 createInstance(@NonNull BluetoothGatt bluetoothGatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] msg) {
        return new BleWriteCharacterisitcNoResponsePacketOperation2(bluetoothGatt, characteristic, msg);
    }

    private BleWriteCharacterisitcNoResponsePacketOperation2(@NonNull BluetoothGatt bluetoothGatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] msg) {
        mBluetoothGatt = bluetoothGatt;
        mCharacteristic = characteristic;
        mMsg = msg;
    }

    @Override
    protected void clearConcurrentOperation() {

    }

    private static synchronized void sleep() {
        int select = mAutoId.getAndAdd(1) % PACKET_INTERVALS.length;
        int sleepTime = lastTimestamp == 0 ? 0 : PACKET_INTERVALS[select] - (int) (System.currentTimeMillis() - lastTimestamp);
        if (sleepTime > 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ignore) {
            }
        }
    }

    private static synchronized void updateLastTimestamp() {
        lastTimestamp = System.currentTimeMillis();
    }

    @Override
    public void run() {
        final int packetSize = PACKET_SIZE;
        final int size = mMsg.length;
        byte[] bytePacket;
        int count;
        int retry = 0;

        mCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        for (int offset = 0; offset < size; offset += count) {
            sleep();
            if (offset + packetSize <= size) {
                count = packetSize;
            } else {
                count = size - offset;
            }
            bytePacket = Arrays.copyOfRange(mMsg, offset, offset + count);
            mCharacteristic.setValue(bytePacket);
            boolean isWriteSuc = mBluetoothGatt.writeCharacteristic(mCharacteristic);
            if (isWriteSuc) {
                retry = 0;
            } else {
                ++retry;
                if (retry > 3) {
                    BleLiteLog.e(TAG, "writeCharacteristic() fail");
                    return;
                } else {
                    offset -= count;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            BleLiteLog.i(TAG, "writeCharacteristic() " + HexUtils.bytes2HexString(bytePacket, 0, count) + ", isWriteSuc: " + isWriteSuc);
            updateLastTimestamp();
        }
    }

    @Override
    public int getOperatcionCode() {
        return BLE_WRITE_CHARACTERISITC_NO_RESPONSE_PACKET2;
    }
}
