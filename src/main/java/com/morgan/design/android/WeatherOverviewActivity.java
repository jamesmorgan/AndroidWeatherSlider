package com.morgan.design.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.morgan.design.WeatherSliderApplication;
import com.morgan.design.android.SimpleGestureFilter.SimpleGestureListener;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.util.Logger;
import com.weatherslider.morgan.design.R;

public class WeatherOverviewActivity extends Activity implements SimpleGestureListener {

	private static final String LOG_TAG = "WeatherOverviewActivity";

	private YahooWeatherInfo currentWeather;
	private SimpleGestureFilter detector;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_overview);

		final WeatherSliderApplication appState = ((WeatherSliderApplication) getApplicationContext());
		this.currentWeather = appState.getCurrentWeather();
		if (null == this.currentWeather) {
			// TODO handle null?
		}
		Logger.d(LOG_TAG, this.currentWeather);

		this.detector = new SimpleGestureFilter(this, this);
		this.detector.setEnabled(true);

		setDescriptionAndImage();
		setLocationDetails();
		setWindDetails();
		setSunDetails();
		setTemperatureDetails();
	}

	private TextView weather_description;
	private ImageView weather_image;

	private void setDescriptionAndImage() {
		this.weather_description = (TextView) findViewById(R.id.weather_description);
		this.weather_image = (ImageView) findViewById(R.id.weather_image);

		this.weather_description.setText(this.currentWeather.getCurrentText());
		this.weather_image.setImageResource(this.currentWeather.getCurrentCode());
	}

	private void setLocationDetails() {
		// TODO Auto-generated method stub

	}

	private void setWindDetails() {
		// TODO Auto-generated method stub

	}

	private void setSunDetails() {
		// TODO Auto-generated method stub

	}

	private void setTemperatureDetails() {
		// TODO Auto-generated method stub

	}

	// /////////////////////////////////////////////
	// ////////// Swipe Gestures ///////////////////
	// /////////////////////////////////////////////

	@Override
	public boolean dispatchTouchEvent(final MotionEvent me) {
		this.detector.onTouchEvent(me);
		return super.dispatchTouchEvent(me);
	}

	@Override
	public void onSwipe(final int direction) {
		switch (direction) {
			case SimpleGestureFilter.SWIPE_RIGHT:
				//
			case SimpleGestureFilter.SWIPE_LEFT:
				//
		}
	}

	@Override
	public void onDoubleTap() {
		// Do nothing at present
	}
}
