package com.morgan.design.android.util;

import java.util.concurrent.Callable;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBUtils {

	private static final String LOG_TAG = "DBUtils";

	public static <T> void executeInTransaction(final SQLiteDatabase db, final Runnable runnable) {
		db.beginTransaction();
		try {
			runnable.run();
			db.setTransactionSuccessful();
		}
		catch (final Exception e) {
			logError(e);
		}
		finally {
			db.endTransaction();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T executeInSafety(final Callable<?> callable) {
		try {
			final Object call = callable.call();
			if (null == call) {
				return null;
			}
			return (T) call;
		}
		catch (final SQLException sqlException) {
			logError(sqlException);
		}
		catch (final Exception e) {
			logError(e);
		}
		return null;
	}

	private static void logError(final Exception e) {
		Logger.e(LOG_TAG, "SQLException ", e);
		e.printStackTrace();
	}
}
