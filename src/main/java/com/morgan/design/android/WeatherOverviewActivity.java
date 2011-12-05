package com.morgan.design.android;

import static com.morgan.design.android.util.ObjectUtils.stringHasValue;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.morgan.design.WeatherSliderApplication;
import com.morgan.design.android.SimpleGestureFilter.SimpleGestureListener;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.util.DegreeToDirectionConverter;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.PressureUtils;
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
		this.detector = new SimpleGestureFilter(this, this);
		this.detector.setEnabled(true);

		if (null == this.currentWeather) {
			Logger.d(LOG_TAG, "Current weather if null, unable to create overview screen");
			return;
		}
		Logger.d(LOG_TAG, this.currentWeather);

		setDescriptionAndImage();
		setLocationDetails();
		setWindDetails();
		setSunDetails();
		setTemperatureDetails();

		setUpMoreInformationLink();
	}

	private TextView weather_description;
	private TextView main_temperature;
	private ImageView weather_image;

	private void setDescriptionAndImage() {
		this.weather_description = (TextView) findViewById(R.id.weather_description);
		this.weather_image = (ImageView) findViewById(R.id.weather_image);
		this.main_temperature = (TextView) findViewById(R.id.main_temperature);

		this.weather_description.setText(valueOrDefault(this.currentWeather.getCurrentText(), "N/A"));
		this.main_temperature.setText(this.currentWeather.getCurrentTemp()
			+ TemperatureUtils.fromSingleToDegree(this.currentWeather.getTemperatureUnit()));
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

		this.location_1.setText(valueOrDefault(this.currentWeather.getCity(), "N/A"));
		this.location_2.setText(valueOrDefault(location2, "N/A"));
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

		this.wind_speed.setText(valueOrDefault(this.currentWeather.getWindSpeed() + this.currentWeather.getWindSpeedUnit(), "N/A"));
		this.wind_chill.setText(this.currentWeather.getWindChill()
			+ TemperatureUtils.fromSingleToDegree(this.currentWeather.getTemperatureUnit()));
		this.wind_direction.setText(DegreeToDirectionConverter.fromDegreeToHumanDirection(this.currentWeather.getWindDirection()));
	}

	private TextView sun_rise;
	private TextView sun_set;

	private void setSunDetails() {
		this.sun_rise = (TextView) findViewById(R.id.sun_rise);
		this.sun_set = (TextView) findViewById(R.id.sun_set);

		this.sun_rise.setText(valueOrDefault(this.currentWeather.getSunRise(), "N/A"));
		this.sun_set.setText(valueOrDefault(this.currentWeather.getSunSet(), "N/A"));
	}

	private TextView temperature;
	private TextView humidity;
	private TextView pressure;
	private ImageView pressure_icon;

	private void setTemperatureDetails() {
		this.temperature = (TextView) findViewById(R.id.temperature);
		this.humidity = (TextView) findViewById(R.id.humidity);
		this.pressure = (TextView) findViewById(R.id.pressure);
		this.pressure_icon = (ImageView) findViewById(R.id.pressure_icon);

		this.temperature.setText(this.currentWeather.getCurrentTemp()
			+ TemperatureUtils.fromSingleToDegree(this.currentWeather.getTemperatureUnit()));
		this.humidity.setText(this.currentWeather.getHumidity() + "%");
		this.pressure.setText(valueOrDefault(this.currentWeather.getPressure() + this.currentWeather.getPressureUnit(), "N/A"));
		this.pressure_icon.setImageResource(PressureUtils.getPressureStateImage(this.currentWeather.getRising()));
	}

	private CharSequence valueOrDefault(final String string, final String defaultValue) {
		return stringHasValue(string)
				? string
				: defaultValue;
	}

	private TextView more_information_link;

	private void setUpMoreInformationLink() {
		this.more_information_link = (TextView) findViewById(R.id.more_information_link);
		this.more_information_link.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				final Intent browserIntent =
						new Intent(Intent.ACTION_VIEW, Uri.parse(WeatherOverviewActivity.this.currentWeather.getLink()));
				startActivity(browserIntent);
			}
		});
		this.more_information_link.setMovementMethod(LinkMovementMethod.getInstance());
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
				if (null != this.currentWeather) {
					final Intent intent = new Intent(this, ForcastTabCreationActivity.class);
					startActivity(intent);
				}
				return;
		}
	}

	@Override
	public void onDoubleTap() {
		// Do nothing at present
	}
}
