package com.morgan.design.android.service.notifcation;

import android.content.Intent;
import android.os.IBinder;

import com.weatherslider.morgan.design.R;

public class WeatherNotificationService2 extends BaseNotifcationService {

	private final LocalBinder<WeatherNotificationService2> mBinder = new LocalBinder<WeatherNotificationService2>() {
		@Override
		WeatherNotificationService2 getService() {
			return WeatherNotificationService2.this;
		}
	};

	@Override
	public IBinder onBind(final Intent intent) {
		return this.mBinder;
	}

	@Override
	public LocalBinder<WeatherNotificationService2> getLocalBinder() {
		return this.mBinder;
	}

	@Override
	public int getNotifcationId() {
		return R.string.weather_notification_service_2;
	}

}
