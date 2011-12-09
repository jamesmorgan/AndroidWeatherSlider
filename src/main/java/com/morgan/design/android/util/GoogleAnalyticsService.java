package com.morgan.design.android.util;

import android.content.Context;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.morgan.design.Constants;

public class GoogleAnalyticsService {

	public static final String WEATHER_OVERVIEW = "WeatherOverviewActivity";
	public static final String GET_LOCATION = "GetLocation";
	public static final String GET_GPS_LOCATION = "GetGpsLocation";
	public static final String ADD_NEW_LOCATION = "AddNewLocation";
	public static final String OPEN_CHANGE_LOG = "OpenChangeLog";
	public static final String OPEN_PREFERENCES = "OpenPreferences";

	private static GoogleAnalyticsService INSTANCE;

	private static GoogleAnalyticsTracker tracker;

	public static synchronized GoogleAnalyticsService create(final Context context) {
		if (null == INSTANCE) {
			INSTANCE = new GoogleAnalyticsService();
		}
		init(context);
		return INSTANCE;
	}

	private static synchronized void init(final Context context) {
		if (null == tracker) {
			tracker = GoogleAnalyticsTracker.getInstance();
		}
		tracker.startNewSession(Constants.GOOGLE_ANALYTICS_KEY, 30, context);
	}

	public void trackClickEvent(final String button) {
		tracker.trackEvent("Clicks", // Category
				"Button", // Action
				button, // Label
				1); // VALUE
	}

	public void trackPageView(final String page) {
		tracker.trackPageView("/" + page);
	}

}
