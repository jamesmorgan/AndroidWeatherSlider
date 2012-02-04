package com.morgan.design.android.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.morgan.design.android.service.ReloadingAlarmService;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.PreferenceUtils;

public class UserPresentReceiver extends BroadcastReceiver {

	private static final String LOG_TAG = UserPresentReceiver.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		// Listen to android.intent.action.USER_PRESENT broadcast and trigger lookup if user allows

		Logger.d(LOG_TAG, "UserPresentReceiver invoked, triggering ReloadingAlarmService");

		if (PreferenceUtils.shouldRefreshOnUserPresent(context)) {
			Logger.d(LOG_TAG, "User preference enabled to allow lookup of weather on user wake/present");
			context.startService(new Intent(context, ReloadingAlarmService.class));
		}
	}

}
