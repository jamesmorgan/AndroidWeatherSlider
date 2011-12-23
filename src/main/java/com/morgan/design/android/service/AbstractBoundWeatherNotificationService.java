package com.morgan.design.android.service;

import static com.morgan.design.Constants.LAST_KNOWN_SERVICE_ID;
import static com.morgan.design.Constants.LATEST_WEATHER_QUERY_COMPLETE;
import static com.morgan.design.Constants.NOTIFICATIONS_FULL;
import static com.morgan.design.Constants.PREFERENCES_UPDATED;
import static com.morgan.design.Constants.REMOVE_CURRENT_NOTIFCATION;
import static com.morgan.design.Constants.SUCCESSFUL;
import static com.morgan.design.android.util.ObjectUtils.isNotBlank;
import static com.morgan.design.android.util.ObjectUtils.isNotNull;
import static com.morgan.design.android.util.ObjectUtils.isNull;

import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseService;
import com.morgan.design.Constants;
import com.morgan.design.WeatherSliderApplication;
import com.morgan.design.android.dao.WeatherChoiceDao;
import com.morgan.design.android.domain.WeatherLookupEntry;
import com.morgan.design.android.domain.orm.WeatherChoice;
import com.morgan.design.android.domain.types.Temperature;
import com.morgan.design.android.repository.DatabaseHelper;
import com.morgan.design.android.service.notifcation.WeatherNotificationControllerService;
import com.morgan.design.android.tasks.DownloadWeatherInfoDataTaskFromWoeid;
import com.morgan.design.android.tasks.OnAsyncQueryCallback;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.PreferenceUtils;

/**
 * Creates default bindings for {@link YahooWeatherNotifcationService}
 * 
 * @author James Edward Morgan
 */
public abstract class AbstractBoundWeatherNotificationService extends OrmLiteBaseService<DatabaseHelper> implements ServiceConnection,
		ServiceUpdate {

	private static final String LOG_TAG = "AbsrtactBoundWeatherService";

	protected BroadcastReceiver preferencesChangedBroadcastReceiver;
	protected BroadcastReceiver removeCurrentNotificationBroadcastReciever;
	protected BroadcastReceiver notificationsFullBroadcastReciever;

	protected WeatherNotificationControllerService mBoundNotificationControllerService;

	protected WeatherChoiceDao woeidChoiceDao;
	protected boolean mIsNotificatoionControllerBound = false;

	private AlarmManager alarms;
	private ConnectivityManager cnnxManager;

	@Override
	public void onServiceConnected(final ComponentName className, final IBinder service) {
		Logger.d(LOG_TAG, "Successfully loaded weather details");
		this.mBoundNotificationControllerService = ((WeatherNotificationControllerService.LocalBinder) service).getService();
	}

	@Override
	public void onServiceDisconnected(final ComponentName className) {
		Logger.d(LOG_TAG, "Disconnected from weather service");
		this.mBoundNotificationControllerService = null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.woeidChoiceDao = new WeatherChoiceDao(getHelper());
		doBindService();
		doRegisterSystemServices();
		doRegisterPreferenceReciever();
		doRegisterRemoveNotificationReciever();
		doRegisterNotificationsFullBroadcastReciever();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		doUnbindService();
		doUnbindPreferenceReciever();
		doUnbindRemoveNotificationReciever();
		doUnbindNotificationsFullBroadcastReciever();
	}

	@Override
	public void loading(final CharSequence message) {
		sendBroadcast(new Intent(ServiceUpdateRegister.ACTION).putExtra(ServiceUpdateRegister.LOADING, message));
	}

	@Override
	public void onGoing(final CharSequence message) {
		sendBroadcast(new Intent(ServiceUpdateRegister.ACTION).putExtra(ServiceUpdateRegister.ONGOING, message));
	}

	@Override
	public void complete(final CharSequence message) {
		sendBroadcast(new Intent(ServiceUpdateRegister.ACTION).putExtra(ServiceUpdateRegister.COMPLETE, message));
	}

	OnAsyncQueryCallback<WeatherLookupEntry> onGetYahooWeatherDataCallback = new OnAsyncQueryCallback<WeatherLookupEntry>() {
		@Override
		public void onPostLookup(final WeatherLookupEntry weatherInfo) {
			complete("Completed Weather Lookup");
			setNextLookupTrigger();
			loadWeatherInfomation(weatherInfo);
		}

		@Override
		protected void onPreLookup() {
			loading("Initalizing Weather Lookup");
		}

		@Override
		protected void onInitiateExecution() {
			onGoing("Running Weather Lookup");
		};
	};

	protected void downloadWeatherData(final Context context, final String woeidId) {
		Logger.d("YahooWeatherLoaderService", "onStartCommand : Found woeidId: " + woeidId);
		if (isNotBlank(woeidId)) {
			new DownloadWeatherInfoDataTaskFromWoeid(this.cnnxManager, woeidId, getTempMode(), this.onGetYahooWeatherDataCallback).execute();
		}
	}

	protected void setNextLookupTrigger() {
		final Intent intentOnAlarm = new Intent(Constants.RELOAD_WEATHER_BROADCAST);
		final PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, intentOnAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

		final int pollingSchedule = Integer.parseInt(PreferenceUtils.getPollingSchedule(this.getApplicationContext()));
		Logger.d(LOG_TAG, "Polling scheduled [%s]", pollingSchedule);

		final long timer = SystemClock.elapsedRealtime() + (pollingSchedule * (60 * 1000));
		Logger.d(LOG_TAG, "Adding next repating alarm, timer [%s]", timer);

		this.alarms.set(AlarmManager.ELAPSED_REALTIME, timer, broadcast);
	}

	protected void loadWeatherInfomation(final WeatherLookupEntry weatherInfo) {
		if (this.mIsNotificatoionControllerBound) {
			setUpdateWeatherInfoForService(weatherInfo);
		}
	}

	protected void setUpdateWeatherInfoForService(final WeatherLookupEntry weatherInfo) {

		// Find it, if not make new one
		WeatherChoice woeidChoice = this.woeidChoiceDao.findByWoeid(weatherInfo.getWoeid());

		if (isNull(woeidChoice)) {
			woeidChoice = new WeatherChoice();
			woeidChoice.setWoeid(weatherInfo.getWoeid());
			woeidChoice.setCreatedDateTime(new Date());
			this.woeidChoiceDao.create(woeidChoice);
		}

		final int serviceId = this.mBoundNotificationControllerService.addNotificationService(weatherInfo);

		woeidChoice.setActive(0 != serviceId);
		woeidChoice.setLastknownNotifcationId(serviceId);

		final Intent broadcastIntent = new Intent(LATEST_WEATHER_QUERY_COMPLETE);

		final boolean failed = (null == weatherInfo);
		if (failed) {
			broadcastIntent.putExtra(SUCCESSFUL, false);
			woeidChoice.failedQuery();
			this.woeidChoiceDao.update(woeidChoice);
		}
		else {
			broadcastIntent.putExtra(SUCCESSFUL, true);
			woeidChoice.successfullyQuery(weatherInfo.getWeatherInfo());
			this.woeidChoiceDao.update(woeidChoice);
		}

		sendBroadcast(broadcastIntent);
	}

	protected void onRemoveNotification(final Context context, final Intent intent) {
		Logger.d(LOG_TAG, "Recieved: %s", REMOVE_CURRENT_NOTIFCATION);

		if (intent.hasExtra(LAST_KNOWN_SERVICE_ID)) {
			final int serviceId = intent.getIntExtra(LAST_KNOWN_SERVICE_ID, 0);
			this.mBoundNotificationControllerService.removeNotification(serviceId);
		}
	}

	protected void onPreferencesChanged(final Context context, final Intent intent) {
		Logger.d(LOG_TAG, "Recieved: %s", PREFERENCES_UPDATED);

		this.mBoundNotificationControllerService.updatePreferences();
		// TODO reload all weather queries
	}

	protected void onNotificationsFull(final Context context, final Intent intent) {
		Logger.d(LOG_TAG, "Recieved: %s ", NOTIFICATIONS_FULL);
		Toast.makeText(
				context,
				String.format("Unable to add more weather notifications, please remove one first, maximum of %s notifications allowed",
						this.mBoundNotificationControllerService.getMaxAllowedNotifications()), Toast.LENGTH_SHORT).show();
	}

	protected void doBindService() {
		bindService(new Intent(this, WeatherNotificationControllerService.class), this, BIND_AUTO_CREATE);
		this.mIsNotificatoionControllerBound = true;
	}

	private void doRegisterSystemServices() {
		this.cnnxManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		this.alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	}

	private void doRegisterPreferenceReciever() {
		if (isNull(this.preferencesChangedBroadcastReceiver)) {
			this.preferencesChangedBroadcastReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(final Context context, final Intent intent) {
					onPreferencesChanged(context, intent);
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
					onRemoveNotification(context, intent);
				}
			};
			registerReceiver(this.removeCurrentNotificationBroadcastReciever, new IntentFilter(REMOVE_CURRENT_NOTIFCATION));
		}
	}

	private void doRegisterNotificationsFullBroadcastReciever() {
		if (isNull(this.notificationsFullBroadcastReciever)) {
			this.notificationsFullBroadcastReciever = new BroadcastReceiver() {
				@Override
				public void onReceive(final Context context, final Intent intent) {
					onNotificationsFull(context, intent);
				}
			};
			registerReceiver(this.notificationsFullBroadcastReciever, new IntentFilter(NOTIFICATIONS_FULL));
		}
	}

	protected void doUnbindService() {
		if (this.mIsNotificatoionControllerBound) {
			unbindService(this);
			this.mIsNotificatoionControllerBound = false;
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

	private void doUnbindNotificationsFullBroadcastReciever() {
		if (isNotNull(this.notificationsFullBroadcastReciever)) {
			unregisterReceiver(this.notificationsFullBroadcastReciever);
			this.notificationsFullBroadcastReciever = null;
		}
	}

	protected WeatherSliderApplication getToLevelApplication() {
		return ((WeatherSliderApplication) getApplication());
	}

	protected Temperature getTempMode() {
		return PreferenceUtils.getTemperatureMode(getApplicationContext());
	}

	@Override
	public IBinder onBind(final Intent arg) {
		return null;
	}
}
