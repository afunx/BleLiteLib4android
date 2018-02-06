package com.afunx.ble.blelitelib.test;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afunx.ble.blelitelib.app.R;
import com.afunx.ble.blelitelib.constant.Key;
import com.afunx.ble.blelitelib.mail.MailUtils;
import com.afunx.ble.blelitelib.proxy.BleGattClientProxy;
import com.afunx.ble.blelitelib.proxy.BleProxy;
import com.afunx.ble.blelitelib.utils.BleUuidUtils;
import com.afunx.ble.blelitelib.utils.HexUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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
    private Button mBtnConfirm1;
    private Button mBtnConfirm2;
    private Button mBtnConfirm3;
    private Spinner mSpinnerConnectInterval;
    private Switch mSwitchSendEmail;
    private EditText mEdtEmailAddr;
    private EditText mEdtEmailPasswd;

    private String mPhoneModel;
    private AtomicBoolean mIsStop = new AtomicBoolean(false);

    private static final UUID GATT_WRITE_SERVICE_UUID = UUID.fromString("49535343-FE7D-4AE5-8FA9-9FAFD205E455");

    private static final UUID GATT_NOTIFY_SERVICE_UUID = UUID.fromString("49535343-FE7D-4AE5-8FA9-9FAFD205E455");

    private static final UUID GATT_WRITE_PROPERTY_UUID = UUID.fromString("49535343-8841-43F4-A8D4-ECBE34729BB3");

    private static final UUID GATT_NOTIFY_PROPERTY_UUID = UUID.fromString("49535343-1E4D-4BD9-BA61-23C647249616");

    public static void startActivity(Context context, BluetoothDevice bluetoothDevice) {
        Intent intent = new Intent(context, TestActivity.class);
        intent.putExtra(Key.BLUETOOTH_DEVICE, bluetoothDevice);
        context.startActivity(intent);
    }

    private void updateProgressDialogTitle(final String title) {
        Log.e("afunx", "updateProgressDialogTitle() title: " + title);
        Log.e(TAG, "updateProgressDialogTitle() title: " + title);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.setTitle(title);
                if (!mProgressDialog.isShowing()) {
                    mProgressDialog.show();
                }
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
                mBtnConfirm1.setEnabled(enabled);
                mBtnConfirm2.setEnabled(enabled);
                mBtnConfirm3.setEnabled(enabled);
            }
        });
    }

    private boolean setConnectInterval(int itemPosition) {
        // default connect interval
        if (itemPosition == 0) {
            return true;
        } else {
            --itemPosition;
        }
        BluetoothGattService gattService = mBleGattClientProxy.discoverService(BleUuidUtils.int2uuid(0xff90), 0);
        if (gattService == null) {
            Log.e(TAG, "setConnectInterval() gattService is null");
            return false;
        }
        BluetoothGattCharacteristic gattCharacteristic = mBleGattClientProxy.discoverCharacteristic(gattService, BleUuidUtils.int2uuid(0xff92));
        if (gattCharacteristic == null) {
            Log.e(TAG, "setConnectInterval() gattCharacteristic is null");
            return false;
        }
        boolean isSuc = mBleGattClientProxy.writeCharacteristic(gattCharacteristic, new byte[]{(byte) itemPosition}, 2000);
        Log.e(TAG, "setConnectInterval() itemPosition: " + itemPosition + " isSuc: " + isSuc);
        return isSuc;
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

        mSpinnerConnectInterval = (Spinner) findViewById(R.id.spinner_connect_interval);

        mSwitchSendEmail = (Switch) findViewById(R.id.switch_send_email);
        mEdtEmailAddr = (EditText) findViewById(R.id.edt_email_account);
        mEdtEmailPasswd = (EditText) findViewById(R.id.edt_email_passwd);

        mBtnConfirm1 = (Button) findViewById(R.id.btn_start_test1);
        mBtnConfirm1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsStop.set(false);
                try {
                    final int testCount = Integer.parseInt(mEdtTestCount.getText().toString());
                    mProgressDialog.setTitle("Test starting...");

                    final boolean isEmailSent = mSwitchSendEmail.isChecked();
                    final String emailAddr = mEdtEmailAddr.getText().toString();
                    final String emailPasswd = mEdtEmailPasswd.getText().toString();

                    new Thread() {
                        @Override
                        public void run() {
                            mConnectRecorder = new TestRecorder("Connect");
                            mDiscoverServicesRecorder = new TestRecorder("DiscoverService");
                            mWriteNotifyRecorder = new TestRecorder("WriteNotify");
                            mWriteNotifyLargeRecorder = new TestRecorder("WriteLargeNotify");

                            int itemPosition = mSpinnerConnectInterval.getSelectedItemPosition();

                            if (!mIsStop.get()) {
                                testConnectAndDiscoverServices(testCount);
                            }
                            if (!mIsStop.get()) {
                                testWriteNotify(testCount, itemPosition);
                            }
                            if (!mIsStop.get()) {
                                testWriteNotifyLargeRecorder(testCount, itemPosition);
                            }
                            dismissProgressDialog();
                        }
                    }.start();
                    setEnabledConfirmBtn(false);
                    mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (mIsStop.get()) {
                                return;
                            }
                            mIsStop.set(true);
                            updateProgressDialogTitle("Please wait test stop...");
                            new Thread() {
                                @Override
                                public void run() {
                                    boolean sentSuc = isEmailSent;
                                    if (sentSuc) {
                                        sentSuc = sendEmail(emailAddr, emailPasswd);
//                                        dismissProgressDialog();
                                    }
                                    showTestResult(sentSuc);
                                }
                            }.start();
                        }
                    });
                } catch (NumberFormatException e) {
                    Toast.makeText(TestActivity.this, "请输入一个正整数", Toast.LENGTH_LONG).show();
                }
            }
        });

        mBtnConfirm2 = (Button) findViewById(R.id.btn_start_test2);
        mBtnConfirm2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.setTitle("Test starting...");
                new Thread() {
                    @Override
                    public void run() {
                        mIsStop.set(false);
                        testReceiver();
                    }
                }.start();
                mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mBleGattClientProxy.unregisterCharacteristicNotification(GATT_NOTIFY_PROPERTY_UUID);
                    }
                });
            }
        });

        mBtnConfirm3 = (Button) findViewById(R.id.btn_start_test3);
        mBtnConfirm3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsStop.set(false);
                try {
                    final int testCount = Integer.parseInt(mEdtTestCount.getText().toString());
                    mProgressDialog.setTitle("Test starting...");

                    final boolean isEmailSent = mSwitchSendEmail.isChecked();
                    final String emailAddr = mEdtEmailAddr.getText().toString();
                    final String emailPasswd = mEdtEmailPasswd.getText().toString();

                    new Thread() {
                        @Override
                        public void run() {
                            mConnectRecorder = new TestRecorder("Connect");
                            mDiscoverServicesRecorder = new TestRecorder("DiscoverService");
                            mWriteNotifyRecorder = new TestRecorder("WriteNotify");
                            mWriteNotifyLargeRecorder = new TestRecorder("WriteLargeNotify");

                            int itemPosition = mSpinnerConnectInterval.getSelectedItemPosition();

                            while (!mIsStop.get()) {
                                if (!mIsStop.get()) {
                                    testConnectAndDiscoverServices(testCount);
                                }
                                if (!mIsStop.get()) {
                                    testWriteNotify(testCount, itemPosition);
                                }
                                if (!mIsStop.get()) {
                                    testWriteNotifyLargeRecorder(testCount, itemPosition);
                                }
                            }
                            dismissProgressDialog();
                        }
                    }.start();
                    setEnabledConfirmBtn(false);

                    mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (mIsStop.get()) {
                                return;
                            }
                            mIsStop.set(true);
                            updateProgressDialogTitle("Please wait test stop...");
                            new Thread() {
                                @Override
                                public void run() {
                                    boolean sentSuc = isEmailSent;
                                    if (sentSuc) {
                                        sentSuc = sendEmail(emailAddr, emailPasswd);
                                    }
//                                    dismissProgressDialog();
                                    showTestResult(sentSuc);
                                }
                            }.start();
                        }
                    });
                } catch (NumberFormatException e) {
                    Toast.makeText(TestActivity.this, "请输入一个正整数", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void testReceiver() {
        boolean isSuc = false;
        for (int retry = 0; retry < 3 && !isSuc && !mIsStop.get(); retry++) {
            updateProgressDialogTitle("Connecting");
            isSuc = mBleGattClientProxy.connect(mBluetoothDevice.getAddress(), 30 * 1000);
            if (isSuc) {
                updateProgressDialogTitle("Discovering");
                List<BluetoothGattService> gattServiceList = mBleGattClientProxy.discoverServices(30 * 1000);
                isSuc = gattServiceList != null;
            }
        }

        if (!isSuc && !mIsStop.get()) {
            updateProgressDialogTitle("connect fail or discover services fail");
            mBleGattClientProxy.close();
            return;
        }

        // write notify test
        BluetoothGattService gattWriteService = mBleGattClientProxy.discoverService(GATT_WRITE_SERVICE_UUID, 0);
        if (gattWriteService == null) {
            updateProgressDialogTitle("connect fail or discover services fail 111");
            mBleGattClientProxy.close();
            return;
        }
        BluetoothGattCharacteristic gattWriteCharacteristic = mBleGattClientProxy.discoverCharacteristic(gattWriteService, GATT_WRITE_PROPERTY_UUID);
        if (gattWriteCharacteristic == null) {
            updateProgressDialogTitle("connect fail or discover services fail 222");
            mBleGattClientProxy.close();
            return;
        }
        BluetoothGattService gattNotifyService = mBleGattClientProxy.discoverService(GATT_NOTIFY_SERVICE_UUID, 0);
        if (gattNotifyService == null) {
            updateProgressDialogTitle("connect fail or discover services fail 333");
            mBleGattClientProxy.close();
            return;
        }
        BluetoothGattCharacteristic gattNotifyCharacteristic = mBleGattClientProxy.discoverCharacteristic(gattNotifyService, GATT_NOTIFY_PROPERTY_UUID);
        if (gattNotifyCharacteristic == null) {
            updateProgressDialogTitle("connect fail or discover services fail 444");
            mBleGattClientProxy.close();
            return;
        }

        final AtomicLong count = new AtomicLong(0);

        mBleGattClientProxy.registerCharacteristicNotification(gattNotifyCharacteristic, new BleGattClientProxy.OnCharacteristicNotificationListener() {
            @Override
            public void onCharacteristicNotification(byte[] msg) {
                long bytesCount = count.addAndGet(msg.length);
                updateProgressDialogTitle("received: " + bytesCount);
            }
        });

        updateProgressDialogTitle("Waiting");
    }

    private void testConnectAndDiscoverServices(int testCount) {
        for (int count = 0; count < testCount && !mIsStop.get(); count++) {
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

    private void testWriteNotify(int testCount, int itemPosition) {
        boolean isSuc = false;
        for (int retry = 0; retry < 3 && !isSuc && !mIsStop.get(); retry++) {
            updateProgressDialogTitle("Test Write and Notify(2/3) connect device and discover services " + (retry + 1) + " time");
            isSuc = mBleGattClientProxy.connect(mBluetoothDevice.getAddress(), 30 * 1000);
            if (isSuc) {
                List<BluetoothGattService> gattServiceList = mBleGattClientProxy.discoverServices(30 * 1000);
                isSuc = gattServiceList != null;
            }
        }

        if (!isSuc) {
            for (int count = 0; count < testCount && !mIsStop.get(); count++) {
                mWriteNotifyRecorder.start();
                mWriteNotifyRecorder.stop(false);
            }
            mBleGattClientProxy.close();
            return;
        }

//        isSuc = false;
//        for (int retry = 0; retry < 3 && !isSuc; retry++) {
//            isSuc = setConnectInterval(itemPosition);
//        }
//
//        if (!isSuc) {
//            for (int count = 0; count < testCount; count++) {
//                mWriteNotifyRecorder.start();
//                mWriteNotifyRecorder.stop(false);
//            }
//            mBleGattClientProxy.close();
//            return;
//        }

        for (int count = 0; count < testCount && !mIsStop.get(); count++) {
            updateProgressDialogTitle("Test Write and Notify(2/3) "
                    + (count + 1) + " time" + " (total: " + testCount + " time)");

            // write notify test
            BluetoothGattService gattWriteService = mBleGattClientProxy.discoverService(GATT_WRITE_SERVICE_UUID, 0);
            if (gattWriteService == null) {
                Log.e(TAG, "gattWriteService is null");
                mWriteNotifyRecorder.start();
                mWriteNotifyRecorder.stop(false);
                break;
            }
            BluetoothGattCharacteristic gattWriteCharacteristic = mBleGattClientProxy.discoverCharacteristic(gattWriteService, GATT_WRITE_PROPERTY_UUID);
            if (gattWriteCharacteristic == null) {
                mWriteNotifyRecorder.start();
                mWriteNotifyRecorder.stop(false);
                break;
            }
            BluetoothGattService gattNotifyService = mBleGattClientProxy.discoverService(GATT_NOTIFY_SERVICE_UUID, 0);
            if (gattNotifyService == null) {
                mWriteNotifyRecorder.start();
                mWriteNotifyRecorder.stop(false);
                break;
            }
            BluetoothGattCharacteristic gattNotifyCharacteristic = mBleGattClientProxy.discoverCharacteristic(gattNotifyService, GATT_NOTIFY_PROPERTY_UUID);
            if (gattNotifyCharacteristic == null) {
                mWriteNotifyRecorder.start();
                mWriteNotifyRecorder.stop(false);
                break;
            }

            final byte[] bytes = new byte[1];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) i;
            }
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
                        return;
                    }
                    // check content
                    for (int i = 0; i < msg.length; i++) {
                        if (msg[i] != bytes[offset.get() + i]) {
                            Log.e(TAG, "i: " + i + ", k: " + (offset.get() + i));
                            Log.e(TAG, "msg: " + msg[i] + ", byte: " + bytes[offset.get() + i]);
                            fastFail.set(true);
                            semaphore.release();
                            return;
                        }
                    }
                    // check suc
                    offset.set(offset.get() + msg.length);
                    if (offset.get() == bytes.length) {
                        semaphore.release();
                    }
                    Log.w("afunx", "offset.get() " + offset.get());
                    Log.w(TAG, "offset.get() " + offset.get());
                }
            });

            boolean isNotified = false;
            mWriteNotifyRecorder.start();
            mBleGattClientProxy.writeCharacteristicNoResponse(gattWriteCharacteristic, bytes, 0);
            try {
                Log.e("afunx", "notify start");
                Log.e(TAG, "notify start");
                isNotified = semaphore.tryAcquire(2000, TimeUnit.MILLISECONDS);
                Log.e("afunx", "notify end " + isNotified);
                Log.e(TAG, "notify end " + isNotified);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mWriteNotifyRecorder.stop(isNotified && !fastFail.get());
        }

        mBleGattClientProxy.close();
    }

    private void testWriteNotifyLargeRecorder(int testCount, int itemPosition) {
        boolean isSuc = false;
        for (int retry = 0; retry < 3 && !isSuc && !mIsStop.get(); retry++) {
            updateProgressDialogTitle("Test Write and Notify Large(3/3) connect device and discover services " + (retry + 1) + " time");
            isSuc = mBleGattClientProxy.connect(mBluetoothDevice.getAddress(), 30 * 1000);
            if (isSuc) {
                List<BluetoothGattService> gattServiceList = mBleGattClientProxy.discoverServices(30 * 1000);
                isSuc = gattServiceList != null;
            }
        }

        if (!isSuc) {
            for (int count = 0; count < testCount && !mIsStop.get(); count++) {
                mWriteNotifyLargeRecorder.start();
                mWriteNotifyLargeRecorder.stop(false);
            }
            mBleGattClientProxy.close();
            return;
        }

//        isSuc = false;
//        for (int retry = 0; retry < 3 && !isSuc && !mIsStop.get(); retry++) {
//            isSuc = setConnectInterval(itemPosition);
//        }
//
//        if (!isSuc) {
//            for (int count = 0; count < testCount && !mIsStop.get(); count++) {
//                mWriteNotifyRecorder.start();
//                mWriteNotifyRecorder.stop(false);
//            }
//            mBleGattClientProxy.close();
//            return;
//        }

        for (int count = 0; count < testCount && !mIsStop.get(); count++) {
            updateProgressDialogTitle("Test Write and Notify Large(3/3) "
                    + (count + 1) + " time" + " (total: " + testCount + " time)");

            // write notify test
            BluetoothGattService gattWriteService = mBleGattClientProxy.discoverService(GATT_WRITE_SERVICE_UUID, 0);
            if (gattWriteService == null) {
                Log.e(TAG, "gattWriteService is null");
                mWriteNotifyLargeRecorder.start();
                mWriteNotifyLargeRecorder.stop(false);
                break;
            }
            BluetoothGattCharacteristic gattWriteCharacteristic = mBleGattClientProxy.discoverCharacteristic(gattWriteService, GATT_WRITE_PROPERTY_UUID);
            if (gattWriteCharacteristic == null) {
                mWriteNotifyLargeRecorder.start();
                mWriteNotifyLargeRecorder.stop(false);
                break;
            }
            BluetoothGattService gattNotifyService = mBleGattClientProxy.discoverService(GATT_NOTIFY_SERVICE_UUID, 0);
            if (gattNotifyService == null) {
                mWriteNotifyLargeRecorder.start();
                mWriteNotifyLargeRecorder.stop(false);
                break;
            }
            BluetoothGattCharacteristic gattNotifyCharacteristic = mBleGattClientProxy.discoverCharacteristic(gattNotifyService, GATT_NOTIFY_PROPERTY_UUID);
            if (gattNotifyCharacteristic == null) {
                mWriteNotifyLargeRecorder.start();
                mWriteNotifyLargeRecorder.stop(false);
                break;
            }

            final byte[] bytes = new byte[200];
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
                    Log.e(TAG, "onCharacteristicNotification() msg: " + HexUtils.bytes2HexString(msg));
                    // check length
                    if (offset.get() + msg.length > bytes.length) {
                        fastFail.set(true);
                        semaphore.release();
                        Log.e(TAG, "offset.get() + msg.length > bytes.length");
                        return;
                    }
                    // check content
                    for (int i = 0; i < msg.length; i++) {
                        if (msg[i] != bytes[offset.get() + i]) {
                            Log.e(TAG, "i: " + i + ", k: " + (offset.get() + i));
                            Log.e(TAG, "msg: " + msg[i] + ", byte: " + bytes[offset.get() + i]);
                            fastFail.set(true);
                            semaphore.release();
                            return;
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
            mBleGattClientProxy.writeCharacterisitcNoResponse2(gattWriteCharacteristic, bytes);
            try {
                Log.e("afunx", "notify start");
                Log.e(TAG, "notify start");
                isNotified = semaphore.tryAcquire(4000, TimeUnit.MILLISECONDS);
                Log.e("afunx", "notify end " + isNotified);
                Log.e(TAG, "notify end " + isNotified);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mWriteNotifyLargeRecorder.stop(isNotified && !fastFail.get());
        }

        mBleGattClientProxy.close();
    }

    private boolean sendEmail(String emailAddr, String emailPasswd) {
        boolean isSentSuc = false;
        for (int retry = 0; !isSentSuc && retry < 3; retry++) {
            if (retry > 0) {
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            updateProgressDialogTitle("send email " + (retry + 1) + " time");
            String version = getString(R.string.app_version);
            long time = System.currentTimeMillis();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(time);
            String timestamp = format.format(date);
            final String connectResult = mConnectRecorder.finish(timestamp + "\n" + version + "\n" + mPhoneModel);
            final String discoverServicesResult = mDiscoverServicesRecorder.finish(null);
            final String writeNotifyResult = mWriteNotifyRecorder.finish(null);
            final String writeNotifyLargeResult = mWriteNotifyLargeRecorder.finish(null);
            final String emailMessage = connectResult + discoverServicesResult + writeNotifyResult + writeNotifyLargeResult;
            isSentSuc = MailUtils.sendEmail(emailAddr, emailPasswd, emailAddr, mPhoneModel + timestamp, emailMessage);
        }
        return isSentSuc;
    }

    private void showTestResult(boolean isEmailSent) {
        String version = getString(R.string.app_version);
        long time = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(time);
        String timestamp = format.format(date);

        final String emailSentResult = (isEmailSent ? "send email suc" : "send eamil fail or not sent") + "\n";

        final String connectResult = mConnectRecorder.finish(timestamp + "\n" + version + "\n" + mPhoneModel);
        final String discoverServicesResult = mDiscoverServicesRecorder.finish(null);
        final String writeNotifyResult = mWriteNotifyRecorder.finish(null);
        final String writeNotifyLargeResult = mWriteNotifyLargeRecorder.finish(null);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View view = getLayoutInflater().inflate(R.layout.textview_log, null);
                TextView textView = (TextView) view.findViewById(R.id.content);
                textView.setText(emailSentResult + connectResult + discoverServicesResult + writeNotifyResult + writeNotifyLargeResult);

                AlertDialog alertDialog = new AlertDialog.Builder(TestActivity.this).setView(view).show();
                alertDialog.setCanceledOnTouchOutside(false);
            }
        });

        setEnabledConfirmBtn(true);
    }

}