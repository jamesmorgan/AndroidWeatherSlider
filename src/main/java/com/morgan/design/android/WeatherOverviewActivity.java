package com.morgan.design.android;

import static com.morgan.design.Constants.NOTIFICATION_ID;
import static com.morgan.design.android.util.ObjectUtils.isNotEmpty;
import static com.morgan.design.android.util.ObjectUtils.stringHasValue;
import static com.morgan.design.android.util.ObjectUtils.valueOrDefault;

import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.morgan.design.Constants;
import com.morgan.design.WeatherSliderApplication;
import com.morgan.design.android.SimpleGestureFilter.SimpleGestureListener;
import com.morgan.design.android.analytics.GoogleAnalyticsService;
import com.morgan.design.android.domain.ForcastEntry;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.domain.types.IconFactory;
import com.morgan.design.android.domain.types.Temperature;
import com.morgan.design.android.domain.types.Wind;
import com.morgan.design.android.domain.types.WindSpeed;
import com.morgan.design.android.repository.DatabaseHelper;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.PreferenceUtils;
import com.morgan.design.android.util.PressureUtils;
import com.morgan.design.android.util.Utils;
import com.morgan.design.weatherslider.R;

public class WeatherOverviewActivity extends OrmLiteBaseActivity<DatabaseHelper> implements SimpleGestureListener {

	private static final String LOG_TAG = "WeatherOverviewActivity";

	private YahooWeatherInfo currentWeather;
	private SimpleGestureFilter detector;
	private GoogleAnalyticsService googleAnalyticsService;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_overview);
		final WeatherSliderApplication appState = WeatherSliderApplication.locate(this);

		final Intent intent = getIntent();
		if (null == intent) {
			finish();
		}
		if (!intent.hasExtra(NOTIFICATION_ID)) {
			finish();
		}

		final YahooWeatherInfo weather = appState.getWeather(intent.getIntExtra(NOTIFICATION_ID, 0));
		if (null == weather) {
			finish();
		}
		this.currentWeather = weather;

		this.googleAnalyticsService = appState.getGoogleAnalyticsService();
		this.detector = new SimpleGestureFilter(this, this);
		this.detector.setEnabled(true);

		Logger.d(LOG_TAG, this.currentWeather);

		if (null != this.currentWeather) {
			setDescriptionAndImage();
			setLocationDetails();
			setWindDetails();
			setSunDetails();
			setTemperatureDetails();

			setUpMoreInformationLink();
			setUpOpenMapLink();
		}
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.overview_menu_settings:
				trackPageView(GoogleAnalyticsService.OPEN_PREFERENCES);
				PreferenceUtils.openUserPreferenecesActivity(this);
				return true;
			case R.id.overview_menu_feedback:
				trackPageView(GoogleAnalyticsService.OPEN_FEEDBACK);
				Utils.openFeedback(this);
				return true;
			case R.id.overview_menu_home:
				trackPageView(GoogleAnalyticsService.OPEN_HOME);
				startActivity(new Intent(this, ManageWeatherChoiceActivity.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.overview_menu, menu);
		return true;
	}

	private TextView weather_description;
	private TextView main_temperature;
	private ImageView weather_image;

	private void setDescriptionAndImage() {
		this.weather_description = (TextView) findViewById(R.id.weather_description);
		this.weather_image = (ImageView) findViewById(R.id.weather_image);
		this.main_temperature = (TextView) findViewById(R.id.main_temperature);

		this.weather_description.setText(valueOrDefault(this.currentWeather.getCurrentText(), getString(R.string.not_available)));
		this.main_temperature.setText(this.currentWeather.getCurrentTemp()
			+ Temperature.withDegree(Utils.abrev(this.currentWeather.getTemperatureUnit())));
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

		this.location_1.setText(valueOrDefault(this.currentWeather.getCity(), getString(R.string.not_available)));
		this.location_2.setText(valueOrDefault(location2, getString(R.string.not_available)));
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
			+ Temperature.withDegree(Utils.abrev(this.currentWeather.getTemperatureUnit())));
		this.wind_direction.setText(" (" + Wind.fromDegreeToAbbreviation(this.currentWeather.getWindDirection()) + ")");
	}

	private TextView sun_rise;
	private TextView sun_set;

	private void setSunDetails() {
		this.sun_rise = (TextView) findViewById(R.id.sun_rise);
		this.sun_set = (TextView) findViewById(R.id.sun_set);

		this.sun_rise.setText(valueOrDefault(this.currentWeather.getSunRise(), getString(R.string.not_available)));
		this.sun_set.setText(valueOrDefault(this.currentWeather.getSunSet(), getString(R.string.not_available)));
	}

	private TextView temperature;
	private TextView humidity;
	private TextView pressure;

	private void setTemperatureDetails() {
		this.temperature = (TextView) findViewById(R.id.temperature);
		this.humidity = (TextView) findViewById(R.id.humidity);
		this.pressure = (TextView) findViewById(R.id.pressure);

		this.temperature.setText(this.currentWeather.getCurrentTemp()
			+ Temperature.withDegree(Utils.abrev(this.currentWeather.getTemperatureUnit())));
		this.humidity.setText(this.currentWeather.getHumidityPercentage() + "%");
		this.pressure.setText(valueOrDefault(this.currentWeather.getPressure() + this.currentWeather.getPressureUnit(),
				getString(R.string.not_available)));
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
				trackPageView(GoogleAnalyticsService.MORE_INFO);
			}
		});
		this.more_information_link.setMovementMethod(LinkMovementMethod.getInstance());

		this.last_updated_date_time = (TextView) findViewById(R.id.last_updated_date_time);
		this.last_updated_date_time.setText(this.currentWeather.getCurrentDate());
	}

	private TextView open_map_link;

	private void setUpOpenMapLink() {
		this.open_map_link = (TextView) findViewById(R.id.open_map_link);
		this.open_map_link.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				Utils.openGoogleMaps(WeatherOverviewActivity.this, WeatherOverviewActivity.this.currentWeather.getLatitude(),
						WeatherOverviewActivity.this.currentWeather.getLongitude());
				trackPageView(GoogleAnalyticsService.OPEN_GOOGLE_MAPS);
			}
		});
		this.open_map_link.setMovementMethod(LinkMovementMethod.getInstance());
	}

	public void trackPageView(final String page) {
		this.googleAnalyticsService.trackPageView(this, page);
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
						// TODO => remove if not needed => final Intent intent = new Intent(this, ForcastTabCreationActivity.class);
						final Intent intent = new Intent(this, TwoDayOverviewActivity.class);
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
