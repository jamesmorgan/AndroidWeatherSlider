package com.morgan.design.android.domain;

import com.morgan.design.android.domain.orm.WeatherChoice;

public class YahooWeatherLookup {

	WeatherChoice weatherChoice;
	YahooWeatherInfo weatherInfo;

	public YahooWeatherLookup(final WeatherChoice weatherChoice, final YahooWeatherInfo weatherInfo) {
		this.weatherChoice = weatherChoice;
		this.weatherInfo = weatherInfo;
	}

	public final WeatherChoice getWeatherChoice() {
		return this.weatherChoice;
	}

	public final YahooWeatherInfo getWeatherInfo() {
		return this.weatherInfo;
	}

}
