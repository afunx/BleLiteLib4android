package com.afunx.ble.blelitelib.proxy.scheme;

/**
 * Created by afunx on 15/12/2016.
 */

/**
 * Ble Gatt Reconnect Scheme about sleep timestamp
 */
public interface BleGattReconnectScheme {

    /**
     * clear retry count
     */
    void clearRetryCount();

    /**
     * add retry count and get current one
     *
     * @return current retry count
     */
    int addAndGetRetryCount();

    /**
     * get sleep timestamp in milliseconds by retry count
     *
     * @param retryCount retry count
     * @return sleep timestamp in milliseconds by retry count
     */
    long getSleepTimestamp(int retryCount);

    /**
     * check whether try reconnect again
     *
     * @return whether try reconnect again
     */
    boolean tryAgain();

    /**
     * set callback for disconnected
     *
     * @param disconnectedCallback disconnected callback
     * @param timeoutMilli         timeout in milliseconds
     */
    void setDisconnectCallback(Runnable disconnectedCallback, long timeoutMilli);

    /**
     * call disconnectedCallback instantly
     */
    void callDisconnectCallbackInstantly();
}
