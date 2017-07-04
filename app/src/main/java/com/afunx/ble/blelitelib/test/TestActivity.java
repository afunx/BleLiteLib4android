package com.afunx.ble.blelitelib.test;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afunx.ble.blelitelib.app.R;
import com.afunx.ble.blelitelib.constant.Key;
import com.afunx.ble.blelitelib.proxy.BleGattClientProxy;
import com.afunx.ble.blelitelib.proxy.BleProxy;
import com.afunx.ble.blelitelib.utils.BleUuidUtils;
import com.afunx.ble.blelitelib.utils.HexUtils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = "TestActivity";
    private BleGattClientProxy mBleGattClientProxy;
    private BluetoothDevice mBluetoothDevice;
    private ProgressDialog mProgressDialog;

    private TestRecorder mConnectRecorder;
    private TestRecorder mDiscoverServicesRecorder;
    private TestRecorder mWriteNotifyRecorder;
    private TestRecorder mWriteNotifyLargeRecorder;
    private EditText mEdtTestCount;
    private Button mBtnConfirm;

    private String mPhoneModel;

    private static final UUID GATT_WRITE_SERVICE_UUID = BleUuidUtils.int2uuid(0xffe5);

    private static final UUID GATT_NOTIFY_SERVICE_UUID = BleUuidUtils.int2uuid(0xffe0);

    private static final UUID GATT_WRITE_PROPERTY_UUID = BleUuidUtils.int2uuid(0xffe9);

    private static final UUID GATT_NOTIFY_PROPERTY_UUID = BleUuidUtils.int2uuid(0xffe4);

    public static void startActivity(Context context, BluetoothDevice bluetoothDevice) {
        Intent intent = new Intent(context, TestActivity.class);
        intent.putExtra(Key.BLUETOOTH_DEVICE, bluetoothDevice);
        context.startActivity(intent);
    }

    private void updateProgressDialogTitle(final String title) {
        Log.e("afunx", "updateProgressDialogTitle() title: " + title);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.setTitle(title);
            }
        });
    }

    private void dismissProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.dismiss();
            }
        });
    }

    private void setEnabledConfirmBtn(final boolean enabled) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBtnConfirm.setEnabled(enabled);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mPhoneModel = "MODEL: " + Build.MODEL + ", RELEASE: " + Build.VERSION.RELEASE;

        mBleGattClientProxy = BleProxy.getInstance().getProxy();

        mBluetoothDevice = getIntent().getParcelableExtra(Key.BLUETOOTH_DEVICE);

        mEdtTestCount = (EditText) findViewById(R.id.edt_test_count);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCanceledOnTouchOutside(false);

        TextView textViewDevName = (TextView) findViewById(R.id.tv_dev_name);
        textViewDevName.setText(getString(R.string.device_name, mBluetoothDevice.getName()));
        TextView textViewDevAddr = (TextView) findViewById(R.id.tv_dev_addr);
        textViewDevAddr.setText(getString(R.string.device_addr, mBluetoothDevice.getAddress()));

        mBtnConfirm = (Button) findViewById(R.id.btn_start_test);
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final int testCount = Integer.parseInt(mEdtTestCount.getText().toString());
                    mProgressDialog.setTitle("Test starting...");
                    mProgressDialog.show();
                    new Thread() {
                        @Override
                        public void run() {
                            mConnectRecorder = new TestRecorder("Connect");
                            mDiscoverServicesRecorder = new TestRecorder("DiscoverService");
                            mWriteNotifyRecorder = new TestRecorder("WriteNotify");
                            mWriteNotifyLargeRecorder = new TestRecorder("WriteLargeNotify");

                            testConnectAndDiscoverServices(testCount);
                            testWriteNotify(testCount);
                            testWriteNotifyLargeRecorder(testCount);
                            showTestResult();
                            dismissProgressDialog();
                        }
                    }.start();
                    setEnabledConfirmBtn(false);
                } catch (NumberFormatException e) {
                    Toast.makeText(TestActivity.this, "请输入一个正整数", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void testConnectAndDiscoverServices(int testCount) {
        for (int count = 0; count < testCount; count++) {
            updateProgressDialogTitle("Test Connect and Discover Services(1/3) "
                    + (count + 1) + " time" + " (total: " + testCount + " time)");
            // connect test
            mConnectRecorder.start();
            boolean isConnected = mBleGattClientProxy.connect(mBluetoothDevice.getAddress(), 30 * 1000);
            mConnectRecorder.stop(isConnected);
            if (isConnected) {
                // discover service test
                mDiscoverServicesRecorder.start();
                List<BluetoothGattService> gattServiceList = mBleGattClientProxy.discoverServices(30 * 1000);
                mDiscoverServicesRecorder.stop(gattServiceList != null);
            }
            mBleGattClientProxy.close();
        }
    }

    private void testWriteNotify(int testCount) {
        boolean isSuc = false;
        for (int retry = 0; retry < 3 && !isSuc; retry++) {
            updateProgressDialogTitle("Test Write and Notify(2/3) connect device and discover services " + (retry + 1) + " time");
            isSuc = mBleGattClientProxy.connect(mBluetoothDevice.getAddress(), 30 * 1000);
            if (isSuc) {
                List<BluetoothGattService> gattServiceList = mBleGattClientProxy.discoverServices(30 * 1000);
                isSuc = gattServiceList != null;
            }
        }

        if (!isSuc) {
            for (int count = 0; count < testCount; count++) {
                mWriteNotifyRecorder.start();
                mWriteNotifyRecorder.stop(false);
            }
            return;
        }

        for (int count = 0; count < testCount; count++) {
            updateProgressDialogTitle("Test Write and Notify(2/3) "
                    + (count + 1) + " time" + " (total: " + testCount + " time)");

            // write notify test
            BluetoothGattService gattWriteService = mBleGattClientProxy.discoverService(GATT_WRITE_SERVICE_UUID, 0);
            if (gattWriteService == null) {
                Log.e(TAG, "gattWriteService is null");
                mWriteNotifyRecorder.stop(false);
                break;
            }
            BluetoothGattCharacteristic gattWriteCharacteristic = mBleGattClientProxy.discoverCharacteristic(gattWriteService, GATT_WRITE_PROPERTY_UUID);
            if (gattWriteCharacteristic == null) {
                mWriteNotifyRecorder.stop(false);
                break;
            }
            BluetoothGattService gattNotifyService = mBleGattClientProxy.discoverService(GATT_NOTIFY_SERVICE_UUID, 0);
            if (gattNotifyService == null) {
                mWriteNotifyRecorder.stop(false);
                break;
            }
            BluetoothGattCharacteristic gattNotifyCharacteristic = mBleGattClientProxy.discoverCharacteristic(gattNotifyService, GATT_NOTIFY_PROPERTY_UUID);
            if (gattNotifyCharacteristic == null) {
                mWriteNotifyRecorder.stop(false);
                break;
            }

            final byte[] bytes = new byte[]{0x01};
            final AtomicInteger offset = new AtomicInteger(0);
            final AtomicBoolean fastFail = new AtomicBoolean(false);

            final Semaphore semaphore = new Semaphore(0);
            mBleGattClientProxy.registerCharacteristicNotification(gattNotifyCharacteristic, new BleGattClientProxy.OnCharacteristicNotificationListener() {
                @Override
                public void onCharacteristicNotification(byte[] msg) {
                    // check length
                    if (offset.get() + msg.length > bytes.length) {
                        fastFail.set(true);
                        semaphore.release();
                        Log.e(TAG, "offset.get() + msg.length > bytes.length");
                    }
                    // check content
                    for (int i = 0; i < msg.length; i++) {
                        if (msg[i] != bytes[offset.get() + i]) {
                            fastFail.set(true);
                            semaphore.release();
                            Log.e(TAG, "msg[i] != bytes[offset.get() + i]");
                        }
                    }
                    // check suc
                    offset.set(offset.get() + msg.length);
                    if (offset.get() == bytes.length) {
                        semaphore.release();
                    }
                }
            });

            boolean isNotified = false;
            mWriteNotifyRecorder.start();
            mBleGattClientProxy.writeCharacteristicNoResponse(gattWriteCharacteristic, bytes, 0);
            try {
                Log.e("afunx", "notify start");
                isNotified = semaphore.tryAcquire(2000, TimeUnit.MILLISECONDS);
                Log.e("afunx", "notify end " + isNotified);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mWriteNotifyRecorder.stop(isNotified && !fastFail.get());
        }

        mBleGattClientProxy.close();
    }

    private void testWriteNotifyLargeRecorder(int testCount) {
        boolean isSuc = false;
        for (int retry = 0; retry < 3 && !isSuc; retry++) {
            updateProgressDialogTitle("Test Write and Notify Large(3/3) connect device and discover services " + (retry + 1) + " time");
            isSuc = mBleGattClientProxy.connect(mBluetoothDevice.getAddress(), 30 * 1000);
            if (isSuc) {
                List<BluetoothGattService> gattServiceList = mBleGattClientProxy.discoverServices(30 * 1000);
                isSuc = gattServiceList != null;
            }
        }

        if (!isSuc) {
            for (int count = 0; count < testCount; count++) {
                mWriteNotifyLargeRecorder.start();
                mWriteNotifyLargeRecorder.stop(false);
            }
            return;
        }

        for (int count = 0; count < testCount; count++) {
            updateProgressDialogTitle("Test Write and Notify Large(3/3) "
                    + (count + 1) + " time" + " (total: " + testCount + " time)");

            // write notify test
            BluetoothGattService gattWriteService = mBleGattClientProxy.discoverService(GATT_WRITE_SERVICE_UUID, 0);
            if (gattWriteService == null) {
                Log.e(TAG, "gattWriteService is null");
                mWriteNotifyLargeRecorder.stop(false);
                break;
            }
            BluetoothGattCharacteristic gattWriteCharacteristic = mBleGattClientProxy.discoverCharacteristic(gattWriteService, GATT_WRITE_PROPERTY_UUID);
            if (gattWriteCharacteristic == null) {
                mWriteNotifyLargeRecorder.stop(false);
                break;
            }
            BluetoothGattService gattNotifyService = mBleGattClientProxy.discoverService(GATT_NOTIFY_SERVICE_UUID, 0);
            if (gattNotifyService == null) {
                mWriteNotifyLargeRecorder.stop(false);
                break;
            }
            BluetoothGattCharacteristic gattNotifyCharacteristic = mBleGattClientProxy.discoverCharacteristic(gattNotifyService, GATT_NOTIFY_PROPERTY_UUID);
            if (gattNotifyCharacteristic == null) {
                mWriteNotifyLargeRecorder.stop(false);
                break;
            }

            final byte[] bytes = new byte[100];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) i;
            }
            final AtomicInteger offset = new AtomicInteger(0);
            final AtomicBoolean fastFail = new AtomicBoolean(false);

            final Semaphore semaphore = new Semaphore(0);
            mBleGattClientProxy.registerCharacteristicNotification(gattNotifyCharacteristic, new BleGattClientProxy.OnCharacteristicNotificationListener() {
                @Override
                public void onCharacteristicNotification(byte[] msg) {
                    Log.e("afunx", "onCharacteristicNotification() msg: " + HexUtils.bytes2HexString(msg));
                    // check length
                    if (offset.get() + msg.length > bytes.length) {
                        fastFail.set(true);
                        semaphore.release();
                        Log.e(TAG, "offset.get() + msg.length > bytes.length");
                    }
                    // check content
                    for (int i = 0; i < msg.length; i++) {
                        if (msg[i] != bytes[offset.get() + i]) {
                            fastFail.set(true);
                            semaphore.release();
                            Log.e(TAG, "msg[i] != bytes[offset.get() + i]");
                        }
                    }
                    // check suc
                    offset.set(offset.get() + msg.length);
                    if (offset.get() == bytes.length) {
                        semaphore.release();
                    }
                }
            });

            boolean isNotified = false;
            mWriteNotifyLargeRecorder.start();
            mBleGattClientProxy.writeCharacteristicNoResponsePacket(gattWriteCharacteristic, bytes, 20, 5);

            try {
                isNotified = semaphore.tryAcquire(2000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mWriteNotifyLargeRecorder.stop(isNotified && !fastFail.get());
        }

        mBleGattClientProxy.close();
    }

    private void showTestResult() {
        final String connectResult = mConnectRecorder.finish(mPhoneModel);
        final String discoverServicesResult = mDiscoverServicesRecorder.finish(null);
        final String writeNotifyResult = mWriteNotifyRecorder.finish(null);
        final String writeNotifyLargeResult = mWriteNotifyLargeRecorder.finish(null);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View view = getLayoutInflater().inflate(R.layout.textview_log, null);
                TextView textView = (TextView) view.findViewById(R.id.content);
                textView.setText(connectResult + discoverServicesResult + writeNotifyResult + writeNotifyLargeResult);

                AlertDialog alertDialog = new AlertDialog.Builder(TestActivity.this).setView(view).show();
                alertDialog.setCanceledOnTouchOutside(false);
            }
        });

        setEnabledConfirmBtn(true);
    }

}