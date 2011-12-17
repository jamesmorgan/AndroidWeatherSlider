package com.morgan.design.android.service.notifcation;

import android.content.Intent;
import android.os.IBinder;

import com.weatherslider.morgan.design.R;

public class WeatherNotificationService3 extends BaseNotifcationService {

	private final LocalBinder<WeatherNotificationService3> mBinder = new LocalBinder<WeatherNotificationService3>() {
		@Override
		WeatherNotificationService3 getService() {
			return WeatherNotificationService3.this;
		}
	};

	@Override
	public IBinder onBind(final Intent intent) {
		return this.mBinder;
	}

	@Override
	public LocalBinder<WeatherNotificationService3> getLocalBinder() {
		return this.mBinder;
	}

	@Override
	public int getNotifcationId() {
		return R.string.weather_notification_service_3;
	}

}
