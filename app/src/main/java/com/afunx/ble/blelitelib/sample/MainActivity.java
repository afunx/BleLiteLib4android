package com.afunx.ble.blelitelib.sample;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.afunx.ble.blelitelib.R;
import com.afunx.ble.blelitelib.proxy.BleGattClientProxy;
import com.afunx.ble.blelitelib.proxy.BleGattClientProxyImpl;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private volatile BleGattClientProxy mProxy;
    private volatile boolean mIsStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIsStop = false;
        mProxy = new BleGattClientProxyImpl(this);
        Button tapBtn = (Button) findViewById(R.id.btn_tap_me);
        tapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tapMe();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mIsStop = true;
        super.onDestroy();
    }

    private void tapMe() {
        Log.i(TAG,"tapMe()");
        final String bleAddr = "24:0A:C4:00:02:BC";
        final Context context = MainActivity.this;
        final BleGattClientProxy proxy = mProxy;
        new Thread(){
            @Override
            public void run() {
                int count = 0;
                while(!mIsStop) {
                    Log.e(TAG,"connect and close count: " + (++count));
                    proxy.connect(bleAddr, 20000);
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
