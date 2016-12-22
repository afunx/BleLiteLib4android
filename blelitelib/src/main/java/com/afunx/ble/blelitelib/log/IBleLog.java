package com.afunx.ble.blelitelib.log;

/**
 * Created by afunx on 22/12/2016.
 */

public interface IBleLog {
    int v(String tag, String msg);

    int d(String tag, String msg);

    int i(String tag, String msg);

    int w(String tag, String msg);

    int e(String tag, String msg);
}
