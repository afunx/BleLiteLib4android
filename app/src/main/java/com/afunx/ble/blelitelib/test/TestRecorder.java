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
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IllegalStateException("TestRecorder couldn't be called in UI thread");
        }
    }

    private String log(String msg) {
        Log.e(TAG, mTestTarget + " " + msg);
        return mTestTarget + " " + msg;
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
        reset();
        mTestTarget = testTarget;
    }

    private int _startCount;
    private int _stopCount;
    private int mFailCount;
    private long mTestStartTimestamp;
    private List<Long> mTestConsumeList = new ArrayList<>();

    public void reset() {
        checkUIThread();
        mTestConsumeList.clear();
        _startCount = 0;
        _stopCount = 0;
        mFailCount = 0;
    }

    public void start() {
        log("start");
        mTestStartTimestamp = System.currentTimeMillis();
        ++_startCount;
    }

    public void stop(boolean isSuc) {
        log("stop suc: " + isSuc);
        long testConsume = System.currentTimeMillis() - mTestStartTimestamp;
        ++_stopCount;
        if (isSuc) {
            mTestConsumeList.add(testConsume);
        } else {
            ++mFailCount;
        }
    }

    public String finish(String msg) {

        StringBuilder sb = new StringBuilder();

        if (msg != null) {
            sb.append(msg);
            sb.append("\n");
            sb.append("\n");
        }

        if (_startCount != _stopCount && _startCount - _stopCount != 1) {
            sb.append(log(("startCount: " + _startCount + ", stopCount: " + _stopCount + ", they should be equal")));
            sb.append("\n");
            return sb.toString();
        }

        if (mTestConsumeList.size() == 0) {
            sb.append(log("test hasn't been started yet"));
            sb.append("\n");
            return sb.toString();
        }

        // suc fail time
        sb.append(log("suc time: " + mTestConsumeList.size() + ", fail time: " + mFailCount));
        sb.append("\n");
        // min
        sb.append(log("min: " + calMinValue(mTestConsumeList) + " ms"));
        sb.append("\n");
        // max
        sb.append(log("max: " + calMaxValue(mTestConsumeList) + " ms"));
        sb.append("\n");
        // avg
        sb.append(log("avg: " + calAvgValue(mTestConsumeList) + " ms"));
        sb.append("\n");
        // med
        sb.append(log("med: " + calMedValue(mTestConsumeList) + " ms"));
        sb.append("\n");
        sb.append("\n");

        return sb.toString();
    }

    public static void main(String args[]) {
        List<Long> longList = new ArrayList<>();
        long[] values = new long[]{4, 5, 6, 7};
        for (long value : values) {
            longList.add(value);
        }
        System.out.println("Max: " + calMaxValue(longList) + " ms");
        System.out.println("Min: " + calMinValue(longList) + " ms");
        System.out.println("Avg: " + calAvgValue(longList) + " ms");
        System.out.println("Med: " + calMedValue(longList) + " ms");
    }
}