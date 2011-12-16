package com.morgan.design.android.service;

import static com.morgan.design.android.util.ObjectUtils.isNotBlank;
import static com.morgan.design.android.util.ObjectUtils.isNotNull;
import static com.morgan.design.android.util.ObjectUtils.isNull;

import java.util.Date;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.IBinder;

import com.j256.ormlite.android.apptools.OrmLiteBaseService;
import com.morgan.design.Constants;
import com.morgan.design.WeatherSliderApplication;
import com.morgan.design.android.dao.WoeidChoiceDao;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.domain.orm.WoeidChoice;
import com.morgan.design.android.domain.types.Temperature;
import com.morgan.design.android.repository.DatabaseHelper;
import com.morgan.design.android.tasks.DownloadWeatherInfoDataTaskFromWoeid;
import com.morgan.design.android.tasks.OnAsyncQueryCallback;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.PreferenceUtils;

/**
 * Creates default bindings for {@link YahooWeatherNotifcationService}
 * 
 * @author James Edward Morgan
 */
public abstract class AbstractBoundWeatherNotificationService extends OrmLiteBaseService<DatabaseHelper> implements ServiceConnection {

	private static final String LOG_TAG = "AbsrtactBoundWeatherService";

	protected BroadcastReceiver preferencesChangedBroadcastReceiver;
	protected YahooWeatherNotifcationService mBoundNotificationService;
	protected WoeidChoiceDao woeidChoiceDao;
	protected boolean mIsNotificatoionServiceBound = false;

	private AlarmManager alarms;
	private ConnectivityManager cnnxManager;

	private YahooWeatherInfo currentWeather;
	protected String woeidId;

	@Override
	public void onServiceConnected(final ComponentName className, final IBinder service) {
		Logger.d(LOG_TAG, "Successfully loaded weather details");
		this.mBoundNotificationService = ((YahooWeatherNotifcationService.LocalBinder) service).getService();
	}

	@Override
	public void onServiceDisconnected(final ComponentName className) {
		Logger.d(LOG_TAG, "Disconnected from weather service");
		this.mBoundNotificationService = null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.woeidChoiceDao = new WoeidChoiceDao(getHelper());
		doBindService();
		doRegisterPreferenceReciever();
		doRegisterSystemServices();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		doUnbindService();
		doPreferenceReciever();
	}

	OnAsyncQueryCallback<YahooWeatherInfo> onGetYahooWeatherDataCallback = new OnAsyncQueryCallback<YahooWeatherInfo>() {
		@Override
		public void onPostLookup(final YahooWeatherInfo weatherInfo) {
			loadWeatherInfomation(weatherInfo);
		}
	};

	protected void downloadWeatherData(final Context context, final String woeidId, final Temperature temperature) {
		Logger.d("YahooWeatherLoaderService", "onStartCommand : Found woeidId: " + woeidId);
		if (isNotBlank(woeidId)) {
			getToLevelApplication().setCurrentWoeid(woeidId);
			new DownloadWeatherInfoDataTaskFromWoeid(context, this.cnnxManager, this.alarms, woeidId,
					PreferenceUtils.getTemperatureMode(getApplicationContext()), this.onGetYahooWeatherDataCallback).execute();
		}
	}

	protected void loadWeatherInfomation(final YahooWeatherInfo weatherInfo) {
		this.currentWeather = weatherInfo;

		if (this.mIsNotificatoionServiceBound) {
			setUpdateWeatherInfoForService();
		}
	}

	protected void setUpdateWeatherInfoForService() {

		if (null != this.currentWeather) {
			((WeatherSliderApplication) getApplication()).setCurrentWeather(this.currentWeather);
		}

		this.mBoundNotificationService.setWeatherInformation(this.currentWeather);

		// Find one, if not make new one
		WoeidChoice woeidChoice = this.woeidChoiceDao.findByWoeid(this.woeidId);
		if (isNull(woeidChoice)) {
			woeidChoice = new WoeidChoice();
			woeidChoice.setWoeid(this.woeidId);
			woeidChoice.setCreatedDateTime(new Date());
			this.woeidChoiceDao.create(woeidChoice);
		}

		// FIXME -> when allowing more than one notification, get notification Id, plus system millis
		if (null != this.mBoundNotificationService) {
			woeidChoice.setLastknownNotifcationId(this.mBoundNotificationService.getCurrentNotifcationId());
		}

		final Intent broadcastIntent = new Intent(Constants.LATEST_WEATHER_QUERY_COMPLETE);

		final boolean failed = (null == this.currentWeather);
		if (failed) {
			broadcastIntent.putExtra(Constants.SUCCESSFUL, false);
			woeidChoice.failedQuery();
			this.woeidChoiceDao.update(woeidChoice);
		}
		else {
			broadcastIntent.putExtra(Constants.SUCCESSFUL, true);
			woeidChoice.successfullyQuery(this.currentWeather);
			this.woeidChoiceDao.update(woeidChoice);
		}

		sendBroadcast(broadcastIntent);
	}

	protected void doBindService() {
		bindService(new Intent(this, YahooWeatherNotifcationService.class), this, Context.BIND_AUTO_CREATE);
		this.mIsNotificatoionServiceBound = true;
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
					Logger.d(LOG_TAG, "Recieved: com.morgan.design.intent.PREFERENCES_UPDATED");
					AbstractBoundWeatherNotificationService.this.mBoundNotificationService.updatePreferences();
					onPreferencesChanged(context, intent);
				}
			};
			registerReceiver(this.preferencesChangedBroadcastReceiver, new IntentFilter(Constants.PREFERENCES_UPDATED));
		}
	}

	protected void onPreferencesChanged(final Context context, final Intent intent) {
		Logger.d(LOG_TAG, "Recieved: com.morgan.design.intent.PREFERENCES_UPDATED");
		final String woeidId = getToLevelApplication().getCurrentWoied();
		if (isNotBlank(woeidId)) {
			this.woeidId = woeidId;
			if (isNotBlank(this.woeidId)) {
				downloadWeatherData(this, woeidId, PreferenceUtils.getTemperatureMode(getApplicationContext()));
			}
		}
	}

	protected void doUnbindService() {
		if (this.mIsNotificatoionServiceBound) {
			unbindService(this);
			this.mIsNotificatoionServiceBound = false;
		}
	}

	private void doPreferenceReciever() {
		if (isNotNull(this.preferencesChangedBroadcastReceiver)) {
			unregisterReceiver(this.preferencesChangedBroadcastReceiver);
			this.preferencesChangedBroadcastReceiver = null;
		}
	}

	protected WeatherSliderApplication getToLevelApplication() {
		return ((WeatherSliderApplication) getApplication());
	}

	@Override
	public IBinder onBind(final Intent arg) {
		return null;
	}
}
