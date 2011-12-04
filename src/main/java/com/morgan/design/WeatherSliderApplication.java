package com.morgan.design;

import android.app.Application;

import com.morgan.design.android.domain.YahooWeatherInfo;

public class WeatherSliderApplication extends Application {

	private YahooWeatherInfo currentWeather;
	private String currentWoeid;

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
