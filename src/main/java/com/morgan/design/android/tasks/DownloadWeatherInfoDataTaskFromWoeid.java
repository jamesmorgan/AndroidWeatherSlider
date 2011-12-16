package com.morgan.design.android.tasks;

import static com.morgan.design.android.util.ObjectUtils.isBlank;
import static com.morgan.design.android.util.ObjectUtils.isNull;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.SystemClock;

import com.morgan.design.Constants;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.domain.types.Temperature;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.PreferenceUtils;
import com.morgan.design.android.util.YahooRequestUtils;

public class DownloadWeatherInfoDataTaskFromWoeid extends AsyncTask<Void, Void, YahooWeatherInfo> {

	private static final String LOG_TAG = "DownloadWeatherInfoDataTaskFromWoeid";

	private final String woeidId;
	private final Temperature temperature;
	private final OnAsyncQueryCallback<YahooWeatherInfo> asyncQueryCallback;

	private final Context context;
	private final ConnectivityManager cnnxManager;
	private final AlarmManager alarms;

	public DownloadWeatherInfoDataTaskFromWoeid(final Context context, final ConnectivityManager cnnxManager, final AlarmManager alarms,
			final String woeidId, final Temperature temperature, final OnAsyncQueryCallback<YahooWeatherInfo> asyncQueryCallback) {
		this.context = context;
		this.cnnxManager = cnnxManager;
		this.alarms = alarms;
		this.woeidId = woeidId;
		this.temperature = temperature;
		this.asyncQueryCallback = asyncQueryCallback;
	}

	// http://stackoverflow.com/questions/2775628/android-how-to-periodically-send-location-to-a-server

	@Override
	protected YahooWeatherInfo doInBackground(final Void... params) {

		try {
			if (isBlank(this.woeidId)) {
				Logger.e(LOG_TAG, "No locaiton WOIED found. Skipping pulse action.");
				return null;
			}
			if (isNotConnectedToNetwork()) {
				Logger.e(LOG_TAG, "No usable network. Skipping pulse action.");
				return null;
			}
			else {
				Logger.d(LOG_TAG, "Looking up weather details for woeid=[%s]", this.woeidId);

				final String url = YahooRequestUtils.getInstance().createWeatherQuery(this.woeidId, this.temperature);

				final RestTemplate restTemplate = new RestTemplate();
				restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

				final String weatherResponse = restTemplate.getForObject(url, String.class);
				return YahooRequestUtils.getInstance().getWeatherInfo(weatherResponse);
			}
		}
		catch (final Throwable e) {
			Logger.e(LOG_TAG, "Unknonw error when running periodic download weather data task", e);
		}
		finally {
			final Intent intentOnAlarm = new Intent(Constants.RELOAD_WEATHER_BROADCAST);
			intentOnAlarm.putExtra(Constants.CURRENT_WEATHER_WOEID, this.woeidId);
			final PendingIntent broadcast = PendingIntent.getBroadcast(this.context, 0, intentOnAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

			final int pollingSchedule = Integer.parseInt(PreferenceUtils.getPollingSchedule(this.context.getApplicationContext()));
			Logger.d(LOG_TAG, "Polling scheduled [%s]", pollingSchedule);

			final long timer = SystemClock.elapsedRealtime() + (pollingSchedule * (60 * 1000));
			Logger.d(LOG_TAG, "Adding next repating alarm, timer [%s]", timer);

			this.alarms.set(AlarmManager.ELAPSED_REALTIME, timer, broadcast);
		}
		return null;
	}

	@Override
	protected void onPostExecute(final YahooWeatherInfo weatherInfo) {
		this.asyncQueryCallback.onPostLookup(weatherInfo);
	}

	private boolean isNotConnectedToNetwork() {
		if (isNull(this.cnnxManager)) {
			return false;
		}
		final NetworkInfo ni = this.cnnxManager.getActiveNetworkInfo();
		return ni == null || !ni.isAvailable() || !ni.isConnected();
	}
}
