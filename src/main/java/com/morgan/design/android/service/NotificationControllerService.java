package com.morgan.design.android.service;

import static com.morgan.design.Constants.CANCEL_ALL_WEATHER_NOTIFICATIONS;
import static com.morgan.design.Constants.DELETE_CURRENT_NOTIFCATION;
import static com.morgan.design.Constants.NOTIFICATIONS_FULL;
import static com.morgan.design.Constants.PREFERENCES_UPDATED;
import static com.morgan.design.Constants.REMOVE_CURRENT_NOTIFCATION;
import static com.morgan.design.Constants.WEATHER_ID;
import static com.morgan.design.android.util.ObjectUtils.isNotNull;
import static com.morgan.design.android.util.ObjectUtils.isNotZero;
import static com.morgan.design.android.util.ObjectUtils.isNull;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseService;
import com.morgan.design.android.dao.WeatherChoiceDao;
import com.morgan.design.android.domain.orm.WeatherChoice;
import com.morgan.design.android.repository.DatabaseHelper;
import com.morgan.design.android.service.notifcation.WeatherNotificationControllerService;
import com.morgan.design.android.util.Logger;

public class NotificationControllerService extends OrmLiteBaseService<DatabaseHelper> implements ServiceConnection {

	private static final String LOG_TAG = "NotificationControllerService";

	protected WeatherNotificationControllerService mBoundNotificationControllerService;

	protected BroadcastReceiver preferencesChangedBroadcastReceiver;
	protected BroadcastReceiver deleteCurrentNotificationBroadcastReciever;
	protected BroadcastReceiver removeCurrentNotificationBroadcastReciever;
	protected BroadcastReceiver notificationsFullBroadcastReciever;
	protected BroadcastReceiver cancelAllNotificationsBroadcastReciever;

	protected WeatherChoiceDao weatherDao;

	@Override
	public void onServiceConnected(final ComponentName className, final IBinder service) {
		this.mBoundNotificationControllerService = ((WeatherNotificationControllerService.LocalBinder) service).getService();
	}

	@Override
	public void onServiceDisconnected(final ComponentName className) {
		this.mBoundNotificationControllerService = null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		bindService(new Intent(this, WeatherNotificationControllerService.class), this, BIND_AUTO_CREATE);
		this.weatherDao = new WeatherChoiceDao(getHelper());
		doRegisterPreferenceReciever();
		doRegisterRemoveNotificationReciever();
		doRegisterDeleteNotificationReciever();
		doRegisterNotificationsFullBroadcastReciever();
		doRegisterCancelAllNotificationsBroadcastReciever();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		doUnbindPreferenceReciever();
		doUnbindDeleteNotificationReciever();
		doUnbindRemoveNotificationReciever();
		doUnbindNotificationsFullBroadcastReciever();
		doUnbindCancelAllNotificationsBroadcastReciever();
	}

	protected void onRemoveNotification(final Intent intent, final boolean delete) {
		Logger.d(LOG_TAG, "Recieved: %s", REMOVE_CURRENT_NOTIFCATION);

		if (intent.hasExtra(WEATHER_ID)) {
			final int weatherId = intent.getIntExtra(WEATHER_ID, 0);
			if (isNotZero(weatherId)) {
				final WeatherChoice weatherChoice = this.weatherDao.getById(weatherId);
				this.mBoundNotificationControllerService.removeNotification(weatherChoice);
				if (delete) {
					this.weatherDao.delete(weatherChoice);
				}
			}
		}
	}

	protected void onPreferencesChanged() {
		Logger.d(LOG_TAG, "Recieved: %s", PREFERENCES_UPDATED);
		this.mBoundNotificationControllerService.updatePreferences();
	}

	protected void onNotificationsFull(final Context context) {
		Logger.d(LOG_TAG, "Recieved: %s ", NOTIFICATIONS_FULL);
		// TODO Replace with hidden preference
		Toast.makeText(context,
				String.format("Unable to add more weather notifications, please remove one first, maximum of %s notifications allowed", 3),
				Toast.LENGTH_SHORT).show();
	}

	protected void onCancelAllNotifications() {
		final List<WeatherChoice> allWoeidChoices = this.weatherDao.findAllWoeidChoices();
		for (final WeatherChoice weatherChoice : allWoeidChoices) {
			weatherChoice.setActive(false);
			this.weatherDao.update(weatherChoice);
			this.mBoundNotificationControllerService.removeNotification(weatherChoice);
		}
	}

	private void doRegisterPreferenceReciever() {
		if (isNull(this.preferencesChangedBroadcastReceiver)) {
			this.preferencesChangedBroadcastReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(final Context context, final Intent intent) {
					onPreferencesChanged();
				}
			};
			registerReceiver(this.preferencesChangedBroadcastReceiver, new IntentFilter(PREFERENCES_UPDATED));
		}
	}

	private void doRegisterRemoveNotificationReciever() {
		if (isNull(this.removeCurrentNotificationBroadcastReciever)) {
			this.removeCurrentNotificationBroadcastReciever = new BroadcastReceiver() {
				@Override
				public void onReceive(final Context context, final Intent intent) {
					onRemoveNotification(intent, false);
				}
			};
			registerReceiver(this.removeCurrentNotificationBroadcastReciever, new IntentFilter(REMOVE_CURRENT_NOTIFCATION));
		}
	}

	private void doRegisterDeleteNotificationReciever() {
		if (isNull(this.deleteCurrentNotificationBroadcastReciever)) {
			this.deleteCurrentNotificationBroadcastReciever = new BroadcastReceiver() {
				@Override
				public void onReceive(final Context context, final Intent intent) {
					onRemoveNotification(intent, true);
				}
			};
			registerReceiver(this.deleteCurrentNotificationBroadcastReciever, new IntentFilter(DELETE_CURRENT_NOTIFCATION));
		}
	}

	private void doRegisterNotificationsFullBroadcastReciever() {
		if (isNull(this.notificationsFullBroadcastReciever)) {
			this.notificationsFullBroadcastReciever = new BroadcastReceiver() {
				@Override
				public void onReceive(final Context context, final Intent intent) {
					onNotificationsFull(context);
				}
			};
			registerReceiver(this.notificationsFullBroadcastReciever, new IntentFilter(NOTIFICATIONS_FULL));
		}
	}

	private void doRegisterCancelAllNotificationsBroadcastReciever() {
		if (isNull(this.cancelAllNotificationsBroadcastReciever)) {
			this.cancelAllNotificationsBroadcastReciever = new BroadcastReceiver() {
				@Override
				public void onReceive(final Context context, final Intent intent) {
					onCancelAllNotifications();
				}
			};
			registerReceiver(this.cancelAllNotificationsBroadcastReciever, new IntentFilter(CANCEL_ALL_WEATHER_NOTIFICATIONS));
		}
	}

	private void doUnbindPreferenceReciever() {
		if (isNotNull(this.preferencesChangedBroadcastReceiver)) {
			unregisterReceiver(this.preferencesChangedBroadcastReceiver);
			this.preferencesChangedBroadcastReceiver = null;
		}
	}

	private void doUnbindRemoveNotificationReciever() {
		if (isNotNull(this.removeCurrentNotificationBroadcastReciever)) {
			unregisterReceiver(this.removeCurrentNotificationBroadcastReciever);
			this.removeCurrentNotificationBroadcastReciever = null;
		}
	}

	private void doUnbindDeleteNotificationReciever() {
		if (isNotNull(this.deleteCurrentNotificationBroadcastReciever)) {
			unregisterReceiver(this.deleteCurrentNotificationBroadcastReciever);
			this.deleteCurrentNotificationBroadcastReciever = null;
		}
	}

	private void doUnbindNotificationsFullBroadcastReciever() {
		if (isNotNull(this.notificationsFullBroadcastReciever)) {
			unregisterReceiver(this.notificationsFullBroadcastReciever);
			this.notificationsFullBroadcastReciever = null;
		}
	}

	private void doUnbindCancelAllNotificationsBroadcastReciever() {
		if (isNotNull(this.cancelAllNotificationsBroadcastReciever)) {
			unregisterReceiver(this.cancelAllNotificationsBroadcastReciever);
			this.cancelAllNotificationsBroadcastReciever = null;
		}
	}

	@Override
	public IBinder onBind(final Intent intent) {
		return null;
	}

}
