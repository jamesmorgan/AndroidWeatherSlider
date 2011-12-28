package com.morgan.design.android.util;

import static com.morgan.design.android.util.ObjectUtils.isBlank;
import static com.morgan.design.android.util.ObjectUtils.isNull;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.morgan.design.android.dao.orm.WeatherChoice;
import com.morgan.design.android.domain.GeocodeResult;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.domain.YahooWeatherLookup;
import com.morgan.design.android.domain.types.Temperature;

public class HttpWeatherLookupFactory {

	private static final String LOG_TAG = "HttpWeatherLookupFactory";

	public static YahooWeatherLookup getForWeatherChoice(final WeatherChoice weatherChoice, final Temperature temperature,
			final ConnectivityManager cnnxManager) {
		if (null == weatherChoice) {
			Logger.i(LOG_TAG, "WeatherChoice is null, no WOIED found. Unable to get yahoo weather info.");
			return null;
		}
		try {
			if (isBlank(weatherChoice.getWoeid())) {
				Logger.i(LOG_TAG, "No locaiton WOIED found.");
				return null;
			}
			if (isNotConnectedToNetwork(cnnxManager)) {
				Logger.i(LOG_TAG, "No usable network.");
				return null;
			}
			else {
				Logger.d(LOG_TAG, "Looking up weather details for woeid=[%s]", weatherChoice.getWoeid());

				final String url = YahooRequestUtils.getInstance().createWeatherQuery(weatherChoice, temperature);

				final YahooWeatherInfo weatherInfo =
						YahooRequestUtils.getInstance().getWeatherInfo(RestTemplateFactory.createAndQuery(url));
				return new YahooWeatherLookup(weatherChoice, weatherInfo);
			}
		}
		catch (final Throwable e) {
			Logger.i(LOG_TAG, "Unknonw error when getting weather data task", e);
		}
		return null;
	}

	public static YahooWeatherInfo getForGeocodeResult(final GeocodeResult geocodeResult, final Temperature temperature,
			final ConnectivityManager cnnxManager) {
		if (null == geocodeResult) {
			Logger.i(LOG_TAG, "GeocodeResult is null, no WOIED found. Unable to get yahoo weather info.");
			return null;
		}
		try {
			final String woeidId = geocodeResult.getWoeid();

			if (isBlank(woeidId)) {
				Logger.i(LOG_TAG, "No locaiton WOIED found.");
				return null;
			}
			if (isNotConnectedToNetwork(cnnxManager)) {
				Logger.i(LOG_TAG, "No usable network.");
				return null;
			}
			else {
				Logger.d(LOG_TAG, "Looking up weather details for woeid=[%s]", woeidId);

				final String url = YahooRequestUtils.getInstance().createWeatherQuery(geocodeResult, temperature);

				return YahooRequestUtils.getInstance().getWeatherInfo(RestTemplateFactory.createAndQuery(url));
			}
		}
		catch (final Throwable e) {
			Logger.i(LOG_TAG, "Unknonw error when getting weather data task", e);
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
