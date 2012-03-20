package com.morgan.design.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.morgan.design.Constants;
import com.morgan.design.Logger;
import com.morgan.design.android.UserPreferencesActivity;
import com.morgan.design.android.domain.types.OverviewMode;
import com.morgan.design.android.domain.types.Temperature;
import com.morgan.design.android.domain.types.WindSpeed;

public final class PreferenceUtils {

	public static final String PREF_ENABLE_GOOGLE_ANALYTICS = "enableGoogleAnalytics";

	public static final String PREF_CHANGELOG = "changelog";
	public static final String PREF_POLLING_SCHEDULE = "pollingSchedule";
	public static final String PREF_START_ON_BOOT = "startOnBoot";
	public static final String PREF_OVERVIEW_MODE = "overviewMode";
	public static final String PREF_TEMPERATURE_MODE = "temperatureMode";
	public static final String PREF_WIND_MODE = "windSpeedMode";
	public static final String PREF_APP_VERSION = "app.version";
	public static final String PREF_RELOAD_ON_CONNECTIVITY_CHANGED = "reloadOnConnectivityChanged";
	public static final String PREF_REFRESH_ON_USER_PRESENT = "refreshOnUserPresent";
	public static final String PREF_ENABLED_NOTIFCATION_TICKER_TEXT = "enableNotificationTickerText";
	public static final String PREF_REPORT_ERROR_ON_FAILED_LOOKUP = "reportErrorOnFailedLookup";

	public static final String PREF_SHOW_UPDATE_DIALOG_ON_NEXT_OPEN = "show.update.dialog.on.next.open";
	public static final String PREF_ENABLE_UPDATE_CHECKER = "enable.update.checker";
	public static final String PREF_LAST_UPDATE_CHECK_TIME = "last.update.check.time";
	public static final String PREF_FIRST_LOOKUP = "first.successful.lookup";
	public static final String PREF_SHOWN_RATE_ME_POPUP = "shown.rateme.popup";
	public static final String PREF_ACRA_SYSTME_LOGS = "acra.syslog.enable";

	public static void openUserPreferenecesActivity(final Activity activity) {
		final Intent intent = new Intent(activity, UserPreferencesActivity.class);
		activity.startActivityForResult(intent, Constants.UPDATED_PREFERENCES);
	}

	private static SharedPreferences getPrefs(final Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	public static boolean getChangelogPref(final Context context) {
		return getPrefs(context).getBoolean(PREF_CHANGELOG, true);
	}

	public static boolean setChangelogPref(final Context context, final boolean value) {
		return getPrefs(context).edit().putBoolean(PREF_CHANGELOG, value).commit();
	}

	public static boolean isGoogleAnalyticsEnabled(final Context context) {
		return getPrefs(context).getBoolean(PREF_ENABLE_GOOGLE_ANALYTICS, true);
	}

	public static boolean setEnableGoogleAalytics(final Context context, final boolean value) {
		return getPrefs(context).edit().putBoolean(PREF_ENABLE_GOOGLE_ANALYTICS, value).commit();
	}

	public static int getPollingSchedule(final Context context) {
		try {
			return Integer.valueOf(getPrefs(context).getString(PREF_POLLING_SCHEDULE, "30"));
		}
		catch (Exception e) {
			return 30;
		}
	}

	public static boolean setPollingSchedule(final Context context, final String minutes) {
		return getPrefs(context).edit().putString(PREF_POLLING_SCHEDULE, minutes).commit();
	}

	public static OverviewMode getOverviewMode(final Context context) {
		return OverviewMode.to(getPrefs(context).getString(PREF_OVERVIEW_MODE, OverviewMode.OVERVIEW.name()));
	}

	public static boolean setOverviewMode(final Context context, final String overviewMode) {
		return getPrefs(context).edit().putString(PREF_OVERVIEW_MODE, overviewMode).commit();
	}

	public static Temperature getTemperatureMode(final Context context) {
		return Temperature.to(getPrefs(context).getString(PREF_TEMPERATURE_MODE, Temperature.CELSIUS.abrev()));
	}

	public static boolean setTemperatureMode(final Context context, final String temperature) {
		return getPrefs(context).edit().putString(PREF_TEMPERATURE_MODE, temperature).commit();
	}

	public static WindSpeed getWindSpeedMode(final Context context) {
		return WindSpeed.fromPref(getPrefs(context).getString(PREF_WIND_MODE, WindSpeed.MPH.toString().toLowerCase()));
	}

	public static boolean setWindSpeedMode(final Context context, final String wind) {
		return getPrefs(context).edit().putString(PREF_WIND_MODE, wind).commit();
	}

	public static boolean shouldStartOnBoot(final Context context) {
		final boolean shouldStartOnBoot = getPrefs(context).getBoolean(PREF_START_ON_BOOT, true);
		Logger.d("PreferenceUtils", "ShouldStartOnBoot [%s]", shouldStartOnBoot);
		return shouldStartOnBoot;
	}

	public static boolean setShouldStartOnBoot(final Context context, final boolean shouldStartOnBoot) {
		return getPrefs(context).edit().putBoolean(PREF_START_ON_BOOT, shouldStartOnBoot).commit();
	}

	public static boolean shouldReloadOnConnectivityChanged(final Context context) {
		final boolean shouldReloadOnConnectivityChanged = getPrefs(context).getBoolean(PREF_RELOAD_ON_CONNECTIVITY_CHANGED, true);
		Logger.d("PreferenceUtils", "shouldReloadOnConnectivityChanged [%s]", shouldReloadOnConnectivityChanged);
		return shouldReloadOnConnectivityChanged;
	}

	public static boolean setShouldReloadOnConnectivityChanged(final Context context, final boolean shouldReloadOnConnectivityChanged) {
		return getPrefs(context).edit().putBoolean(PREF_RELOAD_ON_CONNECTIVITY_CHANGED, shouldReloadOnConnectivityChanged).commit();
	}

	public static boolean setCollectSystemLogsWithArcaReporting(final Context context, final boolean collectLogs) {
		return getPrefs(context).edit().putBoolean(PREF_ACRA_SYSTME_LOGS, collectLogs).commit();
	}

	public static int getAppVersionPref(final Context context) {
		return getPrefs(context).getInt(PREF_APP_VERSION, 0);
	}

	public static boolean setAppVersionPref(final Context context, final int value) {
		return getPrefs(context).edit().putInt(PREF_APP_VERSION, value).commit();
	}

	public static boolean hasSuccessfulWeatherLookup(final Context context) {
		return getPrefs(context).getBoolean(PREF_FIRST_LOOKUP, false);
	}

	public static boolean setHasHadFirstSuccessfulLookup(final Context context, final boolean value) {
		return getPrefs(context).edit().putBoolean(PREF_FIRST_LOOKUP, value).commit();
	}

	public static boolean shownRateMePopup(final Context context) {
		return getPrefs(context).getBoolean(PREF_SHOWN_RATE_ME_POPUP, false);
	}

	public static boolean setShownMeRateMePopup(final Context context) {
		return getPrefs(context).edit().putBoolean(PREF_SHOWN_RATE_ME_POPUP, true).commit();
	}

	public static boolean setRefreshOnUserPresent(final Context context, final boolean value) {
		return getPrefs(context).edit().putBoolean(PREF_REFRESH_ON_USER_PRESENT, value).commit();
	}

	public static boolean shouldRefreshOnUserPresent(Context context) {
		return getPrefs(context).getBoolean(PREF_REFRESH_ON_USER_PRESENT, false);
	}

	public static boolean setEnableNotificationTickerText(final Context context, final boolean value) {
		return getPrefs(context).edit().putBoolean(PREF_ENABLED_NOTIFCATION_TICKER_TEXT, value).commit();
	}

	public static boolean enabledNotifcationTickerText(Context context) {
		return getPrefs(context).getBoolean(PREF_ENABLED_NOTIFCATION_TICKER_TEXT, false);
	}

	public static boolean setReportErrorOnFailedLookup(final Context context, final boolean value) {
		return getPrefs(context).edit().putBoolean(PREF_REPORT_ERROR_ON_FAILED_LOOKUP, value).commit();
	}

	public static boolean reportErrorOnFailedLookup(Context context) {
		return getPrefs(context).getBoolean(PREF_REPORT_ERROR_ON_FAILED_LOOKUP, false);
	}

	public static boolean setEnableDailyUpdateCheck(final Context context, final boolean value) {
		return getPrefs(context).edit().putBoolean(PREF_ENABLE_UPDATE_CHECKER, value).commit();
	}

	public static boolean enableDailyUpdateCheck(Context context) {
		return getPrefs(context).getBoolean(PREF_ENABLE_UPDATE_CHECKER, true);
	}

	public static boolean setShowUpdateDialogOnNextOpen(Context context, boolean value) {
		return getPrefs(context).edit().putBoolean(PREF_SHOW_UPDATE_DIALOG_ON_NEXT_OPEN, value).commit();
	}

	public static boolean shouldShowUpdateDialogOnNextOpen(Context context) {
		return getPrefs(context).getBoolean(PREF_SHOW_UPDATE_DIALOG_ON_NEXT_OPEN, false);
	}

	public static boolean setLastTimCheckedForUpdate(final Context context, final long time) {
		return getPrefs(context).edit().putLong(PREF_LAST_UPDATE_CHECK_TIME, time).commit();
	}

	public static long getLastTimCheckedForUpdate(Context context) {
		return getPrefs(context).getLong(PREF_LAST_UPDATE_CHECK_TIME, 0L);
	}
}
