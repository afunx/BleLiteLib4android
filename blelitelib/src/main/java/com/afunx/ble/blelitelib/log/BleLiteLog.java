package com.afunx.ble.blelitelib.log;

public class BleLiteLog {

	private static final boolean sDebug = true;

	private static final String TAG = "BleLiteLog";

	/**
	 * BleLiteLogTag could make log filter easy 
	 */
	private static final boolean sBleLiteLogTag = true;

	private BleLiteLog() {
	}

	/**************************** Log ****************************/
	public static int v(String tag, String msg) {
		if (sBleLiteLogTag && sDebug) {
			msg = tag + ": " + msg;
			tag = TAG;
		}
		return sDebug ? android.util.Log.v(tag, msg) : -1;
	}

	public static int d(String tag, String msg) {
		if (sBleLiteLogTag && sDebug) {
			msg = tag + ": " + msg;
			tag = TAG;
		}
		return sDebug ? android.util.Log.d(tag, msg) : -1;
	}

	public static int i(String tag, String msg) {
		if (sBleLiteLogTag && sDebug) {
			msg = tag + ": " + msg;
			tag = TAG;
		}
		return sDebug ? android.util.Log.i(tag, msg) : -1;
	}

	public static int w(String tag, String msg) {
		if (sBleLiteLogTag && sDebug) {
			msg = tag + ": " + msg;
			tag = TAG;
		}
		return sDebug ? android.util.Log.w(tag, msg) : -1;
	}

	public static int e(String tag, String msg) {
		if (sBleLiteLogTag && sDebug) {
			msg = tag + ": " + msg;
			tag = TAG;
		}
		return sDebug ? android.util.Log.e(tag, msg) : -1;
	}

	/**************************** Log with Throwable ****************************/
	public static int v(String tag, String msg, Throwable tr) {
		if (sBleLiteLogTag && sDebug) {
			msg = tag + ": " + msg;
			tag = TAG;
		}
		return sDebug ? android.util.Log.v(tag, msg, tr) : -1;
	}

	public static int d(String tag, String msg, Throwable tr) {
		if (sBleLiteLogTag && sDebug) {
			msg = tag + ": " + msg;
			tag = TAG;
		}
		return sDebug ? android.util.Log.d(tag, msg, tr) : -1;
	}

	public static int i(String tag, String msg, Throwable tr) {
		if (sBleLiteLogTag && sDebug) {
			msg = tag + ": " + msg;
			tag = TAG;
		}
		return sDebug ? android.util.Log.i(tag, msg, tr) : -1;
	}

	public static int w(String tag, String msg, Throwable tr) {
		if (sBleLiteLogTag && sDebug) {
			msg = tag + ": " + msg;
			tag = TAG;
		}
		return sDebug ? android.util.Log.w(tag, msg, tr) : -1;
	}

	public static int e(String tag, String msg, Throwable tr) {
		if (sBleLiteLogTag && sDebug) {
			msg = tag + ": " + msg;
			tag = TAG;
		}
		return sDebug ? android.util.Log.e(tag, msg, tr) : -1;
	}
}
