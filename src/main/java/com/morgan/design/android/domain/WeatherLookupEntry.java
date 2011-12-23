package com.morgan.design.android.domain;

public class WeatherLookupEntry {

	private final String woeid;
	private final YahooWeatherInfo weatherInfo;

	public WeatherLookupEntry(final String woeid, final YahooWeatherInfo weatherInfo) {
		this.woeid = woeid;
		this.weatherInfo = weatherInfo;
	}

	public YahooWeatherInfo getWeatherInfo() {
		return this.weatherInfo;
	}

	public String getWoeid() {
		return this.woeid;
	}
}
