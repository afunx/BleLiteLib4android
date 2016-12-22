package com.afunx.ble.blelitelib.log;

/**
 * Created by afunx on 22/12/2016.
 */

public class BleLogImpl implements IBleLog {
    @Override
    public int v(String tag, String msg) {
        return android.util.Log.v(tag, msg);
    }

    @Override
    public int d(String tag, String msg) {
        return android.util.Log.d(tag, msg);
    }

    @Override
    public int i(String tag, String msg) {
        return android.util.Log.i(tag, msg);
    }

    @Override
    public int w(String tag, String msg) {
        return android.util.Log.w(tag, msg);
    }

    @Override
    public int e(String tag, String msg) {
        return android.util.Log.e(tag, msg);
    }
}
