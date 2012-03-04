package com.morgan.design.android.broadcast;

import java.util.HashSet;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.morgan.design.Logger;
import com.morgan.design.android.service.ReloadingAlarmService;

public class ApplicationMovedToSDCardReciever extends BroadcastReceiver {

	private static final String LOG_TAG = ApplicationMovedToSDCardReciever.class.getName();

	private static final Set<String> mKnownPackages = new HashSet<String>();
	static {
		mKnownPackages.add("com.morgan.design.weatherslider");
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Logger.d(LOG_TAG,
				"Found android.intent.action.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE, restarting ReloadingAlarmService");

		String action = intent.getAction();

		// Handle applications being moved back and forth from sdcard.
		if (Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE.equals(action)
				|| Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.equals(action)) {

			String[] pkgList = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);

			for (String packageName : pkgList) {
				final boolean knownPackage = mKnownPackages.contains(packageName);
				if (knownPackage) {
					context.startService(new Intent(context, ReloadingAlarmService.class));
					break;
				}
			}
		}

	}
}
