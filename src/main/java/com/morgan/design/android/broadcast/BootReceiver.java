package com.morgan.design.android.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.morgan.design.Logger;
import com.morgan.design.android.service.ReloadingAlarmService;
import com.morgan.design.android.util.PreferenceUtils;

public class BootReceiver extends BroadcastReceiver {

	private static final String LOG_TAG = "BootReceiver";

	@Override
	public void onReceive(final Context context, final Intent intent) {
		// Listen to android.intent.action.BOOT_COMPLETED broadcast and start the application if user allows

		Logger.d(LOG_TAG, "BootReceiver invoked, triggering ReloadingAlarmService");

		if (PreferenceUtils.shouldStartOnBoot(context)) {
			Logger.d(LOG_TAG, "User preference enabled to allow start of boot");
			context.startService(new Intent(context, ReloadingAlarmService.class));
		}
	}

}
