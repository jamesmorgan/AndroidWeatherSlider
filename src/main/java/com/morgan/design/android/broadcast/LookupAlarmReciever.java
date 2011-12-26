package com.morgan.design.android.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.morgan.design.android.service.ReloadingAlarmService;
import com.morgan.design.android.util.Logger;

public class LookupAlarmReciever extends BroadcastReceiver {

	private static final String LOG_TAG = "LookupAlarmReciever";

	@Override
	public void onReceive(final Context context, final Intent intent) {

		// //////////////////////////////////////////////////////

		// 1) alarm set -> triggers -> com.morgan.design.android.broadcast.LOOPING_ALARM

		// 2) LookupAlarmReciever.class listens to it

		// // starts ReloadingAlarmService.class - DONE
		// // -> looks up active notifications - DONE
		// // // yes -> - DONE
		// // // // // broadcasts -> com.morgan.design.android.broadcast.RELOAD_WEATHER_BROADCAST
		// // // No -> - DONE
		// // // // // kills self

		// 3) Roaming & Static services listens to -> com.morgan.design.android.broadcast.RELOAD_WEATHER_BROADCAST

		// //////////////////////////////////////////////////////

		Logger.d(LOG_TAG, "LookupAlarmReciever invoked, triggering ReloadingAlarmService");
		context.startService(new Intent(context, ReloadingAlarmService.class));
	}

}
