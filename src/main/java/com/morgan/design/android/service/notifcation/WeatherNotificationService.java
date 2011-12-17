package com.morgan.design.android.service.notifcation;

import com.morgan.design.android.domain.YahooWeatherInfo;

public interface WeatherNotificationService {

	public void updatePreferences();

	public void setWeatherInformation(final YahooWeatherInfo currentWeather);

	public void removeNotification();

	public boolean isBound();

}
