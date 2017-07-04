package com.afunx.ble.blelitelib.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.afunx.ble.blelitelib.adapter.BleDeviceAdapter;
import com.afunx.ble.blelitelib.app.R;
import com.afunx.ble.blelitelib.bean.BleDevice;
import com.afunx.ble.blelitelib.proxy.BleProxy;
import com.afunx.ble.blelitelib.scanner.BleScanner;
import com.afunx.ble.blelitelib.test.TestActivity;
import com.afunx.ble.blelitelib.utils.BleUtils;

import java.util.ArrayList;
import java.util.List;


public class ScanActivity extends AppCompatActivity {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private BleDeviceAdapter mBleDeviceAdapter;
    private Handler mHandler;
    private ListView mListView;

    private int REQUEST_CODE = 1;

    private void requestBleAuthority() {

        List<String> permissions = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.BLUETOOTH);
            }
        }

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.BLUETOOTH_ADMIN);
            }
        }

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
        }

        if (!permissions.isEmpty()) {
            String[] requestPermissions = new String[permissions.size()];
            int offset = 0;
            for (String permission : permissions) {
                requestPermissions[offset++] = permission;
            }
            ActivityCompat.requestPermissions(this,
                    requestPermissions,
                    REQUEST_CODE);
        } else {
            doRefresh();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            boolean isForbidden = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != 0) {
                    isForbidden = true;
                    break;
                }
            }
            if (isForbidden) {
                Toast.makeText(this, "无法获得蓝牙相关全部权限", Toast.LENGTH_LONG).show();
            } else {
                doRefresh();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mHandler = new Handler();
        mListView = (ListView) findViewById(R.id.lv_devices);
        mBleDeviceAdapter = new BleDeviceAdapter(this);
        mListView.setAdapter(mBleDeviceAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BleDevice bleDevice = (BleDevice) mBleDeviceAdapter.getItem(position);
//                ServiceListActivity.startActivity(ScanActivity.this, bleDevice.getBluetoothDevice());
                TestActivity.startActivity(ScanActivity.this, bleDevice.getBluetoothDevice());
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
            
        });

        mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

            private long timestamp = 0;
            private final long interval = 200;

            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                BleDevice bleDevice = new BleDevice();
                bleDevice.setBluetoothDevice(device);
                bleDevice.setRssi(rssi);
                bleDevice.setScanRecord(scanRecord);
                if (mBleDeviceAdapter.addOrUpdateDevice(bleDevice)) {
                    timestamp = System.currentTimeMillis();
                    mBleDeviceAdapter.notifyDataSetChanged();
                } else {
                    if (System.currentTimeMillis() - timestamp > interval) {
                        timestamp = System.currentTimeMillis();
                        mBleDeviceAdapter.notifyDataSetChanged();
                    }
                }
            }
        };

        BleUtils.enableBluetooth();

        BleProxy.getInstance().init(getApplicationContext());
        requestBleAuthority();
    }

    private void doRefresh() {
        final long interval = 5000;
        mSwipeRefreshLayout.setRefreshing(true);
        Toast.makeText(this, R.string.scanning, Toast.LENGTH_LONG).show();
        // clear ble devices in UI
        mBleDeviceAdapter.clear();
        new Thread() {
            public void run() {
                BleScanner.startLeScan(mLeScanCallback);
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException ignore) {
                }
                BleScanner.stopLeScan(mLeScanCallback);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // stop swipe refresh refreshing
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }.start();
    }
}
