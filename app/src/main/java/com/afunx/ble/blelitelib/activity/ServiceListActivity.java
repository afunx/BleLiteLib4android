package com.afunx.ble.blelitelib.activity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.afunx.ble.blelitelib.adapter.BleServiceAdapter;
import com.afunx.ble.blelitelib.app.R;
import com.afunx.ble.blelitelib.constant.Key;
import com.afunx.ble.blelitelib.proxy.BleGattClientProxy;
import com.afunx.ble.blelitelib.proxy.BleProxy;
import com.afunx.ble.blelitelib.utils.BleUuidUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ServiceListActivity extends AppCompatActivity {

    interface STEP {
        int START = -1;
        int SUC = 1;
        int FAIL = 0;
    }

    private static final boolean DEBUG = true;
    private static final String TAG = "ServiceListActivity";
    private BluetoothDevice mBluetoothDevice;
    private BleGattClientProxy mBleGattClientProxy;
    private Button mBtnConnect;
    private Button mBtnLog;
    private BleServiceAdapter mBleServiceAdapter;
    private final List<BluetoothGattService> mBleServices = new ArrayList<>();

    public static void startActivity(Context context, BluetoothDevice bluetoothDevice) {
        Intent intent = new Intent(context, ServiceListActivity.class);
        intent.putExtra(Key.BLUETOOTH_DEVICE, bluetoothDevice);
        context.startActivity(intent);
    }

    /**
     * 连接蓝牙
     *
     * @param bluetoothDevice
     */
    private void connectAsync(final BluetoothDevice bluetoothDevice) {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(STEP.START);
                long timeoutMilli = 30000;
                long start = System.currentTimeMillis();
                boolean isConnectSuc = mBleGattClientProxy.connect(bluetoothDevice.getAddress(), timeoutMilli);
                long consume = System.currentTimeMillis() - start;
                testConnectConsumeTotal += consume;
                ++testTotalCount;
                Log.e("afunx", "isConnectSuc: " + isConnectSuc + ", consume: " + consume + ", count: " + testTotalCount + ", avg: " + (testConnectConsumeTotal * 1.0f / testTotalCount) + " ms");
                e.onNext(isConnectSuc ? STEP.SUC : STEP.FAIL);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Integer value) {
                if (DEBUG) {
                    Log.i(TAG, "connectAsync() bluetoothDevice: " + bluetoothDevice + " is connected suc: " + value);
                }
                switch (value) {
                    // 开始连接
                    case STEP.START:
                        mBtnConnect.setEnabled(false);
                        mBtnConnect.setText(R.string.btn_connecting);
                        break;
                    // 连接失败
                    case STEP.FAIL:
                        mBtnConnect.setEnabled(true);
                        mBtnConnect.setText(R.string.btn_connect);
                        break;
                    // 连接成功
                    case STEP.SUC:
                        mBtnConnect.setEnabled(true);
                        mBtnConnect.setText(R.string.btn_disconnect);
//                        new Thread() {
//                            @Override
//                            public void run() {
//                                testAsync();
//                            }
//                        }.start();

                        discoverServicesAsync(mBluetoothDevice);
                        break;
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        };
        observable.subscribe(observer);
    }

    /**
     * 发现服务
     *
     * @param bluetoothDevice
     */
    private void discoverServicesAsync(final BluetoothDevice bluetoothDevice) {

        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(STEP.START);
                long timeoutMilli = 30000;
                mBleServices.clear();
                List<BluetoothGattService> gattServices = mBleGattClientProxy.discoverServices(timeoutMilli);
                if (gattServices != null) {
                    mBleServices.addAll(gattServices);
                    e.onNext(STEP.SUC);
                } else {
                    e.onNext(STEP.FAIL);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e("afunx", "onSubscribe");
            }

            @Override
            public void onNext(Integer value) {
                switch (value) {
                    // 开始扫描
                    case STEP.START:
                        if (DEBUG) {
                            Log.i(TAG, "discoverServicesAsync() start");
                        }
                        mBtnConnect.setEnabled(false);
                        mBtnConnect.setText(R.string.btn_discovering);
                        mBleServiceAdapter.clear();
                        mBleServiceAdapter.notifyDataSetChanged();
                        break;
                    // 扫描成功
                    case STEP.SUC:
                        if (DEBUG) {
                            Log.i(TAG, "discoverServicesAsync() suc");
                        }
                        mBtnConnect.setEnabled(true);
                        mBtnConnect.setText(R.string.btn_disconnect);
                        mBleServiceAdapter.addResults(mBleServices);
                        mBleServiceAdapter.notifyDataSetChanged();
                        break;
                    // 扫描失败
                    case STEP.FAIL:
                        if (DEBUG) {
                            Log.i(TAG, "discoverServicesAsync() fail");
                        }
                        mBtnConnect.setEnabled(true);
                        mBtnConnect.setText(R.string.btn_disconnect);
                        mBleServiceAdapter.clear();
                        mBleServiceAdapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e("afunx", "onError");
            }

            @Override
            public void onComplete() {
                Log.e("afunx", "onComplete");
            }
        };
        observable.subscribe(observer);
    }

    private void disconnectAsync() {
        mBleGattClientProxy.close();
        mBtnConnect.setText(R.string.btn_connect);
        mBleServiceAdapter.clear();
        mBleServiceAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_service_list);

        mBtnConnect = (Button) findViewById(R.id.btn_connect);
        mBtnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBtnConnect.getText().toString().equals(getString(R.string.btn_connect))) {
                    if (DEBUG) {
                        Log.i(TAG, "btnConnect connectAsync()");
                    }
                    connectAsync(mBluetoothDevice);
                } else if (mBtnConnect.getText().toString().equals(getString(R.string.btn_disconnect))) {
                    if (DEBUG) {
                        Log.i(TAG, "btnConnect disconnectAsync()");
                    }
                    disconnectAsync();
                } else {
                    if (DEBUG) {
                        Log.e(TAG, "btnConnect IllegalStateException");
                    }
                    throw new IllegalStateException();
                }
            }
        });

        mBtnLog = (Button) findViewById(R.id.btn_log);
        mBtnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("afunx", "log");
            }
        });

        mBleGattClientProxy = BleProxy.getInstance().getProxy();

        mBluetoothDevice = getIntent().getParcelableExtra(Key.BLUETOOTH_DEVICE);

        if (DEBUG) {
            Log.i(TAG, "onCreate() mBluetoothDevice: " + mBluetoothDevice);
        }

        mBleServiceAdapter = new BleServiceAdapter(this);
        ListView listView_device = (ListView) findViewById(R.id.list_service);
        listView_device.setAdapter(mBleServiceAdapter);
        listView_device.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothGattService service = mBleServiceAdapter.getItem(position);
//                mBluetoothService.setService(service);
//                ((OperationActivity) getActivity()).changePage(1);
                Log.e("afunx", "service: " + service + " is clicked " + service.getUuid());
                PropertyListActivity.startActivity(ServiceListActivity.this, mBluetoothDevice, service.getUuid());
            }
        });

        mBleGattClientProxy.setDisconnectCallback(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBtnConnect.setText(R.string.btn_connect);
                    }
                });
            }
        }, 5000);

        TextView textViewDevName = (TextView) findViewById(R.id.tv_dev_name);
        textViewDevName.setText(getString(R.string.device_name, mBluetoothDevice.getName()));
        TextView textViewDevAddr = (TextView) findViewById(R.id.tv_dev_addr);
        textViewDevAddr.setText(getString(R.string.device_addr, mBluetoothDevice.getAddress()));

        testConnectConsumeTotal = 0;
        testNotifyConsumeTotal = 0;
        testTotalCount = 0;
        mBtnConnect.performClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectAsync();
    }

    /**
     * Test Async start
     */
    private volatile long testConnectConsumeTotal;
    private volatile long testNotifyConsumeTotal;
    private volatile long testTotalCount;

    private void testAsync() {
        BluetoothGattService gattWriteService = mBleGattClientProxy.discoverService(BleUuidUtils.int2uuid(0xffe5), 30000);
        if (gattWriteService == null) {
            Log.e("afunx", "gattWriteService is null");
            return;
        }
        BluetoothGattService gattNotifyService = mBleGattClientProxy.discoverService(BleUuidUtils.int2uuid(0xffe0), 30000);
        if (gattNotifyService == null) {
            Log.e("afunx", "gattNotifyService is null");
            return;
        }
        BluetoothGattCharacteristic gattCharacteristicNotify = mBleGattClientProxy.discoverCharacteristic(gattNotifyService, BleUuidUtils.int2uuid(0xffe4));
        BluetoothGattCharacteristic gattCharacteristicWrite = mBleGattClientProxy.discoverCharacteristic(gattWriteService, BleUuidUtils.int2uuid(0xffe9));

        final AtomicLong timestamp = new AtomicLong(0);
        mBleGattClientProxy.registerCharacteristicNotification(gattCharacteristicNotify, new BleGattClientProxy.OnCharacteristicNotificationListener() {
            @Override
            public void onCharacteristicNotification(byte[] msg) {
                long consume = System.currentTimeMillis() - timestamp.get();
                testNotifyConsumeTotal += consume;
                Log.e("afunx", "consume: " + consume + " ms, bytes: " + Arrays.toString(msg) + ", count: " + testTotalCount + ", avg: " + (testNotifyConsumeTotal * 1.0f / testTotalCount) + " ms");
            }
        });
        timestamp.set(System.currentTimeMillis());
        mBleGattClientProxy.writeCharacteristic(gattCharacteristicWrite, new byte[]{0x01, 0x02}, 1000);
    }
    /**
     * Test Async end
     */
}