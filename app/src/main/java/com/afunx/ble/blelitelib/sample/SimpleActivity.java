package com.afunx.ble.blelitelib.sample;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.afunx.ble.blelitelib.proxy.BleGattClientProxy;
import com.afunx.ble.blelitelib.proxy.BleGattClientProxyImpl;
import com.afunx.ble.blelitelib.utils.BleUuidUtils;
import com.afunx.ble.blelitelib.utils.HexUtils;

import java.util.Arrays;
import java.util.UUID;

public class SimpleActivity extends AppCompatActivity {

    private static final String TAG = "SimpleActivity";
    private volatile BleGattClientProxy mProxy;
    private volatile boolean mIsStop;
    private Button tapBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        mIsStop = false;
        mProxy = new BleGattClientProxyImpl(this);
        tapBtn = (Button) findViewById(R.id.btn_tap_me);
        tapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableButton();
                new Thread() {
                    @Override
                    public void run() {
                        tapMe7();
                    }
                }.start();

            }
        });
    }

    private void enableButton() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tapBtn.setEnabled(true);
            }
        });
    }

    private void disableButton() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tapBtn.setEnabled(false);
            }
        });
    }

    @Override
    protected void onDestroy() {
        mIsStop = true;
        mProxy.close();
        super.onDestroy();
    }

    BleGattClientProxy.OnCharacteristicNotificationListener notifyListener = new BleGattClientProxy.OnCharacteristicNotificationListener() {
        @Override
        public void onCharacteristicNotification(byte[] msg) {
            Log.e("afunx", "onCharacteristicNotification() msg: " + HexUtils.bytes2HexString(msg));
        }
    };

    private void tapMe7() {
        Log.e("afunx", "tapMe7()");
        final BleGattClientProxy proxy = mProxy;
        final String bleAddr = "20:91:48:1A:0F:76";
//        final String bleAddr = "68:9E:19:0E:91:C0";

        boolean isConnectSuc = mProxy.connect(bleAddr, 5000);
        if (!isConnectSuc) {
            Log.e(TAG, "connect fail " + bleAddr);
            enableButton();
            return;
        }

        // service 0xff12
        final long bleDiscoverServiceTimeout = 5000;
        final UUID serviceUuid = BleUuidUtils.int2uuid(0xff12);
        final BluetoothGattService gattService = proxy.discoverService(serviceUuid, bleDiscoverServiceTimeout);
        if (gattService == null) {
            Log.w(TAG, "bindWSRobot() discover service 0xff12 fail");
            enableButton();
            return;
        }
        // characteristic 0xff01 for write
        final UUID characteristicWriteUuid = BleUuidUtils.int2uuid(0xff01);
        final BluetoothGattCharacteristic characteristicWrite = proxy.discoverCharacteristic(gattService, characteristicWriteUuid);
        if (characteristicWrite == null) {
            Log.w(TAG, "bindWSRobot() discover characteristic 0xff01 fail");
            enableButton();
            return;
        }
        // characteristic 0xff02 for notify
        final UUID characteristicNotifyUuid = BleUuidUtils.int2uuid(0xff02);
        final BluetoothGattCharacteristic characteristicNotify = proxy.discoverCharacteristic(gattService, characteristicNotifyUuid);
        if (characteristicNotify == null) {
            Log.w(TAG, "bindWSRobot() discover characteristic 0xff02 fail");
            enableButton();
            return;
        }

        boolean isRegisterSuc = proxy.registerCharacteristicNotification(characteristicNotify, notifyListener);
        if (!isRegisterSuc) {
            Log.w(TAG, "bindWSRobot() register characteristic 0xff02 notify fail");
            enableButton();
            return;
        }

//        4D 1A 01 02 01 62 12 00 07 00 2F 31 2E 74 78 74 00 03 00 74 65 00 54 00 00 00 1C 0A
        byte[] request = new byte[]{(byte)0x4D,(byte)0x08,(byte)0x01,(byte)0x02,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0A};



        request = new byte[]{(byte) 0x4D, (byte) 0x1A, (byte) 0x01, (byte) 0x02, (byte) 0x01, (byte) 0x62, (byte) 0x12, (byte) 0x00,
                (byte) 0x07, (byte) 0x00, (byte) 0x2F, (byte) 0x31, (byte) 0x2E, (byte) 0x74, (byte) 0x78, (byte) 0x74, (byte) 0x00,
                (byte) 0x03, (byte) 0x00, (byte) 0x74, (byte) 0x65, (byte) 0x00, (byte) 0x54, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x1C, (byte) 0x0A};
//        boolean isWriteSuc = mProxy.writeCharacteristic(characteristicWrite, request, 5000);

        while(true) {
            boolean isWriteSuc = mProxy.writeCharacteristicNoResponsePacket(characteristicWrite, request, 20, 2);
            Log.e("afunx", "isWriteSuc: " + isWriteSuc);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void tapMe6() {
        Log.e("afunx", "tapMe6()");
        final BleGattClientProxy proxy = mProxy;

//        final String bleAddr = "7C:EC:79:EB:FB:DC";
//        final String bleAddr = "88:4A:EA:72:10:F9";
//        final String bleAddr = "20:91:48:1A:0F:74";
//        final String bleAddr = "68:9E:19:0E:91:C0";

        final String bleAddr = "EA:AE:36:B7:AC:AF";
//        final String bleAddr = "C4:A4:5B:CE:E9:BF";

        boolean isConnectSuc = mProxy.connect(bleAddr, 5000);
        if (!isConnectSuc) {
            Log.e(TAG, "connect fail " + bleAddr);
            enableButton();
            return;
        }

        // service 0xff12
        final long bleDiscoverServiceTimeout = 5000;
        final UUID serviceUuid = BleUuidUtils.int2uuid(0x0001);
        final BluetoothGattService gattService = proxy.discoverService(serviceUuid, bleDiscoverServiceTimeout);
        if (gattService == null) {
            Log.w(TAG, "bindWSRobot() discover service 0x0001 fail");
            enableButton();
            return;
        }
        // characteristic 0xff01 for write
        final UUID characteristicWriteUuid = BleUuidUtils.int2uuid(0x0002);
        final BluetoothGattCharacteristic characteristicWrite = proxy.discoverCharacteristic(gattService, characteristicWriteUuid);
        if (characteristicWrite == null) {
            Log.w(TAG, "bindWSRobot() discover characteristic 0x0002 fail");
            enableButton();
            return;
        }
        // characteristic 0xff02 for notify
//        final UUID characteristicNotifyUuid = BleUuidUtils.int2uuid(0xff02);
//        final BluetoothGattCharacteristic characteristicNotify = proxy.discoverCharacteristic(gattService, characteristicNotifyUuid);
//        if (characteristicNotify == null) {
//            Log.w(TAG, "bindWSRobot() discover characteristic 0xff02 fail");
//            enableButton();
//            return;
//        }

//        boolean isRegisterSuc = proxy.registerCharacteristicNotification(characteristicNotify, notifyListener);
//        if (!isRegisterSuc) {
//            Log.w(TAG, "bindWSRobot() register characteristic 0xff02 notify fail");
//            enableButton();
//            return;
//        }


        byte[] _msg = new byte[]{(byte) 0x4D, (byte) 0x16, (byte) 0x01, (byte) 0x02, (byte) 0x00, (byte) 0x62,
                (byte) 0x0E, (byte) 0x00, (byte) 0x07, (byte) 0x00, (byte) 0x2F, (byte) 0x31, (byte) 0x2E,
                (byte) 0x74, (byte) 0x78, (byte) 0x74, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x74,
                (byte) 0x65, (byte) 0x00, (byte) 0x24, (byte) 0x0A};


        final byte[] REQ_GET_BOARD_VERSION
                = new byte[]{(byte) 0x4D, (byte) 0x08, (byte) 0x01, (byte) 0x02, (byte) 0x01, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0A};

//        _msg = REQ_GET_BOARD_VERSION;


        _msg = new byte[]{(byte) 0x4D, (byte) 0x15, (byte) 0xFB, (byte) 0x04, (byte) 0x23, (byte) 0x00, (byte) 0x0D, (byte) 0x00, (byte) 0x01, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x3E, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0xD8, (byte) 0x0A};
        if (1 == 0) {
            while(true) {
                proxy.writeCharacteristicNoResponse20(characteristicWrite, _msg);
                proxy.writeCharacteristicNoResponse20(characteristicWrite, _msg);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            enableButton();
//            proxy.close();
//            if (1==1){
//                return;
//            }
        }

        // write data
        while (true) {
            byte[] msg = new byte[80];
            for (int k = 0; k < msg.length; k++) {
                msg[k] = (byte) k;
            }
            proxy.writeCharacteristicNoResponse20(characteristicWrite, msg);
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }

    private void tapMe5() {
        final BleGattClientProxy proxy = mProxy;

//        final String bleAddr = "7C:EC:79:EB:FB:DC";
//        final String bleAddr = "88:4A:EA:72:10:F9";
//        final String bleAddr = "20:91:48:1A:0F:74";
//        final String bleAddr = "68:9E:19:0E:91:C0";

        final String bleAddr = "B4:99:4C:48:CE:27";

        boolean isConnectSuc = mProxy.connect(bleAddr, 5000);
        if (!isConnectSuc) {
            Log.e(TAG, "connect fail");
            enableButton();
            return;
        }

        // service 0xff12
        final long bleDiscoverServiceTimeout = 5000;
        final UUID serviceUuid = BleUuidUtils.int2uuid(0xffe5);
        final BluetoothGattService gattService = proxy.discoverService(serviceUuid, bleDiscoverServiceTimeout);
        if (gattService == null) {
            Log.w(TAG, "bindWSRobot() discover service 0xff12 fail");
            enableButton();
            return;
        }
        // characteristic 0xff01 for write
        final UUID characteristicWriteUuid = BleUuidUtils.int2uuid(0xffe9);
        final BluetoothGattCharacteristic characteristicWrite = proxy.discoverCharacteristic(gattService, characteristicWriteUuid);
        if (characteristicWrite == null) {
            Log.w(TAG, "bindWSRobot() discover characteristic 0xff01 fail");
            enableButton();
            return;
        }
        // characteristic 0xff02 for notify
//        final UUID characteristicNotifyUuid = BleUuidUtils.int2uuid(0xff02);
//        final BluetoothGattCharacteristic characteristicNotify = proxy.discoverCharacteristic(gattService, characteristicNotifyUuid);
//        if (characteristicNotify == null) {
//            Log.w(TAG, "bindWSRobot() discover characteristic 0xff02 fail");
//            enableButton();
//            return;
//        }

//        boolean isRegisterSuc = proxy.registerCharacteristicNotification(characteristicNotify, notifyListener);
//        if (!isRegisterSuc) {
//            Log.w(TAG, "bindWSRobot() register characteristic 0xff02 notify fail");
//            enableButton();
//            return;
//        }


        byte[] _msg = new byte[]{(byte) 0x4D, (byte) 0x16, (byte) 0x01, (byte) 0x02, (byte) 0x00, (byte) 0x62,
                (byte) 0x0E, (byte) 0x00, (byte) 0x07, (byte) 0x00, (byte) 0x2F, (byte) 0x31, (byte) 0x2E,
                (byte) 0x74, (byte) 0x78, (byte) 0x74, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x74,
                (byte) 0x65, (byte) 0x00, (byte) 0x24, (byte) 0x0A};


        final byte[] REQ_GET_BOARD_VERSION
                = new byte[]{(byte) 0x4D, (byte) 0x08, (byte) 0x01, (byte) 0x02, (byte) 0x01, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0A};

//        _msg = REQ_GET_BOARD_VERSION;


        _msg = new byte[]{(byte) 0x4D, (byte) 0x15, (byte) 0xFB, (byte) 0x04, (byte) 0x23, (byte) 0x00, (byte) 0x0D, (byte) 0x00, (byte) 0x01, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x3E, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0xD8, (byte) 0x0A};
        if (1 == 0) {
            while(true) {
                proxy.writeCharacteristicNoResponse20(characteristicWrite, _msg);
                proxy.writeCharacteristicNoResponse20(characteristicWrite, _msg);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            enableButton();
//            proxy.close();
//            if (1==1){
//                return;
//            }
        }

        // write data
        while (true) {
            byte[] msg = new byte[80];
            for (int k = 0; k < msg.length; k++) {
                msg[k] = (byte) k;
            }
            proxy.writeCharacteristicNoResponse20(characteristicWrite, msg);
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }

    private void tapMe4() {
        final BleGattClientProxy proxy = mProxy;

        final String bleAddr = "7C:EC:79:EB:FB:DC";
//        final String bleAddr = "88:4A:EA:72:10:F9";
//        final String bleAddr = "20:91:48:1A:0F:74";
//        final String bleAddr = "68:9E:19:0E:91:C0";

//        final String bleAddr = "B4:99:4C:48:CE:27";

        boolean isConnectSuc = mProxy.connect(bleAddr, 5000);
        if (!isConnectSuc) {
            Log.e(TAG, "connect fail");
            enableButton();
            return;
        }

        // service 0xff12
        final long bleDiscoverServiceTimeout = 5000;
        final UUID serviceUuid = BleUuidUtils.int2uuid(0xff12);
        final BluetoothGattService gattService = proxy.discoverService(serviceUuid, bleDiscoverServiceTimeout);
        if (gattService == null) {
            Log.w(TAG, "bindWSRobot() discover service 0xff12 fail");
            enableButton();
            return;
        }
        // characteristic 0xff01 for write
        final UUID characteristicWriteUuid = BleUuidUtils.int2uuid(0xff01);
        final BluetoothGattCharacteristic characteristicWrite = proxy.discoverCharacteristic(gattService, characteristicWriteUuid);
        if (characteristicWrite == null) {
            Log.w(TAG, "bindWSRobot() discover characteristic 0xff01 fail");
            enableButton();
            return;
        }
        // characteristic 0xff02 for notify
        final UUID characteristicNotifyUuid = BleUuidUtils.int2uuid(0xff02);
        final BluetoothGattCharacteristic characteristicNotify = proxy.discoverCharacteristic(gattService, characteristicNotifyUuid);
        if (characteristicNotify == null) {
            Log.w(TAG, "bindWSRobot() discover characteristic 0xff02 fail");
            enableButton();
            return;
        }

        boolean isRegisterSuc = proxy.registerCharacteristicNotification(characteristicNotify, notifyListener);
        if (!isRegisterSuc) {
            Log.w(TAG, "bindWSRobot() register characteristic 0xff02 notify fail");
            enableButton();
            return;
        }


        byte[] _msg = new byte[]{(byte) 0x4D, (byte) 0x16, (byte) 0x01, (byte) 0x02, (byte) 0x00, (byte) 0x62,
                (byte) 0x0E, (byte) 0x00, (byte) 0x07, (byte) 0x00, (byte) 0x2F, (byte) 0x31, (byte) 0x2E,
                (byte) 0x74, (byte) 0x78, (byte) 0x74, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x74,
                (byte) 0x65, (byte) 0x00, (byte) 0x24, (byte) 0x0A};


        final byte[] REQ_GET_BOARD_VERSION
                = new byte[]{(byte) 0x4D, (byte) 0x08, (byte) 0x01, (byte) 0x02, (byte) 0x01, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0A};

//        _msg = REQ_GET_BOARD_VERSION;


        _msg = new byte[]{(byte) 0x4D, (byte) 0x15, (byte) 0xFB, (byte) 0x04, (byte) 0x23, (byte) 0x00, (byte) 0x0D, (byte) 0x00, (byte) 0x01, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x3E, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0xD8, (byte) 0x0A};
        if (1 == 0) {
            while(true) {
                proxy.writeCharacteristicNoResponse20(characteristicWrite, _msg);
                proxy.writeCharacteristicNoResponse20(characteristicWrite, _msg);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            enableButton();
//            proxy.close();
//            if (1==1){
//                return;
//            }
        }

        // write data
        while (true) {
            byte[] msg = new byte[80];
            for (int k = 0; k < msg.length; k++) {
                msg[k] = (byte) k;
            }
            proxy.writeCharacteristicNoResponse20(characteristicWrite, msg);
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }

    private void tapMe3() {
        final BleGattClientProxy proxy = mProxy;
        final boolean[] isRunning = new boolean[]{false};
        new Thread() {
            @Override
            public void run() {
                proxy.close();
//                boolean isConnect = mProxy.connect("20:91:48:1A:0F:74",5000);
                boolean isConnect = mProxy.connect("20:91:48:1A:0F:74",5000);

                Log.e("afunx", "isConnected: " + isConnect);

                // service 0xff12
                final UUID serviceUuid = BleUuidUtils.int2uuid(0xff12);
                final BluetoothGattService gattService = mProxy.discoverService(serviceUuid, 5000);
                if (gattService == null) {
                    Log.w(TAG, "bindWSRobot() discover service 0xff12 fail");
                    mProxy.close();
                    return;
                }
                // characteristic 0xff01 for write
                final UUID characteristicWriteUuid = BleUuidUtils.int2uuid(0xff01);
                final BluetoothGattCharacteristic characteristicWrite = mProxy.discoverCharacteristic(gattService, characteristicWriteUuid);
                if (characteristicWrite == null) {
                    Log.w(TAG, "bindWSRobot() discover characteristic 0xff01 fail");
                    mProxy.close();
                    return;
                }
                Log.e("afunx","characteristicWrite: " + characteristicWrite.getUuid() + ", " +  characteristicWriteUuid);

                // characteristic 0xff02 for notify
                final UUID characteristicNotifyUuid = BleUuidUtils.int2uuid(0xff02);
                final BluetoothGattCharacteristic characteristicNotify = mProxy.discoverCharacteristic(gattService, characteristicNotifyUuid);
                if (characteristicNotify == null) {
                    Log.w(TAG, "bindWSRobot() discover characteristic 0xff02 fail");
                    mProxy.close();
                    return;
                }
                Log.e("afunx","characteristicNotifyUuid: " + characteristicNotifyUuid);
                BleGattClientProxy.OnCharacteristicNotificationListener mNotifyListener  = new BleGattClientProxy.OnCharacteristicNotificationListener() {
                    @Override
                    public void onCharacteristicNotification(byte[] msg) {
                        Log.e("afunx", "onCharacteristicNotification msg: " + Arrays.toString(msg));
                    }
                };
                boolean isRegisterSuc = mProxy.registerCharacteristicNotification(characteristicNotify, mNotifyListener);
                Log.e("afunx", "isRegisterSuc: " + isRegisterSuc);

                byte[] request = new byte[]{0x4D,0x08,0x01,0x02,0x01,0x00,0x00,0x00,0x00,0x0A};
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                boolean isWriteSuc =mProxy.writeCharacteristic(characteristicWrite, request, 5000);
                Log.e("afunx", "isWriteSuc: " + isWriteSuc);


//                mProxy.close();
            }
        }.start();
    }

    private void tapMe2() {
        final BleGattClientProxy proxy = mProxy;
        new Thread(){
            @Override
            public void run() {
                final UUID UUID_WIFI_SERVICE = UUID
                        .fromString("0000ffff-0000-1000-8000-00805f9b34fb");
                BluetoothGattService gattService = proxy.discoverService(UUID_WIFI_SERVICE, 5000);

            }
        }.start();
    }

    private void tapMe() {
        Log.i(TAG, "tapMe()");
        final String bleAddr = "24:0A:C4:00:02:BC";
        final UUID UUID_WIFI_SERVICE = UUID
                .fromString("0000ffff-0000-1000-8000-00805f9b34fb");
        final UUID UUID_CONFIGURE_CHARACTERISTIC = UUID
                .fromString("0000ff01-0000-1000-8000-00805f9b34fb");
        final Context context = SimpleActivity.this;
        final BleGattClientProxy proxy = mProxy;
        new Thread() {
            @Override
            public void run() {
                int count = 0;
                while (!mIsStop) {
                    Log.e(TAG, "connect and close count: " + (++count));
                    proxy.connect(bleAddr, 20000);
                    BluetoothGattService gattService = proxy.discoverService(UUID_WIFI_SERVICE, 5000);
                    proxy.requestMtu(64,2000);
                    if (gattService != null) {
                        BluetoothGattCharacteristic characteristic = proxy.discoverCharacteristic(gattService, UUID_CONFIGURE_CHARACTERISTIC);
                        if(characteristic!=null) {
                            proxy.writeCharacteristic(characteristic, "ssid:wifi-11".getBytes(), 5000);
                            byte[] msgRead = proxy.readCharacteristic(characteristic, 5000);
                            System.out.println("BH msgRead: " + Arrays.toString(msgRead));

                            BleGattClientProxy.OnCharacteristicNotificationListener listener = new BleGattClientProxy.OnCharacteristicNotificationListener() {
                                @Override
                                public void onCharacteristicNotification(byte[] msg) {
                                    System.out.println("BH ********************onCharacteristicNotification() msg: " + Arrays.toString(msg));
                                }
                            };
                            proxy.registerCharacteristicNotification(characteristic, listener);
                            proxy.writeCharacteristic(characteristic,"passwd:sumof1+1=2".getBytes(),5000);
                            proxy.writeCharacteristic(characteristic,"confirm:".getBytes(),5000);
                            proxy.unregisterCharacteristicNotification(characteristic.getUuid());
                            System.out.println("BH ********************Sleep 20 seconds********************");
                            try {
                                Thread.sleep(20*1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mIsStop = true;
                        }
                    }
                    proxy.close();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
