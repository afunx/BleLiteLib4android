package com.afunx.ble.blelitelib.activity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.afunx.ble.blelitelib.app.R;
import com.afunx.ble.blelitelib.constant.Key;
import com.afunx.ble.blelitelib.proxy.BleGattClientProxy;
import com.afunx.ble.blelitelib.proxy.BleProxy;

import java.util.UUID;

public class PropertyListActivity extends AppCompatActivity {

    private static final String TAG = "PropertyListActivity";
    private BleGattClientProxy mBleGattClientProxy;
    private BluetoothGattService mGattService;

    public static void startActivity(Context context, BluetoothDevice bluetoothDevice, UUID serviceUuid) {
        Intent intent = new Intent(context, PropertyListActivity.class);
        intent.putExtra(Key.BLUETOOTH_DEVICE, bluetoothDevice);
        intent.putExtra(Key.BLUETOOTH_GATT_SERVICE_UUID, serviceUuid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_list);

        mBleGattClientProxy = BleProxy.getInstance().getProxy();

        BluetoothDevice bluetoothDevice = getIntent().getParcelableExtra(Key.BLUETOOTH_DEVICE);
        if (bluetoothDevice == null) {
            Toast.makeText(this, R.string.bluetooth_device_null, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        UUID serviceUuid = (UUID) getIntent().getSerializableExtra(Key.BLUETOOTH_GATT_SERVICE_UUID);

        if (serviceUuid == null) {
            Toast.makeText(this, R.string.service_uuid_null, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        BluetoothGattService gattService = mBleGattClientProxy.discoverService(serviceUuid, 0);
        if (gattService == null) {
            Toast.makeText(this, R.string.service_null, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        mGattService = gattService;
        Log.e(TAG, "gattService: " + gattService);
    }
}
