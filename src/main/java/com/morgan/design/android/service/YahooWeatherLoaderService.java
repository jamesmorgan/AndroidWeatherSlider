package com.morgan.design.android.service;

import static com.morgan.design.android.util.ObjectUtils.isNotNull;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteBaseService;
import com.morgan.design.android.ManageWeatherChoiceActivity;
import com.morgan.design.android.dao.WoeidChoiceDao;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.domain.orm.WoeidChoice;
import com.morgan.design.android.repository.DatabaseHelper;
import com.morgan.design.android.util.Logger;

public class YahooWeatherLoaderService extends OrmLiteBaseService<DatabaseHelper> {

	public static final String CURRENT_WEATHER_WOEID = "CURRENT_WEATHER_WOEID";

	private static final String LOG_TAG = "YahooWeatherLoaderService";

	private YahooWeatherNotifcationService mBoundNotificationService;

	private YahooWeatherInfo currentWeather;
	private boolean mIsNotificatoionServiceBound = false;

	private WoeidChoiceDao woeidChoiceDao;

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

	private String woeidId;

	@Override
	public IBinder onBind(final Intent arg) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.woeidChoiceDao = new WoeidChoiceDao(getHelper());
		doBindService();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		doUnbindService();
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		super.onStartCommand(intent, flags, startId);

		doBindService();

		if (isNotNull(intent)) {
			final Bundle extras = intent.getExtras();
			if (isNotNull(extras)) {

				this.woeidId = (String) intent.getExtras().getSerializable(CURRENT_WEATHER_WOEID);
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
		if (null != this.currentWeather) {

			// Only start/bind if not null
			if (null != this.currentWeather) {

				if (null == this.mBoundNotificationService || !this.mIsNotificatoionServiceBound) {
					doBindService();
				}

				// If bind reset options
				if (this.mIsNotificatoionServiceBound) {
					setUpdateWeatherInfoForService();
				}
			}
		}
	}

	protected void setUpdateWeatherInfoForService() {

		final WoeidChoice woeidChoice = this.woeidChoiceDao.findByWoeidOrNewInstance(this.woeidId);

		// FIXME -> when allowing more than one notification, get notification Id, plus ystem millis
		if (null != this.mBoundNotificationService) {
			woeidChoice.setLastknownNotifcationId(this.mBoundNotificationService.getCurrentNotifcationId());
		}

		final boolean failed = null == this.currentWeather;
		try {
			this.mBoundNotificationService.setWeatherInformation(this.currentWeather);
		}
		finally {
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

	void doBindService() {
		bindService(new Intent(this, YahooWeatherNotifcationService.class), this.mNotificationConnection, Context.BIND_AUTO_CREATE);
		this.mIsNotificatoionServiceBound = true;
	}

	private class DownloadWeatherInfoDataTask extends AsyncTask<Void, Void, YahooWeatherInfo> {

		private final String woeidId;

		public DownloadWeatherInfoDataTask(final String woeidId) {
			this.woeidId = woeidId;
		}

		@Override
		protected YahooWeatherInfo doInBackground(final Void... params) {
			final String url = YahooRequestUtils.getInstance().createWeatherQuery(this.woeidId);

			final RestTemplate restTemplate = new RestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

			final String weatherResponse = restTemplate.getForObject(url, String.class);
			return YahooRequestUtils.getInstance().getWeatherInfo(weatherResponse);
		}

		@Override
		protected void onPostExecute(final YahooWeatherInfo weatherInfo) {
			loadWeatherInfomation(weatherInfo);
		}
	}
}
