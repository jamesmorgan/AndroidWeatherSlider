package com.morgan.design.android.tasks;

import static com.morgan.design.android.util.ObjectUtils.isBlank;
import static com.morgan.design.android.util.ObjectUtils.isNull;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.morgan.design.android.domain.WeatherLookupEntry;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.domain.types.Temperature;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.YahooRequestUtils;

public class DownloadWeatherInfoDataTaskFromWoeid extends AsyncTask<Void, Void, WeatherLookupEntry> {

	private static final String LOG_TAG = "DownloadWeatherInfoDataTaskFromWoeid";

	private final String woeidId;
	private final Temperature temperature;
	private final OnAsyncQueryCallback<WeatherLookupEntry> asyncQueryCallback;

	private final ConnectivityManager cnnxManager;

	public DownloadWeatherInfoDataTaskFromWoeid(final ConnectivityManager cnnxManager, final String woeidId, final Temperature temperature,
			final OnAsyncQueryCallback<WeatherLookupEntry> asyncQueryCallback) {
		this.cnnxManager = cnnxManager;
		this.woeidId = woeidId;
		this.temperature = temperature;
		this.asyncQueryCallback = asyncQueryCallback;
	}

	// http://stackoverflow.com/questions/2775628/android-how-to-periodically-send-location-to-a-server

	@Override
	protected WeatherLookupEntry doInBackground(final Void... params) {
		this.asyncQueryCallback.onInitiateExecution();

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
				final YahooWeatherInfo weatherInfo = YahooRequestUtils.getInstance().getWeatherInfo(weatherResponse);

				return new WeatherLookupEntry(this.woeidId, weatherInfo);

			}
		}
		catch (final Throwable e) {
			Logger.e(LOG_TAG, "Unknonw error when running periodic download weather data task", e);
		}
		return null;
	}

	@Override
	protected void onPreExecute() {
		this.asyncQueryCallback.onPreLookup();
	}

	@Override
	protected void onPostExecute(final WeatherLookupEntry weatherInfo) {
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
