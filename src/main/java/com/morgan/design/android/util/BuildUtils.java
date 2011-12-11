package com.morgan.design.android.util;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.os.Build;

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
}
