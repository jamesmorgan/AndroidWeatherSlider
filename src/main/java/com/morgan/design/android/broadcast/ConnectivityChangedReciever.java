package com.morgan.design.android.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.morgan.design.Logger;
import com.morgan.design.android.service.ReloadingAlarmService;
import com.morgan.design.android.util.PreferenceUtils;

public class ConnectivityChangedReciever extends BroadcastReceiver {

	private static final String LOG_TAG = "ConnectivityChangedReciever";

	@Override
	public void onReceive(final Context context, final Intent intent) {
		// Listen to android.net.conn.CONNECTIVITY_CHANGE broadcast and start the application if user allows

		Logger.d(LOG_TAG, "ConnectivityChangedReciever invoked, triggering ReloadingAlarmService");

		if (PreferenceUtils.shouldReloadOnConnectivityChanged(context)) {
			Logger.d(LOG_TAG, "User preference enabled to allow trigger update on connectivity changed");
			context.startService(new Intent(context, ReloadingAlarmService.class));
		}
	}

}
