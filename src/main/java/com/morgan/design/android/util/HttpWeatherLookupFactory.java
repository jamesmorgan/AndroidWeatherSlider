package com.morgan.design.android.util;

import static com.morgan.design.android.util.ObjectUtils.isBlank;
import static com.morgan.design.android.util.ObjectUtils.isNull;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.morgan.design.android.domain.GeocodeResult;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.domain.YahooWeatherLookup;
import com.morgan.design.android.domain.orm.WeatherChoice;
import com.morgan.design.android.domain.types.Temperature;

public class HttpWeatherLookupFactory {

	private static final String LOG_TAG = "HttpWeatherLookupFactory";

	public static YahooWeatherLookup getForWeatherChoice(final WeatherChoice weatherChoice, final Temperature temperature,
			final ConnectivityManager cnnxManager) {
		try {
			if (isBlank(weatherChoice.getWoeid())) {
				Logger.e(LOG_TAG, "No locaiton WOIED found. Skipping pulse action.");
				return null;
			}
			if (isNotConnectedToNetwork(cnnxManager)) {
				Logger.e(LOG_TAG, "No usable network. Skipping pulse action.");
				return null;
			}
			else {
				Logger.d(LOG_TAG, "Looking up weather details for woeid=[%s]", weatherChoice.getWoeid());

				final String url = YahooRequestUtils.getInstance().createWeatherQuery(weatherChoice.getWoeid(), temperature);

				final RestTemplate restTemplate = new RestTemplate();
				restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

				final String weatherResponse = restTemplate.getForObject(url, String.class);
				final YahooWeatherInfo weatherInfo = YahooRequestUtils.getInstance().getWeatherInfo(weatherResponse);
				return new YahooWeatherLookup(weatherChoice, weatherInfo);
			}
		}
		catch (final Throwable e) {
			Logger.e(LOG_TAG, "Unknonw error when running periodic download weather data task", e);
		}
		return null;
	}

	public static YahooWeatherInfo getForGeocodeResult(final GeocodeResult geocodeResult, final Temperature temperature,
			final ConnectivityManager cnnxManager) {
		final String woeidId = geocodeResult.getWoeid();

		try {
			if (isBlank(woeidId)) {
				Logger.e(LOG_TAG, "No locaiton WOIED found. Skipping pulse action.");
				return null;
			}
			if (isNotConnectedToNetwork(cnnxManager)) {
				Logger.e(LOG_TAG, "No usable network. Skipping pulse action.");
				return null;
			}
			else {
				Logger.d(LOG_TAG, "Looking up weather details for woeid=[%s]", woeidId);

				final String url = YahooRequestUtils.getInstance().createWeatherQuery(woeidId, temperature);

				final RestTemplate restTemplate = new RestTemplate();
				restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

				final String weatherResponse = restTemplate.getForObject(url, String.class);
				return YahooRequestUtils.getInstance().getWeatherInfo(weatherResponse);
			}
		}
		catch (final Throwable e) {
			Logger.e(LOG_TAG, "Unknonw error when running periodic download weather data task", e);
		}
		return null;
	}

	private static boolean isNotConnectedToNetwork(final ConnectivityManager cnnxManager) {
		if (isNull(cnnxManager)) {
			return false;
		}
		final NetworkInfo ni = cnnxManager.getActiveNetworkInfo();
		return ni == null || !ni.isAvailable() || !ni.isConnected();
	}
}
