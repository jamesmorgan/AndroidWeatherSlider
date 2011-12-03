package com.morgan.design.android;

import android.app.Activity;
import android.os.Bundle;

import com.weatherslider.morgan.design.R;

public class WeatherOverviewActivity extends Activity {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_overview);
	}

}
