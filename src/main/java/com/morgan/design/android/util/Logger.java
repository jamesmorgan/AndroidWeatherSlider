<<<<<<< HEAD
package com.morgan.design.android.util;

import android.util.Log;

/**
 * @author James Edward Morgan
 */
public class Logger {

	private final static String LOGTAG = "";

	public static void w(final String LOG_TAG, final String logMe) {
		Log.w(tag(LOG_TAG), logMe);
	}

	public static void w(final String LOG_TAG, final String logMe, Throwable throwable) {
		Log.w(tag(LOG_TAG), logMe, throwable);
	}

	public static void w(final String LOG_TAG, Throwable throwable) {
		Log.w(tag(LOG_TAG), throwable);
	}

	public static void e(final String LOG_TAG, final String logMe) {
		Log.e(tag(LOG_TAG), logMe);
	}

	public static void e(final String LOG_TAG, final String logMe, final Object... values) {
		Log.e(tag(LOG_TAG), String.format(logMe, values));
	}

	public static void e(final String LOG_TAG, final String logMe, final Throwable ex) {
		Log.e(tag(LOG_TAG), logMe, ex);
	}

	public static void d(final String LOG_TAG, final long logMe) {
		Log.d(tag(LOG_TAG), "" + logMe);
	}

	public static void d(final String LOG_TAG, final Object logMe) {
		Log.d(tag(LOG_TAG), "" + logMe);
	}

	public static void d(final String LOG_TAG, final String logMe, final Object... values) {
		Log.d(tag(LOG_TAG), String.format(logMe, values));
	}

	private static String tag(final String LOG_TAG) {
		return LOGTAG + ": " + LOG_TAG;
	}
}
=======
package com.morgan.design.android.util;

import android.util.Log;

/**
 * @author James Edward Morgan
 */
public class Logger {

	private final static String LOGTAG = "";

	public static void w(final String LOG_TAG, final String logMe) {
		Log.w(tag(LOG_TAG), logMe);
	}

	public static void w(final String LOG_TAG, final String logMe, Throwable throwable) {
		Log.w(tag(LOG_TAG), logMe, throwable);
	}

	public static void w(final String LOG_TAG, Throwable throwable) {
		Log.w(tag(LOG_TAG), throwable);
	}

	public static void e(final String LOG_TAG, final String logMe) {
		Log.e(tag(LOG_TAG), logMe);
	}

	public static void e(final String LOG_TAG, final String logMe, final Object... values) {
		Log.e(tag(LOG_TAG), String.format(logMe, values));
	}

	public static void e(final String LOG_TAG, final String logMe, final Throwable ex) {
		Log.e(tag(LOG_TAG), logMe, ex);
	}

	public static void d(final String LOG_TAG, final long logMe) {
		Log.d(tag(LOG_TAG), "" + logMe);
	}

	public static void d(final String LOG_TAG, final Object logMe) {
		Log.d(tag(LOG_TAG), "" + logMe);
	}

	public static void d(final String LOG_TAG, final String logMe, final Object... values) {
		Log.d(tag(LOG_TAG), String.format(logMe, values));
	}

	private static String tag(final String LOG_TAG) {
		return LOGTAG + ": " + LOG_TAG;
	}
}
>>>>>>> development
