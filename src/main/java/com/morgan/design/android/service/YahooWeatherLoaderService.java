package com.morgan.design.android.service;

import static com.morgan.design.Constants.CURRENT_WEATHER_WOEID;
import static com.morgan.design.Constants.FROM_BOOT;
import static com.morgan.design.android.util.ObjectUtils.isNotBlank;
import static com.morgan.design.android.util.ObjectUtils.isNotEmpty;
import static com.morgan.design.android.util.ObjectUtils.isNotNull;
import static com.morgan.design.android.util.ObjectUtils.stringHasValue;

import java.util.List;

import android.content.Intent;

import com.morgan.design.Constants;
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
			boolean fromBoot = false;
			if (intent.hasExtra(Constants.FROM_BOOT)) {
				fromBoot = intent.getBooleanExtra(FROM_BOOT, false);
			}

			if (fromBoot) {
				Logger.d(LOG_TAG, "Starting service from phone boot");

				final List<WeatherChoice> weatherInfos = this.woeidChoiceDao.findActive();
				if (isNotEmpty(weatherInfos)) {
					Logger.d(LOG_TAG, "Found [%s] active notifications to start", weatherInfos.size());
					for (final WeatherChoice weatherChoice : weatherInfos) {
						final String woeid = weatherChoice.getWoeid();
						if (!weatherChoice.isActive() && stringHasValue(woeid)) {
							downloadWeatherData(this, woeid, PreferenceUtils.getTemperatureMode(getApplicationContext()));
						}
					}
				}
				else {
					Logger.d(LOG_TAG, "User preference start on boot disabled");
				}
			}
			else {
				if (intent.hasExtra(CURRENT_WEATHER_WOEID)) {
					final String woeidId = intent.getStringExtra(CURRENT_WEATHER_WOEID);
					Logger.d(LOG_TAG, "onStartCommand : Found woeidId: " + woeidId);
					if (isNotBlank(woeidId)) {
						downloadWeatherData(this, woeidId, PreferenceUtils.getTemperatureMode(getApplicationContext()));
					}
				}
			}
		}
		return START_STICKY;
	}

}
