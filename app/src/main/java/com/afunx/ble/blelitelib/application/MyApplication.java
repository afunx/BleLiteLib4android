package com.afunx.ble.blelitelib.application;

import android.app.Application;
import android.util.Log;

/**
 * Created by afunx on 11/07/2017.
 */

public class MyApplication extends Application {

    private static class CrashHandler implements Thread.UncaughtExceptionHandler {

        // Singleton Instance
        private static CrashHandler gInstance = null;

        private CrashHandler() {
        }

        private static class InstanceHolder {
            static CrashHandler instance = new CrashHandler();
        }

        public static CrashHandler getInstance() {
            if (gInstance == null) {
                gInstance = new CrashHandler();
            }
            return InstanceHolder.instance;
        }

        public void init() {
            Thread.setDefaultUncaughtExceptionHandler(this);
        }

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            Log.e("afunx", "uncaughtException");
            e.printStackTrace();
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init();
    }
}
