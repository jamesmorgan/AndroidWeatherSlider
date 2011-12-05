package com.morgan.design.android;

import static com.morgan.design.android.util.ObjectUtils.stringHasValue;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.morgan.design.WeatherSliderApplication;
import com.morgan.design.android.SimpleGestureFilter.SimpleGestureListener;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.util.DegreeToDirectionConverter;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.TemperatureUtils;
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

	private TextView location_1;
	private TextView location_2;
	private TextView location_lat_long;

	private void setLocationDetails() {
		this.location_1 = (TextView) findViewById(R.id.location_a);
		this.location_2 = (TextView) findViewById(R.id.location_b);
		this.location_lat_long = (TextView) findViewById(R.id.location_lat_long);

		String location2 = "";
		if (stringHasValue(this.currentWeather.getCountry())) {
			location2 += this.currentWeather.getCountry();
		}
		if (stringHasValue(this.currentWeather.getRegion())) {
			location2 += stringHasValue(location2)
					? ", " + this.currentWeather.getRegion()
					: this.currentWeather.getRegion();
		}

		this.location_1.setText(this.currentWeather.getCity());
		this.location_2.setText(location2);
		this.location_lat_long.setText(String.format("Lat: %s | Long: %s", this.currentWeather.getLatitude(),
				this.currentWeather.getLongitude()));
	}

	private TextView wind_speed;
	private TextView wind_chill;
	private TextView wind_direction;

	private void setWindDetails() {
		this.wind_speed = (TextView) findViewById(R.id.wind_speed);
		this.wind_chill = (TextView) findViewById(R.id.wind_chill);
		this.wind_direction = (TextView) findViewById(R.id.wind_direction);

		this.wind_speed.setText(this.currentWeather.getWindSpeed() + this.currentWeather.getWindSpeedUnit());
		this.wind_chill.setText(this.currentWeather.getWindChill() + TemperatureUtils.fromSingleToDegree(this.currentWeather.getTemperatureUnit()));
		this.wind_direction.setText(DegreeToDirectionConverter.fromDegreeToHumanDirection(this.currentWeather.getWindDirection()));
	}

	private TextView sun_rise;
	private TextView sun_set;

	private void setSunDetails() {
		this.sun_rise = (TextView) findViewById(R.id.sun_rise);
		this.sun_set = (TextView) findViewById(R.id.sun_set);

		this.sun_rise.setText(this.currentWeather.getSunRise());
		this.sun_set.setText(this.currentWeather.getSunSet());
	}

	private TextView temperature;
	private TextView humidity;

	private void setTemperatureDetails() {
		this.temperature = (TextView) findViewById(R.id.temperature);
		this.humidity = (TextView) findViewById(R.id.humidity);

		this.temperature.setText(this.currentWeather.getCurrentTemp()
			+ TemperatureUtils.fromSingleToDegree(this.currentWeather.getTemperatureUnit()));
		this.humidity.setText(this.currentWeather.getHumidity() + "%");
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
