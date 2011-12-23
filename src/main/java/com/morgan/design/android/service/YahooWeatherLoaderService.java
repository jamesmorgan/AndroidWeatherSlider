package com.morgan.design.android.service;

import static com.morgan.design.Constants.CONNECTIVITY_CHANGED;
import static com.morgan.design.Constants.FROM_BOOT;
import static com.morgan.design.Constants.FROM_INACTIVE_SERVICE;
import static com.morgan.design.Constants.FROM_LAUNCHER;
import static com.morgan.design.Constants.FROM_LOAD_WEATHER;
import static com.morgan.design.Constants.WOEID;
import static com.morgan.design.android.util.ObjectUtils.isNotEmpty;
import static com.morgan.design.android.util.ObjectUtils.isNotNull;
import static com.morgan.design.android.util.ObjectUtils.stringHasValue;

import java.util.List;

import android.content.Intent;

import com.morgan.design.android.domain.orm.WeatherChoice;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.PreferenceUtils;

public class YahooWeatherLoaderService extends AbstractBoundWeatherNotificationService {

	private static final String LOG_TAG = "YahooWeatherLoaderService";

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		super.onStartCommand(intent, flags, startId);
		Logger.d(LOG_TAG, "onStartCommand found");

		if (isNotNull(intent)) {

			// Handle phone boot service start
			if (intent.hasExtra(FROM_BOOT) && intent.getBooleanExtra(FROM_BOOT, false)) {
				if (PreferenceUtils.shouldStartOnBoot(this)) {
					Logger.d(LOG_TAG, "Loading weather data, triggered from phone boot");
					reloadActiveServices();
				}
			}
			// Handle connectivity change service start
			else if (intent.hasExtra(CONNECTIVITY_CHANGED) && intent.getBooleanExtra(CONNECTIVITY_CHANGED, false)) {
				Logger.d(LOG_TAG, "Loading weather data, triggered from connectivity changed");
				reloadActiveServices();
			}
			// Handle from launch alarm
			else if (intent.hasExtra(FROM_LAUNCHER) && intent.getBooleanExtra(FROM_LAUNCHER, false)) {
				Logger.d(LOG_TAG, "Loading weather data, triggered from alarm");
				reloadActiveServices();
			}
			// Handle start disabled notification
			else if (intent.hasExtra(FROM_INACTIVE_SERVICE) && intent.getBooleanExtra(FROM_INACTIVE_SERVICE, false)) {
				Logger.d(LOG_TAG, "Loading weather data, triggered from inacitve notification load");
				downloadWeatherData(this, intent.getStringExtra(WOEID));
			}
			// Handle newly added weather location
			else if (intent.hasExtra(FROM_LOAD_WEATHER) && intent.getBooleanExtra(FROM_LOAD_WEATHER, false)) {
				Logger.d(LOG_TAG, "Loading weather data, triggered from newly added weather locaiton");
				downloadWeatherData(this, intent.getStringExtra(WOEID));
			}

		}
		return START_STICKY;
	}

	private void reloadActiveServices() {
		final List<WeatherChoice> weatherInfos = this.woeidChoiceDao.findActive();
		if (isNotEmpty(weatherInfos)) {
			Logger.d(LOG_TAG, "Found [%s] active notifications to start", weatherInfos.size());
			for (final WeatherChoice weatherChoice : weatherInfos) {
				final String woeid = weatherChoice.getWoeid();
				if (!weatherChoice.isActive() && stringHasValue(woeid)) {
					downloadWeatherData(this, woeid);
				}
			}
		}
	}

}
