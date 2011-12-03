package com.morgan.design.android.service;

import static com.morgan.design.android.util.ObjectUtils.isNotNull;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteBaseService;
import com.morgan.design.android.ManageWeatherChoiceActivity;
import com.morgan.design.android.broadcast.LaunchReceiver;
import com.morgan.design.android.dao.WoeidChoiceDao;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.domain.orm.WoeidChoice;
import com.morgan.design.android.repository.DatabaseHelper;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.PreferenceUtils;

public class YahooWeatherLoaderService extends OrmLiteBaseService<DatabaseHelper> {

	public static final String CURRENT_WEATHER_WOEID = "CURRENT_WEATHER_WOEID";

	private static final String LOG_TAG = "YahooWeatherLoaderService";

	private YahooWeatherNotifcationService mBoundNotificationService;

	private AlarmManager alarms;
	private ConnectivityManager cnnxManager;

	private YahooWeatherInfo currentWeather;
	private WoeidChoiceDao woeidChoiceDao;
	private String woeidId;

	private boolean mIsNotificatoionServiceBound = false;

	private final ServiceConnection mNotificationConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(final ComponentName className, final IBinder service) {
			YahooWeatherLoaderService.this.mBoundNotificationService = ((YahooWeatherNotifcationService.LocalBinder) service).getService();
			setUpdateWeatherInfoForService();
			Logger.d(LOG_TAG, "Successfully loaded weather details");
		}

		@Override
		public void onServiceDisconnected(final ComponentName className) {
			YahooWeatherLoaderService.this.mBoundNotificationService = null;
			Logger.d(LOG_TAG, "Disconnected from weather service");
		}
	};

	@Override
	public IBinder onBind(final Intent arg) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.woeidChoiceDao = new WoeidChoiceDao(getHelper());
		doBindService();

		this.cnnxManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		this.alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		doUnbindService();
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		super.onStartCommand(intent, flags, startId);
		Logger.d(LOG_TAG, "onStartCommand found");

		doBindService();

		if (isNotNull(intent)) {
			final Bundle extras = intent.getExtras();
			if (isNotNull(extras)) {

				this.woeidId = intent.getStringExtra(CURRENT_WEATHER_WOEID);
				Log.d("YahooWeatherLoaderService", "onStartCommand : Found woeidId: " + this.woeidId);

				new DownloadWeatherInfoDataTask(this.woeidId).execute();
			}
		}

		return START_STICKY;
	}

	void doUnbindService() {
		if (this.mIsNotificatoionServiceBound) {
			unbindService(this.mNotificationConnection);
			this.mIsNotificatoionServiceBound = false;
		}
	}

	public void loadWeatherInfomation(final YahooWeatherInfo weatherInfo) {
		this.currentWeather = weatherInfo;

		if (null == this.mBoundNotificationService || !this.mIsNotificatoionServiceBound) {
			doBindService();
		}

		if (this.mIsNotificatoionServiceBound) {
			setUpdateWeatherInfoForService();
		}
	}

	protected void setUpdateWeatherInfoForService() {

		final WoeidChoice woeidChoice = this.woeidChoiceDao.findByWoeidOrNewInstance(this.woeidId);

		// FIXME -> when allowing more than one notification, get notification Id, plus ystem millis
		if (null != woeidChoice && null != this.mBoundNotificationService) {
			woeidChoice.setLastknownNotifcationId(this.mBoundNotificationService.getCurrentNotifcationId());
		}

		final boolean failed = null == this.currentWeather;
		try {
			this.mBoundNotificationService.setWeatherInformation(this.currentWeather);
		}
		finally {
			if (null != woeidChoice) {
				if (failed) {
					woeidChoice.failedQuery();
					this.woeidChoiceDao.update(woeidChoice);
				}
				else {
					woeidChoice.successfullyQuery(this.currentWeather);
					this.woeidChoiceDao.update(woeidChoice);
				}
				sendBroadcast(new Intent(ManageWeatherChoiceActivity.LATEST_WEATHER_QUERY_COMPLETE));
			}
		}
	}

	void doBindService() {
		bindService(new Intent(this, YahooWeatherNotifcationService.class), this.mNotificationConnection, Context.BIND_AUTO_CREATE);
		this.mIsNotificatoionServiceBound = true;
	}

	private class DownloadWeatherInfoDataTask extends AsyncTask<Void, Void, YahooWeatherInfo> {

		private final String woeidId;

		public DownloadWeatherInfoDataTask(final String woeidId) {
			this.woeidId = woeidId;
		}

		// http://stackoverflow.com/questions/2775628/android-how-to-periodically-send-location-to-a-server

		@Override
		protected YahooWeatherInfo doInBackground(final Void... params) {

			try {
				// if we have no data connection, no point in proceeding.
				if (isNotConnectedToNetwork()) {
					Logger.e(LOG_TAG, "No usable network. Skipping pulse action.");
					return null;
				}
				else {
					Logger.d(LOG_TAG, "Looking up weather details for woeid=[%s]", this.woeidId);
					final String url = YahooRequestUtils.getInstance().createWeatherQuery(this.woeidId);

					final RestTemplate restTemplate = new RestTemplate();
					restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

					final String weatherResponse = restTemplate.getForObject(url, String.class);
					return YahooRequestUtils.getInstance().getWeatherInfo(weatherResponse);
				}
			}
			catch (final Exception e) {
				Logger.e(LOG_TAG, "Unknonw error when running periodic download weather data task", e);
			}
			finally {
				// TODO Confirm if required to create new one everytime
				final Intent intentOnAlarm = new Intent(LaunchReceiver.GET_WEATHER_FORCAST);
				intentOnAlarm.putExtra(CURRENT_WEATHER_WOEID, this.woeidId);
				final PendingIntent broadcast =
						PendingIntent.getBroadcast(YahooWeatherLoaderService.this, 0, intentOnAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

				final int pollingSchedule = Integer.parseInt(PreferenceUtils.getPollingSchedule(getApplicationContext()));
				Logger.d(LOG_TAG, "Polling scheduled [%s]", pollingSchedule);

				final long timer = SystemClock.elapsedRealtime() + (pollingSchedule * (60 * 1000));
				Logger.d(LOG_TAG, "Adding next repating alarm, timer [%s]", timer);

				YahooWeatherLoaderService.this.alarms.set(AlarmManager.ELAPSED_REALTIME, timer, broadcast);
			}
			return null;
		}

		@Override
		protected void onPostExecute(final YahooWeatherInfo weatherInfo) {
			loadWeatherInfomation(weatherInfo);
		}

		private boolean isNotConnectedToNetwork() {
			final NetworkInfo ni = YahooWeatherLoaderService.this.cnnxManager.getActiveNetworkInfo();
			return ni == null || !ni.isAvailable() || !ni.isConnected();
		}
	}
}
