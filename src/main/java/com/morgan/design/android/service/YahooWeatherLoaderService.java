package com.morgan.design.android.service;

import static com.morgan.design.android.util.ObjectUtils.isNotBlank;
import static com.morgan.design.android.util.ObjectUtils.isNotNull;
import android.content.Intent;
import android.os.Bundle;

import com.morgan.design.Constants;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.PreferenceUtils;

public class YahooWeatherLoaderService extends AbstractBoundWeatherNotificationService {

	private static final String LOG_TAG = "YahooWeatherLoaderService";

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		super.onStartCommand(intent, flags, startId);
		Logger.d(LOG_TAG, "onStartCommand found");

		if (isNotNull(intent)) {
			final Bundle extras = intent.getExtras();
			if (isNotNull(extras)) {
				final String woeidId = intent.getStringExtra(Constants.CURRENT_WEATHER_WOEID);
				Logger.d("YahooWeatherLoaderService", "onStartCommand : Found woeidId: " + woeidId);
				if (isNotBlank(woeidId)) {
					this.woeidId = woeidId;
					downloadWeatherData(this, woeidId, PreferenceUtils.getTemperatureMode(getApplicationContext()));
				}
			}
		}

		return START_STICKY;
	}

}
