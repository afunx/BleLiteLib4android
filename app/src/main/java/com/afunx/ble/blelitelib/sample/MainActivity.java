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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button tapBtn = (Button) findViewById(R.id.btn_tap_me);
        tapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tapMe();
            }
        });
    }

    private void tapMe() {
        Log.i(TAG,"tapMe()");
        final String bleAddr = "24:0A:C4:00:02:BC";
        final Context context = MainActivity.this;
        new Thread(){
            @Override
            public void run() {
                BleGattClientProxy proxy = new BleGattClientProxyImpl(context);
                int count = 0;
                while(true) {
                    Log.e(TAG,"connect and close count: " + (++count));
                    proxy.connect(bleAddr, 20000);
                    proxy.close();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
