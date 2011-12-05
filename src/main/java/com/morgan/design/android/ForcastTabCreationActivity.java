package com.morgan.design.android;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.morgan.design.Constants;
import com.morgan.design.WeatherSliderApplication;
import com.morgan.design.android.domain.ForcastEntry;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.util.TemperatureUtils;

public class ForcastTabCreationActivity extends TabActivity {

	private YahooWeatherInfo currentWeather;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.currentWeather = getToLevelApplication().getCurrentWeather();

		// The activity TabHost
		final TabHost tabHost = getTabHost();
		// Resusable TabSpec for each tab
		TabHost.TabSpec spec;

		for (final ForcastEntry forcastEntry : this.currentWeather.getForcastEntries()) {
			if (isNotToday(forcastEntry)) {
				// Create an Intent to launch an Activity for the tab (to be reused)
				final Intent intent = new Intent().setClass(this, ForecastOverviewTabActivity.class);
				intent.putExtra(Constants.FORCAST_ENTRY, forcastEntry);
				intent.putExtra(Constants.TEMPERATURE_UNIT, TemperatureUtils.fromSingleToDegree(this.currentWeather.getTemperatureUnit()));

				// Initialize a TabSpec for each tab and add it to the TabHost
				spec = tabHost.newTabSpec(forcastEntry.getDay()).setIndicator(forcastEntry.getDay(), null).setContent(intent);
				tabHost.addTab(spec);
			}
		}

		tabHost.setCurrentTab(0);
	}

	private boolean isNotToday(final ForcastEntry forcastEntry) {
		return true;
	}

	protected WeatherSliderApplication getToLevelApplication() {
		return ((WeatherSliderApplication) getApplication());
	}
}
