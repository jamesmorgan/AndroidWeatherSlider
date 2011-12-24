package com.morgan.design.android;

import static com.morgan.design.Constants.SERVICE_ID;
import static com.morgan.design.android.util.ObjectUtils.isNotEmpty;
import static com.morgan.design.android.util.ObjectUtils.isZero;
import static com.morgan.design.android.util.ObjectUtils.stringHasValue;
import static com.morgan.design.android.util.ObjectUtils.valueOrDefault;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.morgan.design.Constants;
import com.morgan.design.WeatherSliderApplication;
import com.morgan.design.android.SimpleGestureFilter.SimpleGestureListener;
import com.morgan.design.android.domain.ForcastEntry;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.domain.types.IconFactory;
import com.morgan.design.android.domain.types.Temperature;
import com.morgan.design.android.domain.types.Wind;
import com.morgan.design.android.domain.types.WindSpeed;
import com.morgan.design.android.util.DateUtils;
import com.morgan.design.android.util.GoogleAnalyticsService;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.PressureUtils;
import com.weatherslider.morgan.design.R;

public class WeatherOverviewActivity extends Activity implements SimpleGestureListener {

	private static final String LOG_TAG = "WeatherOverviewActivity";

	private YahooWeatherInfo currentWeather;
	private SimpleGestureFilter detector;

	private GoogleAnalyticsService googleAnalyticsService;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_overview);

		final Intent intent = getIntent();
		if (null != intent) {
			if (intent.hasExtra(SERVICE_ID)) {
				final int serviceId = intent.getIntExtra(SERVICE_ID, 0);
				if (isZero(serviceId)) {
					Logger.w(LOG_TAG, "Service ID is null, this should not happen");
					finish();
				}
				// final WeatherLookupEntry lookupEntry = getTopLevelApplication().getWeatherForSerivceId(serviceId);
				// this.currentWeather = lookupEntry.getWeatherInfo();
				// if (isNull(this.currentWeather)) {
				// Logger.w(LOG_TAG, "Unable to find weather for service ID [%s], this should not happen", serviceId);
				// finish();
				// }
			}
		}

		final WeatherSliderApplication appState = getTopLevelApplication();
		this.googleAnalyticsService = appState.getGoogleAnalyticsService();
		this.detector = new SimpleGestureFilter(this, this);
		this.detector.setEnabled(true);

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
			+ Temperature.withDegree(this.currentWeather.getTemperatureUnit().getAbrev()));
		this.weather_image.setImageResource(IconFactory.getImageResourceFromCode(this.currentWeather.getCurrentCode()));
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

		this.wind_speed.setText(WindSpeed.fromSpeedAndUnit(this, this.currentWeather.getWindSpeed(), this.currentWeather.getWindSpeedUnit()));
		this.wind_chill.setText(this.currentWeather.getWindChill()
			+ Temperature.withDegree(this.currentWeather.getTemperatureUnit().getAbrev()));
		this.wind_direction.setText(Wind.fromDegreeToHumanDirection(this.currentWeather.getWindDirection()));
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

	private void setTemperatureDetails() {
		this.temperature = (TextView) findViewById(R.id.temperature);
		this.humidity = (TextView) findViewById(R.id.humidity);
		this.pressure = (TextView) findViewById(R.id.pressure);

		this.temperature.setText(this.currentWeather.getCurrentTemp()
			+ Temperature.withDegree(this.currentWeather.getTemperatureUnit().getAbrev()));
		this.humidity.setText(this.currentWeather.getHumidityPercentage() + "%");
		this.pressure.setText(valueOrDefault(this.currentWeather.getPressure() + this.currentWeather.getPressureUnit(), "N/A"));
		this.pressure.setCompoundDrawablesWithIntrinsicBounds(PressureUtils.getPressureStateImage(this.currentWeather.getRising()), 0, 0, 0);
	}

	private TextView more_information_link;
	private TextView last_updated_date_time;

	private void setUpMoreInformationLink() {
		this.more_information_link = (TextView) findViewById(R.id.more_information_link);
		this.more_information_link.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				final Intent browserIntent =
						new Intent(Intent.ACTION_VIEW, Uri.parse(WeatherOverviewActivity.this.currentWeather.getLink()));
				startActivity(browserIntent);
				WeatherOverviewActivity.this.googleAnalyticsService.trackPageView(WeatherOverviewActivity.this,
						GoogleAnalyticsService.WEATHER_OVERVIEW);
			}
		});
		this.more_information_link.setMovementMethod(LinkMovementMethod.getInstance());

		this.last_updated_date_time = (TextView) findViewById(R.id.last_updated_date_time);
		this.last_updated_date_time.setText(DateUtils.dateToSimpleDateFormat(this.currentWeather.getCurrentDate()));
	}

	protected WeatherSliderApplication getTopLevelApplication() {
		return ((WeatherSliderApplication) getApplication());
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
			case SimpleGestureFilter.SWIPE_LEFT:
				if (null != this.currentWeather) {

					final List<ForcastEntry> entries = this.currentWeather.getForcastEntries();
					if (isNotEmpty(entries)) {
						// HACK to put forecast details in intent as current weather is not Parcelable
						final Intent intent = new Intent(this, ForcastTabCreationActivity.class);
						final Bundle extras = new Bundle();
						int i = 0;
						for (final ForcastEntry forcastEntry : entries) {
							intent.putExtra((Constants.FORCAST_ENTRY + i++), forcastEntry);
						}
						intent.putExtra(Constants.NUMBER_OF_FORCASTS, i);
						intent.putExtras(extras);

						startActivity(intent);
					}
				}
				return;
		}
	}

	@Override
	public void onDoubleTap() {
		// Do nothing at present
	}
}
