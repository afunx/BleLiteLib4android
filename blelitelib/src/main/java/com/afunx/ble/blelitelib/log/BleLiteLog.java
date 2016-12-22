package com.afunx.ble.blelitelib.log;

public class BleLiteLog {

    private static volatile boolean sEnabled = true;

    private static final String TAG = "BleLiteLog";

    private static volatile IBleLog sBleLogImpl = new BleLogImpl();

    public static void setEnabled(boolean enabled) {
        sEnabled = enabled;
    }

    public static void setBleLogImpl(IBleLog blelog) {
        sBleLogImpl = blelog;
    }

    /**
     * BleLiteLogTag could make log filter easy
     */
    private static final boolean sBleLiteLogTag = true;

    private BleLiteLog() {
    }

    /****************************
     * Log
     ****************************/
    public static int v(String tag, String msg) {
        if (sBleLiteLogTag && sEnabled) {
            msg = tag + ": " + msg;
            tag = TAG;
        }
        return sEnabled && sBleLogImpl != null ? sBleLogImpl.v(tag, msg) : -1;
    }

    public static int d(String tag, String msg) {
        if (sBleLiteLogTag && sEnabled) {
            msg = tag + ": " + msg;
            tag = TAG;
        }
        return sEnabled && sBleLogImpl != null ? sBleLogImpl.d(tag, msg) : -1;
    }

    public static int i(String tag, String msg) {
        if (sBleLiteLogTag && sEnabled) {
            msg = tag + ": " + msg;
            tag = TAG;
        }
        return sEnabled && sBleLogImpl != null ? sBleLogImpl.i(tag, msg) : -1;
    }

    public static int w(String tag, String msg) {
        if (sBleLiteLogTag && sEnabled) {
            msg = tag + ": " + msg;
            tag = TAG;
        }
        return sEnabled && sBleLogImpl != null ? sBleLogImpl.w(tag, msg) : -1;
    }

    public static int e(String tag, String msg) {
        if (sBleLiteLogTag && sEnabled) {
            msg = tag + ": " + msg;
            tag = TAG;
        }
        return sEnabled && sBleLogImpl != null ? sBleLogImpl.e(tag, msg) : -1;
    }

}
