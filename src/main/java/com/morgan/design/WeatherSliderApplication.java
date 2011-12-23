package com.morgan.design;

import java.util.HashMap;
import java.util.Map;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.util.GoogleAnalyticsService;
import com.weatherslider.morgan.design.R;

@ReportsCrashes(formKey = Constants.ANDROID_DOCS_CRASH_REPORT_KEY, mode = ReportingInteractionMode.TOAST, forceCloseDialogAfterToast = false, resToastText = R.string.crash_toast_text)
public class WeatherSliderApplication extends Application {

	private static Map<Integer, YahooWeatherInfo> SERVICE_IDS = new HashMap<Integer, YahooWeatherInfo>();

	private GoogleAnalyticsService googleAnalyticsService;

	@Override
	public void onCreate() {
		ACRA.init(this);
		super.onCreate();
		this.googleAnalyticsService = GoogleAnalyticsService.create(getApplicationContext());
	}

	public GoogleAnalyticsService getGoogleAnalyticsService() {
		return this.googleAnalyticsService;
	}

	public void addWeatherService(final Integer serviceId, final YahooWeatherInfo currentWeather) {
		SERVICE_IDS.put(serviceId, currentWeather);
	}

	public YahooWeatherInfo getWeatherForSerivceId(final int serviceId) {
		return SERVICE_IDS.get(serviceId);
	}

}
