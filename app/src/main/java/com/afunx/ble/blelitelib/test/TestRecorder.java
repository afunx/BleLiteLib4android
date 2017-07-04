package com.afunx.ble.blelitelib.test;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by afunx on 04/07/2017.
 */

public class TestRecorder {

    private static final String TAG = "TestRecorder";

    private void checkUIThread() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException("TestRecorder couldn't be called after UI thread");
        }
    }

    private void log(String msg) {
        Log.e(TAG, mTestTarget + " " + msg);
    }

    private static long calMaxValue(List<Long> values) {
        if (values.size() == 0) {
            throw new IllegalStateException();
        }
        long max = values.get(0);
        for (int i = 1; i < values.size(); i++) {
            if (max < values.get(i)) {
                max = values.get(i);
            }
        }
        return max;
    }

    private static long calMinValue(List<Long> values) {
        if (values.size() == 0) {
            throw new IllegalStateException();
        }
        long min = values.get(0);
        for (int i = 1; i < values.size(); i++) {
            if (min > values.get(i)) {
                min = values.get(i);
            }
        }
        return min;
    }

    private static long calAvgValue(List<Long> values) {
        if (values.size() == 0) {
            throw new IllegalStateException();
        }
        long total = 0;
        int size = values.size();
        for (int i = 0; i < size; i++) {
            total += values.get(i);
        }
        return total / size;
    }

    private static long calMedValue(List<Long> values) {
        if (values.size() == 0) {
            throw new IllegalStateException();
        }
        Collections.sort(values);
        int size = values.size();
        if (size % 2 == 0) {
            long medMin = values.get(values.size() / 2 - 1);
            long medMax = values.get(values.size() / 2);
            return (medMin + medMax) / 2;
        } else {
            return values.get(size / 2);
        }
    }


    private final String mTestTarget;

    public TestRecorder(@NonNull String testTarget) {
        mTestTarget = testTarget;
    }

    private int _startCount;
    private int _stopCount;
    private long mTestStartTimestamp;
    private List<Long> mTestConsumeList = new ArrayList<>();

    public void reset() {
        checkUIThread();
        mTestConsumeList.clear();
        _startCount = 0;
        _stopCount = 0;
    }

    public void start() {
        log("start");
        mTestStartTimestamp = System.currentTimeMillis();
        ++_startCount;
    }

    public void stop() {
        log("stop");
        long testConsume = System.currentTimeMillis() - mTestStartTimestamp;
        mTestConsumeList.add(testConsume);
        ++_stopCount;
    }

    public void finish() {

        if (_startCount != _stopCount) {
            log(("startCount: " + _startCount + ", stopCount: " + _stopCount + ", they should be equal"));
            return;
        }

        if (mTestConsumeList.size() == 0) {
            log("test hasn't been started yet");
            return;
        }

        // min
        log("min: " + calMinValue(mTestConsumeList));
        // max
        log("max: " + calMaxValue(mTestConsumeList));
        // avg
        log("avg: " + calAvgValue(mTestConsumeList));
        // med
        log("med: " + calMedValue(mTestConsumeList));
    }

    public static void main(String args[]) {
        List<Long> longList = new ArrayList<>();
        long[] values = new long[]{4, 5, 6, 7};
        for (long value : values) {
            longList.add(value);
        }
        System.out.println("Max: " + calMaxValue(longList));
        System.out.println("Min: " + calMinValue(longList));
        System.out.println("Avg: " + calAvgValue(longList));
        System.out.println("Med: " + calMedValue(longList));
    }
}