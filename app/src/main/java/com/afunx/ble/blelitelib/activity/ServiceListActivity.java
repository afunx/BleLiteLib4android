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
import android.widget.Button;

import com.afunx.ble.blelitelib.app.R;
import com.afunx.ble.blelitelib.constant.Key;
import com.afunx.ble.blelitelib.proxy.BleGattClientProxy;
import com.afunx.ble.blelitelib.proxy.BleProxy;
import com.afunx.ble.blelitelib.utils.BleUuidUtils;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ServiceListActivity extends AppCompatActivity {

    private static final boolean DEBUG = true;
    private static final String TAG = "ServiceListActivity";
    private BluetoothDevice mBluetoothDevice;
    private BleGattClientProxy mBleGattClientProxy;
    private Button mBtnConnect;
    private Button mBtnLog;

    public static void startActivity(Context context, BluetoothDevice bluetoothDevice) {
        Intent intent = new Intent(context, ServiceListActivity.class);
        intent.putExtra(Key.BLUETOOTH_DEVICE, bluetoothDevice);
        context.startActivity(intent);
    }

    private void connectAync(final BluetoothDevice bluetoothDevice) {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(-1);
                long timeoutMilli = 30000;
                long start = System.currentTimeMillis();
                boolean isConnectSuc = mBleGattClientProxy.connect(bluetoothDevice.getAddress(), timeoutMilli);
                long consume = System.currentTimeMillis() - start;
                testConnectConsumeTotal += consume;
                ++testTotalCount;
                Log.e("afunx", "isConnectSuc: " + isConnectSuc + ", consume: " + consume + ", count: " + testTotalCount + ", avg: " + (testConnectConsumeTotal * 1.0f / testTotalCount) + " ms");
                e.onNext(isConnectSuc ? 1 : 0);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Integer value) {
                if (DEBUG) {
                    Log.i(TAG, "connectAync() bluetoothDevice: " + bluetoothDevice + " is connected suc: " + value);
                }
                switch (value) {
                    // 开始连接
                    case -1:
                        mBtnConnect.setEnabled(false);
                        mBtnConnect.setText(R.string.btn_connecting);
                        break;
                    // 连接失败
                    case 0:
                        mBtnConnect.setEnabled(true);
                        mBtnConnect.setText(R.string.btn_connect);
                        break;
                    // 连接成功
                    case 1:
                        mBtnConnect.setEnabled(true);
                        mBtnConnect.setText(R.string.btn_disconnect);
//                        testAsync();
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

    private void disconnectAsync() {
        mBleGattClientProxy.close();
        mBtnConnect.setText(R.string.btn_connect);
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
                    connectAync(mBluetoothDevice);
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

        setTitle(mBluetoothDevice.getName() + ": " + mBluetoothDevice.getAddress());

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