package com.morgan.design.android;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TabHost;

import com.morgan.design.Constants;
import com.morgan.design.WeatherSliderApplication;
import com.morgan.design.android.SimpleGestureFilter.SimpleGestureListener;
import com.morgan.design.android.domain.ForcastEntry;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.domain.types.Temperature;

public class ForcastTabCreationActivity extends TabActivity implements SimpleGestureListener {

	private YahooWeatherInfo currentWeather;
	private SimpleGestureFilter detector;
	private int currentIndex;
	private TabHost tabHost;
	private int tabCount;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.detector = new SimpleGestureFilter(this, this);
		this.detector.setEnabled(true);

		this.currentWeather = getToLevelApplication().getCurrentWeather();

		this.tabHost = getTabHost();
		TabHost.TabSpec spec;

		for (final ForcastEntry forcastEntry : this.currentWeather.getForcastEntries()) {
			if (isNotToday(forcastEntry)) {

				final Intent intent = new Intent().setClass(this, ForecastOverviewTabActivity.class);
				intent.putExtra(Constants.FORCAST_ENTRY, forcastEntry);
				intent.putExtra(Constants.TEMPERATURE_UNIT, Temperature.withDegree(this.currentWeather.getTemperatureUnit().getAbrev()));

				spec =
						this.tabHost.newTabSpec(forcastEntry.getDayOfWeek().full())
							.setIndicator(forcastEntry.getDayOfWeek().full(), null)
							.setContent(intent);

				this.tabHost.addTab(spec);
			}
		}

		this.currentIndex = 0;
		this.tabHost.setCurrentTab(this.currentIndex);
		this.tabCount = this.tabHost.getTabWidget().getTabCount();
	}

	private boolean isNotToday(final ForcastEntry forcastEntry) {
		return true;
	}

	protected WeatherSliderApplication getToLevelApplication() {
		return ((WeatherSliderApplication) getApplication());
	}

	// /////////////////////////////////////////////
	// ////////// Swipe Gestures ///////////////////
	// /////////////////////////////////////////////

	@Override
	public void onSwipe(final int direction) {
		switch (direction) {
			case SimpleGestureFilter.SWIPE_RIGHT:
				if (0 == this.currentIndex) {
					finish();
				}
				this.tabHost.setCurrentTab(--this.currentIndex);
				break;
			case SimpleGestureFilter.SWIPE_LEFT:
				if (this.tabCount != this.currentIndex + 1) {
					this.tabHost.setCurrentTab(++this.currentIndex);
				}
				break;
		}
	}

	@Override
	public boolean dispatchTouchEvent(final MotionEvent me) {
		this.detector.onTouchEvent(me);
		return super.dispatchTouchEvent(me);
	}

	@Override
	public void onDoubleTap() {
		// Do nothing at present
	}
}
