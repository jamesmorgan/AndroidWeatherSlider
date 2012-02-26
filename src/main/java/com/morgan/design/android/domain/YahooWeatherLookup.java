package com.morgan.design.android.domain;

import com.morgan.design.android.dao.orm.WeatherChoice;

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

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("YahooWeatherLookup [weatherChoice=")
			.append(this.weatherChoice)
			.append(", weatherInfo=")
			.append(this.weatherInfo)
			.append("]");
		return builder.toString();
	}

}
