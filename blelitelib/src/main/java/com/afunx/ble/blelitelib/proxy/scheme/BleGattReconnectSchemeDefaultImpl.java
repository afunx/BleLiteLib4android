package com.afunx.ble.blelitelib.proxy.scheme;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by afunx on 15/12/2016.
 */

public class BleGattReconnectSchemeDefaultImpl implements BleGattReconnectScheme {

    private final AtomicInteger mCount = new AtomicInteger();

    @Override
    public void clearRetryCount() {
        mCount.set(0);
    }

    @Override
    public int addAndGetRetryCount() {
        return mCount.addAndGet(1);
    }

    @Override
    public long getSleepTimestamp(int retryCount) {
        // 2000ms, 4000ms, 8000ms, ...
        return 1000 * (2 << (retryCount - 1));
    }
}