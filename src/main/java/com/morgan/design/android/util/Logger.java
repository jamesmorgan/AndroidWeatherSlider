package com.morgan.design.android.util;

/**
 * @author James Edward Morgan
 */
public class Logger {

	private final static String LOGTAG = "TeaRoundGenerator:";

	public static void v(final String LOG_TAG, final String logMe) {
		android.util.Log.v(LOGTAG, LOG_TAG + ": " + logMe);
	}

	public static void v(final String LOG_TAG, final String logMe, final Object... values) {
		android.util.Log.v(LOGTAG, LOG_TAG + ": " + String.format(logMe, values));
	}

	public static void w(final String LOG_TAG, final String logMe) {
		android.util.Log.w(LOGTAG, LOG_TAG + ": " + logMe);
	}

	public static void e(final String LOG_TAG, final String logMe) {
		android.util.Log.e(LOGTAG, LOG_TAG + ": " + logMe);
	}

	public static void e(final String LOG_TAG, final String logMe, final Object... values) {
		android.util.Log.e(LOGTAG, LOG_TAG + ": " + String.format(logMe, values));
	}

	public static void e(final String LOG_TAG, final String logMe, final Throwable ex) {
		android.util.Log.e(LOGTAG, LOG_TAG + ": " + logMe, ex);
	}

	public static void i(final String LOG_TAG, final String logMe) {
		android.util.Log.i(LOGTAG, LOG_TAG + ": " + logMe);
	}

	public static void i(final String LOG_TAG, final String logMe, final Object... values) {
		android.util.Log.i(LOGTAG, LOG_TAG + ": " + String.format(logMe, values));
	}

	public static void d(final String LOG_TAG, final long logMe) {
		android.util.Log.d(LOGTAG, LOG_TAG + ": " + logMe);
	}

	public static void d(final String LOG_TAG, final String logMe, final Object... values) {
		android.util.Log.d(LOGTAG, LOG_TAG + ": " + String.format(logMe, values));
	}

}
