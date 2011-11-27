package com.morgan.design.android.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.morgan.design.android.service.YahooWeatherLoaderService;

public class LaunchReceiver extends BroadcastReceiver {

	public static final String GET_WEATHER_FORCAST = "com.morgan.design.android.broadcast.GET_WEATHER_FORCAST";

	@Override
	public void onReceive(final Context context, final Intent intent) {
		// AppGlobal.logDebug("OnReceive for " + intent.getAction());
		// AppGlobal.logDebug(intent.getExtras().toString());
		// final Intent serviceIntent = new Intent(AppGlobal.getContext(), MonitorService.class);
		// AppGlobal.getContext().startService(serviceIntent);

		Toast.makeText(context, "LaunchReceiver invoked !!!!.", Toast.LENGTH_LONG).show();

		final Intent service = new Intent(context, YahooWeatherLoaderService.class);
		context.startService(service);

		// final Intent intent = new Intent(this, LaunchReceiver.class);
		// final PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 234324243, intent, 0);
		// final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		// alarmManager.set(AlarmManager.RTC_WAKEUP, 10000, pendingIntent);
	}
}
