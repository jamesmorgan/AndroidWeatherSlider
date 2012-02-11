package com.morgan.design.android.service;

import static com.morgan.design.Constants.LOOPING_ALARM;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.SystemClock;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.morgan.design.Constants;
import com.morgan.design.android.broadcast.IServiceUpdateBroadcaster;
import com.morgan.design.android.broadcast.ServiceUpdateBroadcasterImpl;
import com.morgan.design.android.dao.WeatherChoiceDao;
import com.morgan.design.android.repository.DatabaseHelper;
import com.morgan.design.android.util.ACRAErrorLogger;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.PreferenceUtils;
import com.morgan.design.weatherslider.R;

public class ReloadingAlarmService extends IntentService {

	private static final String LOG_TAG = "ReloadingAlarmService";

	private IServiceUpdateBroadcaster serviceUpdate;

	public ReloadingAlarmService() {
		super("com.morgan.design.android.service.ReloadingAlarmService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.serviceUpdate = new ServiceUpdateBroadcasterImpl(this);
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		Logger.d(LOG_TAG, "Reloading Alarm Service Initiated -> may set new one");

		this.serviceUpdate.onGoing(getString(R.string.service_update_refreshing_weather_notifications));

		final AlarmManager mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		final PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(LOOPING_ALARM), 0);

		boolean hasNotifications = false;

		try {
			// Cancel any existing alarms
			mAlarmManager.cancel(pi);

			final WeatherChoiceDao dao = new WeatherChoiceDao(OpenHelperManager.getHelper(getApplicationContext(),
					DatabaseHelper.class));
			if (dao.hasActiveNotifications()) {
				hasNotifications = true;

				// Initiate reload existing broadcast
				this.serviceUpdate.onGoing(getString(R.string.service_update_attempting_to_refresh_weather));
				sendBroadcast(new Intent(Constants.RELOAD_WEATHER_BROADCAST));
			}
		}
		catch (final Exception exception) {
			this.serviceUpdate.onGoing(getString(R.string.service_update_failed_to_refresh_weather));
			ACRAErrorLogger.handleSilentException(exception);
		}
		finally {
			// Start new alarm to launch this if active notifications present
			if (hasNotifications) {
				Logger.d(LOG_TAG, "Setting next reloading alarm");
				this.serviceUpdate.onGoing(getString(R.string.service_update_scheduling_next_refresh));

				final long schedule = PreferenceUtils.getPollingSchedule(this) * 60 * 1000;
				final long futureAlarm = SystemClock.elapsedRealtime() + schedule;
				mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureAlarm, schedule, pi);
			}
		}

	}

}
