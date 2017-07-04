package com.afunx.ble.blelitelib.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
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


public class ScanActivity extends AppCompatActivity {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private BleDeviceAdapter mBleDeviceAdapter;
    private Handler mHandler;
    private ListView mListView;

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

        BleProxy.getInstance().init(getApplicationContext());
        doRefresh();
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
