package com.morgan.design;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.util.GoogleAnalyticsService;
import com.weatherslider.morgan.design.R;

@ReportsCrashes(formKey = Constants.ANDROID_DOCS_CRASH_REPORT_KEY, mode = ReportingInteractionMode.TOAST, forceCloseDialogAfterToast = false, resToastText = R.string.crash_toast_text)
public class WeatherSliderApplication extends Application {

	private GoogleAnalyticsService googleAnalyticsService;
	private YahooWeatherInfo currentWeather;
	private String currentWoeid;

	@Override
	public void onCreate() {
		ACRA.init(this);
		super.onCreate();
		this.googleAnalyticsService = GoogleAnalyticsService.create(getApplicationContext());
	}

	public GoogleAnalyticsService getGoogleAnalyticsService() {
		return this.googleAnalyticsService;
	}

	public YahooWeatherInfo getCurrentWeather() {
		return this.currentWeather;
	}

	public void setCurrentWeather(final Integer serviceId, final YahooWeatherInfo currentWeather) {
		// TODO record weather for service
		this.currentWeather = currentWeather;
	}

	public void setCurrentWoeid(final String currentWoeid) {
		this.currentWoeid = currentWoeid;
	}

	public String getCurrentWoied() {
		return this.currentWoeid;
	}

}
