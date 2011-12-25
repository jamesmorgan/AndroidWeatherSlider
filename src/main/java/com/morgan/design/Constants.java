package com.morgan.design;

public class Constants {

	// Yahoo Key
	public static final String YAHOO_API_KEY = "8t08Wo34";

	// Google Analytics
	public static final String GOOGLE_ANALYTICS_KEY = "UA-27624701-1";

	// Application Crash Report Google Docs Keys
	public static final String ANDROID_DOCS_CRASH_REPORT_KEY = "dHJsdXA5Nk5VVXRZdzFlcWY3bWhpWkE6MQ";

	// Email's
	public static final String[] FEEDBACK_EMAIL = new String[] { "appfeedback@morgan-design.com" };

	// Web URL's
	public static final String YAHOO_WEATHER_FORECAST_LINK = "http://weather.yahoo.com/forecast/%s.html";

	// Broadcast receiver actions
	public static final String PREFERENCES_UPDATED = "com.morgan.design.android.broadcast.PREFERENCES_UPDATED";
	public static final String OPEN_WEATHER_OVERVIEW = "com.morgan.design.android.broadcast.OPEN_WEATHER_OVERVIEW";
	public static final String UPDATE_WEATHER_LIST = "com.morgan.design.android.broadcast.UPDATE_WEATHER_LIST";
	public static final String NOTIFICATIONS_FULL = "com.morgan.design.android.broadcast.NOTIFICATIONS_FULL";

	public static final String REMOVE_CURRENT_NOTIFCATION = "com.morgan.design.android.broadcast.REMOVE_CURRENT_NOTIFCATION";
	public static final String DELETE_CURRENT_NOTIFCATION = "com.morgan.design.android.broadcast.DELETE_CURRENT_NOTIFCATION";

	/** Triggered when a request to get the weather is made */
	public static final String RELOAD_WEATHER_BROADCAST = "com.morgan.design.android.broadcast.RELOAD_WEATHER_BROADCAST";

	/** Triggered when the user wants to attempt to force close/cancel all active notifications */
	public static final String CANCEL_ALL_WEATHER_NOTIFICATIONS = "com.morgan.design.android.broadcast.CANCEL_ALL_WEATHER_NOTIFICATIONS";

	// Android inbuilt broadcasts
	public static final String ANDROID_BOOT_COMPLETED_BROADCAST = "android.intent.action.ACTION_BOOT_COMPLETED";
	public static final String ANDROID_CONNECTIVITY_CHANGE_BROADCAST = "android.net.conn.CONNECTIVITY_CHANGE";

	// Activity Result codes
	public static final int ENTER_LOCATION = 1;
	public static final int SELECT_LOCATION = 2;
	public static final int UPDATED_PREFERENCES = 3;

	// Intent Extras
	public static final String WOEID = "WOEID";
	public static final String CURRENT_WEATHER = "CURRENT_WEATHER";
	public static final String WOIED_LOCAITONS = "WOIED_LOCAITONS";

	// Intent extras used when creating overview screens
	public static final String FORCAST_ENTRY = "FORCAST_ENTRY";
	public static final String NUMBER_OF_FORCASTS = "NUMBER_OF_FORCASTS";
	public static final String WEATHER_ID = "WEATHER_ID";

	// Launch Controller Broadcast Intent properties
	public static final String PHONE_BOOT = "PHONE_BOOT";
	public static final String RELOAD_WEATHER = "RELOAD_WEATHER";
	public static final String CONNECTIVITY_CHANGED = "CONNECTIVITY_CHANGED";
	public static final String FROM_INACTIVE_LOCATION = "FROM_INACTIVE_LOCATION";
	public static final String FROM_FRESH_LOOKUP = "FROM_FRESH_LOOKUP";

}
