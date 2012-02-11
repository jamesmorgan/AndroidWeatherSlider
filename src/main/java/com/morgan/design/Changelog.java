package com.morgan.design;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.morgan.design.android.util.BuildUtils;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.PreferenceUtils;
import com.morgan.design.android.util.Utils;
import com.morgan.design.weatherslider.R;

public class Changelog {

	private static final String LOG_TAG = "Changelog";

	public static boolean show(final Activity activity, final boolean forceShow) {
		if (forceShow) {
			showChangelogDialog(activity);
		}
		else {
			show(activity);
		}
		return true;
	}

	private static boolean show(final Activity activity) {

		final int prefVersion = PreferenceUtils.getAppVersionPref(activity);
		int currentVersion = 0;

		final boolean overrideChangeLog = BuildUtils.isRunningEmmulator();
		Logger.d(LOG_TAG, "Overriding ChangeLog: " + overrideChangeLog);

		final boolean showChangeLog = PreferenceUtils.getChangelogPref(activity);

		try {
			final PackageInfo pi = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
			currentVersion = pi.versionCode;
		}
		catch (final NameNotFoundException e) {
			Logger.e(LOG_TAG, "Package name not found", e);
			return false;
		}

		// Not added before
		if (prefVersion == 0 & showChangeLog) {
			showChangelogDialog(activity);
		}
		// On upgrade
		else if (overrideChangeLog || (showChangeLog && currentVersion > prefVersion)) {
			showChangelogDialog(activity);
		}
		PreferenceUtils.setAppVersionPref(activity, currentVersion);
		return true;
	}

	protected static void showChangelogDialog(final Activity activity) {
		new AlertDialog.Builder(activity).setIcon(android.R.drawable.ic_dialog_info).setTitle(R.string.changelog_title)
				.setView(Utils.dialogWebView(activity, activity.getString(R.string.changelog_filename))).setPositiveButton(R.string.okay, null)
				.setNegativeButton(R.string.feedback, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						Utils.openFeedback(activity);
					}
				}).show();
	}
}
