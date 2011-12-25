package com.morgan.design;

import org.acra.ACRA;
import org.acra.ErrorReporter;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.Intent;

import com.morgan.design.android.analytics.GoogleAnalyticsService;
import com.morgan.design.android.service.NotificationControllerService;
import com.morgan.design.android.service.RoamingLookupService;
import com.morgan.design.android.service.StaticLookupService;
import com.morgan.design.android.util.BuildUtils;
import com.weatherslider.morgan.design.R;

@ReportsCrashes(formKey = Constants.ANDROID_DOCS_CRASH_REPORT_KEY, mode = ReportingInteractionMode.TOAST, forceCloseDialogAfterToast = false, resToastText = R.string.crash_toast_text)
public class WeatherSliderApplication extends Application {

	private GoogleAnalyticsService googleAnalyticsService;

	@Override
	public void onCreate() {
		ACRA.init(this);
		super.onCreate();

		if (BuildUtils.isRunningEmmulator()) {
			ErrorReporter.getInstance().disable();
		}

		this.googleAnalyticsService = GoogleAnalyticsService.create(getApplicationContext());
		startService(new Intent(this, NotificationControllerService.class));
		startService(new Intent(this, StaticLookupService.class));
		startService(new Intent(this, RoamingLookupService.class));
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		stopService(new Intent(this, NotificationControllerService.class));
	}

	public GoogleAnalyticsService getGoogleAnalyticsService() {
		return this.googleAnalyticsService;
	}

}
