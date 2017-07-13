package com.afunx.ble.blelitelib.operation;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.support.annotation.NonNull;
import android.util.Log;

import com.afunx.ble.blelitelib.log.BleLiteLog;
import com.afunx.ble.blelitelib.utils.HexUtils;

import java.util.Arrays;

import static com.afunx.ble.blelitelib.operation.BleOperation.BLE_WRITE_CHARACTERISTIC_NO_RESPONSE_20;

/**
 * Created by afunx on 20/06/2017.
 */

public class BleWriteCharacteristicNoResponse20Operation extends BleOperationAbs {

    private static final String TAG = "BleWriteCharacteristicNoResponse20Operation";
    private final BluetoothGatt mBluetoothGatt;
    private final BluetoothGattCharacteristic mCharacteristic;
    private final byte[] mMsg;

    private final static int TWEENTY = 20;

    public static BleWriteCharacteristicNoResponse20Operation createInstance(@NonNull BluetoothGatt bluetoothGatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] msg) {
        return new BleWriteCharacteristicNoResponse20Operation(bluetoothGatt, characteristic, msg);
    }

    private BleWriteCharacteristicNoResponse20Operation(@NonNull BluetoothGatt bluetoothGatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] msg) {
        mBluetoothGatt = bluetoothGatt;
        mCharacteristic = characteristic;
        mMsg = msg;
        Log.e("NoResponse20Operation", "msg: " + HexUtils.bytes2HexString(msg));
    }

    @Override
    protected void clearConcurrentOperation() {

    }

    @Override
    public void run() {
        mCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);

        byte[] byte20;
        final int size = mMsg.length;
        int count;
        for (int offset = 0; offset < size; offset += TWEENTY) {
            if (offset + TWEENTY <= size) {
                count = 20;
            } else {
                count = size - offset;
            }
            byte20 = Arrays.copyOfRange(mMsg, offset, offset + count);
            mCharacteristic.setValue(byte20);
            mBluetoothGatt.writeCharacteristic(mCharacteristic);
            BleLiteLog.i(TAG, "writeCharacteristic() " + HexUtils.bytes2HexString(byte20, 0, count));
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getOperatcionCode() {
        return BLE_WRITE_CHARACTERISTIC_NO_RESPONSE_20;
    }
}