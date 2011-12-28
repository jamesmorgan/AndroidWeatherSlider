package com.morgan.design.android.service.notifcation;

import android.content.Intent;
import android.os.IBinder;

import com.weatherslider.morgan.design.R;

@Deprecated
public class WeatherNotificationService1 extends BaseNotifcationService {

	private final LocalBinder<WeatherNotificationService1> mBinder = new LocalBinder<WeatherNotificationService1>() {
		@Override
		WeatherNotificationService1 getService() {
			return WeatherNotificationService1.this;
		}
	};

	@Override
	public IBinder onBind(final Intent intent) {
		return this.mBinder;
	}

	@Override
	public LocalBinder<WeatherNotificationService1> getLocalBinder() {
		return this.mBinder;
	}

	@Override
	public int getNotifcationId() {
		return R.string.weather_notification_service_1;
	}
}
