package com.morgan.design;

import android.app.Application;

import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.util.GoogleAnalyticsService;

public class WeatherSliderApplication extends Application {

	private GoogleAnalyticsService googleAnalyticsService;
	private YahooWeatherInfo currentWeather;
	private String currentWoeid;

	@Override
	public void onCreate() {
		super.onCreate();
		this.googleAnalyticsService = GoogleAnalyticsService.create(getApplicationContext());
	}

	public GoogleAnalyticsService getGoogleAnalyticsService() {
		return this.googleAnalyticsService;
	}

	public YahooWeatherInfo getCurrentWeather() {
		return this.currentWeather;
	}

	public void setCurrentWeather(final YahooWeatherInfo currentWeather) {
		this.currentWeather = currentWeather;
	}

	public void setCurrentWoeid(final String currentWoeid) {
		this.currentWoeid = currentWoeid;
	}

	public String getCurrentWoied() {
		return this.currentWoeid;
	}

}
