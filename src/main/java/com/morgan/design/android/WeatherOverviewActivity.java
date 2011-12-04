package com.morgan.design.android;

import android.app.Activity;
import android.os.Bundle;

import com.morgan.design.WeatherSliderApplication;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.util.Logger;
import com.weatherslider.morgan.design.R;

public class WeatherOverviewActivity extends Activity {

	private static final String LOG_TAG = "WeatherOverviewActivity";
	private YahooWeatherInfo currentWeather;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_overview);

		final WeatherSliderApplication appState = ((WeatherSliderApplication) getApplicationContext());
		this.currentWeather = appState.getCurrentWeather();

		Logger.d(LOG_TAG, this.currentWeather);
	}
}
