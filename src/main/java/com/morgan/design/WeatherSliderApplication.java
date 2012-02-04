package com.morgan.design;

import static com.morgan.design.Constants.LOOPING_ALARM;
import static com.morgan.design.android.util.PreferenceUtils.PREF_ACRA_SYSTME_LOGS;
import static com.morgan.design.android.util.PreferenceUtils.PREF_CHANGELOG;
import static com.morgan.design.android.util.PreferenceUtils.PREF_FIRST_LOOKUP;
import static com.morgan.design.android.util.PreferenceUtils.PREF_OVERVIEW_MODE;
import static com.morgan.design.android.util.PreferenceUtils.PREF_POLLING_SCHEDULE;
import static com.morgan.design.android.util.PreferenceUtils.PREF_REFRESH_ON_USER_PRESENT;
import static com.morgan.design.android.util.PreferenceUtils.PREF_RELOAD_ON_CONNECTIVITY_CHANGED;
import static com.morgan.design.android.util.PreferenceUtils.PREF_SHOWN_RATE_ME_POPUP;
import static com.morgan.design.android.util.PreferenceUtils.PREF_TEMPERATURE_MODE;
import static com.morgan.design.android.util.PreferenceUtils.PREF_WIND_MODE;

import java.util.HashMap;
import java.util.Map;

import org.acra.ACRA;
import org.acra.ErrorReporter;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Intent;

import com.morgan.design.android.analytics.GoogleAnalyticsService;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.service.NotificationControllerService;
import com.morgan.design.android.service.RoamingLookupService;
import com.morgan.design.android.service.StaticLookupService;
import com.morgan.design.android.util.BuildUtils;
import com.morgan.design.weatherslider.R;

//@formatter:off
@ReportsCrashes(
		formKey = Constants.ANDROID_DOCS_CRASH_REPORT_KEY, 
		mode = ReportingInteractionMode.TOAST, 
		forceCloseDialogAfterToast = false, 
		resToastText = R.string.crash_toast_text,
		additionalSharedPreferences={PREF_CHANGELOG, PREF_POLLING_SCHEDULE, PREF_POLLING_SCHEDULE, PREF_OVERVIEW_MODE,
									PREF_TEMPERATURE_MODE,PREF_WIND_MODE, PREF_RELOAD_ON_CONNECTIVITY_CHANGED,
									PREF_REFRESH_ON_USER_PRESENT, PREF_FIRST_LOOKUP, PREF_SHOWN_RATE_ME_POPUP, PREF_ACRA_SYSTME_LOGS})
//@formatter:on
public class WeatherSliderApplication extends Application {

	private GoogleAnalyticsService googleAnalyticsService;

	private static Map<Integer, YahooWeatherInfo> WEATHERS = new HashMap<Integer, YahooWeatherInfo>();

	public static final WeatherSliderApplication locate(Activity activity) {
		return ((WeatherSliderApplication) activity.getApplication());
	}

	public static final WeatherSliderApplication locate(Service service) {
		return ((WeatherSliderApplication) service.getApplication());
	}

	@Override
	public void onCreate() {
		ACRA.init(this);
		super.onCreate();

		if (BuildUtils.isRunningEmmulator()) {
			ErrorReporter.getInstance().disable();
		}

		// SETUP three available notifications
		WEATHERS.put(R.string.weather_notification_service_1, null);
		WEATHERS.put(R.string.weather_notification_service_2, null);
		WEATHERS.put(R.string.weather_notification_service_3, null);

		this.googleAnalyticsService = GoogleAnalyticsService.create(getApplicationContext());
		startService(new Intent(this, NotificationControllerService.class));
		startService(new Intent(this, StaticLookupService.class));
		startService(new Intent(this, RoamingLookupService.class));

		// Start looping alarm
		sendBroadcast(new Intent(LOOPING_ALARM));
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		stopService(new Intent(this, NotificationControllerService.class));
		stopService(new Intent(this, StaticLookupService.class));
		stopService(new Intent(this, RoamingLookupService.class));
	}

	public GoogleAnalyticsService getGoogleAnalyticsService() {
		return this.googleAnalyticsService;
	}

	public Map<Integer, YahooWeatherInfo> getWeathers() {
		return WEATHERS;
	}

	public YahooWeatherInfo getWeather(final int notificationId) {
		return WEATHERS.get(notificationId);
	}

	public void setWeather(final int notifcationId, final YahooWeatherInfo weatherInfo) {
		if (0 != notifcationId) {
			WEATHERS.put(notifcationId, weatherInfo);
		}
	}

	public void clearAll() {
		WEATHERS.put(R.string.weather_notification_service_1, null);
		WEATHERS.put(R.string.weather_notification_service_2, null);
		WEATHERS.put(R.string.weather_notification_service_3, null);
	}

}
