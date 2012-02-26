package com.morgan.design.android.util;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import com.morgan.design.android.repository.DatabaseHelper;

public class BuildUtils {

	private static final String LOG_TAG = "BUILD_UTILS";

	public static boolean isNotRunningEmmulator() {
		return isRunningEmmulator();
	}

	public static String getDeviceId() {
		String AndroidID = System.getProperty(android.provider.Settings.Secure.ANDROID_ID);
		if (AndroidID == null) {
			AndroidID = "a23456790112345b";
		}
		final String Android_ID = Build.ID + "-" + android.os.Build.PRODUCT + "-" + AndroidID;
		Logger.d(LOG_TAG, "#########################################");
		Logger.d(LOG_TAG, "Android_ID = " + Android_ID);
		Logger.d(LOG_TAG, "#########################################");
		return Android_ID;
	}

	public static String getVersion(final Activity activity) {
		String versionCode = "- | -";
		try {
			final PackageInfo pi = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
			versionCode =
					new StringBuilder().append("").append(Integer.toString(pi.versionCode)).append(" | ").append(pi.versionName).toString();
		}
		catch (final Exception e) {
			Logger.e(LOG_TAG, "Error gettting version code", e);
		}
		return versionCode;
	}

	public static boolean isRunningEmmulator() {
		return "sdk".equals(Build.PRODUCT);
	}

	public static String getSQLLiteVersion() {
		try {
			final SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(":memory:", null);
			final Cursor cursor = db.rawQuery("select sqlite_version() AS sqlite_version", null);
			String sqliteVersion = "";
			while (cursor.moveToNext()) {
				sqliteVersion += cursor.getString(0);
			}
			cursor.close();
			db.close();
			return sqliteVersion;
		}
		catch (final Exception e) {
			// suppress error
		}
		return "N/A";
	}

	public static int getDbVersion() {
		return DatabaseHelper.DATABASE_VERSION;
	}
}
