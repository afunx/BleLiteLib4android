package com.afunx.ble.blelitelib.proxy;

import android.content.Context;

/**
 * Created by afunx on 29/06/2017.
 */

public class BleProxy {

    private BleGattClientProxy mProxy;

    private BleProxy() {
    }

    public static BleProxy getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final BleProxy INSTANCE = new BleProxy();
    }

    public void init(Context context) {
        mProxy = new BleGattClientProxyImpl(context);
    }

    public BleGattClientProxy getProxy() {
        return mProxy;
    }
}
