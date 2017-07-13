package com.afunx.ble.blelitelib.operation;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.support.annotation.NonNull;
import android.util.Log;

import com.afunx.ble.blelitelib.log.BleLiteLog;
import com.afunx.ble.blelitelib.utils.HexUtils;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by afunx on 13/07/2017.
 */

public class BleWriteCharacteristicNoResponseInterruptOperation extends BleOperationAbs implements BleInterruptable {
    private static final String TAG = "BleWriteCharacteristicNoResponseInterruptOperation";
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

    public static BleWriteCharacteristicNoResponseInterruptOperation createInstance(@NonNull BluetoothGatt bluetoothGatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] msg) {
        return new BleWriteCharacteristicNoResponseInterruptOperation(bluetoothGatt, characteristic, msg);
    }

    private BleWriteCharacteristicNoResponseInterruptOperation(@NonNull BluetoothGatt bluetoothGatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] msg) {
        mBluetoothGatt = bluetoothGatt;
        mCharacteristic = characteristic;
        mMsg = msg;
    }

    @Override
    protected void clearConcurrentOperation() {

    }

    private static synchronized boolean sleep() {
        int select = mAutoId.getAndAdd(1) % PACKET_INTERVALS.length;
        int sleepTime = PACKET_INTERVALS[select] - (int) (System.currentTimeMillis() - lastTimestamp);
        if (sleepTime > 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ignore) {
                BleLiteLog.e(TAG, "sleep() is interrupted, return true");
                return true;
            }
        } else {
            return Thread.interrupted();
        }
        return false;
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

        synchronized (mAutoId) {
            mCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            for (int offset = 0; offset < size; offset += count) {
                boolean isInterrupted = sleep();
                if (isInterrupted) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateLastTimestamp();
                    break;
                }
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
                            BleLiteLog.e(TAG, "sleep() is interrupted, break");
                            updateLastTimestamp();
                            break;
                        }
                    }
                }
                BleLiteLog.i(TAG, "writeCharacteristic() " + HexUtils.bytes2HexString(bytePacket, 0, count) + ", isWriteSuc: " + isWriteSuc);
                updateLastTimestamp();
            }
        }
    }

    @Override
    public long getOperatcionCode() {
        return BLE_WRITE_CHARACTERISTIC_NO_RESPONSE_INTERRUPT;
    }
}
