package com.morgan.design.android;

import static com.morgan.design.android.util.PreferenceUtils.PREF_ACRA_SYSTME_LOGS;
import static com.morgan.design.android.util.PreferenceUtils.PREF_CHANGELOG;
import static com.morgan.design.android.util.PreferenceUtils.PREF_ENABLED_NOTIFCATION_TICKER_TEXT;
import static com.morgan.design.android.util.PreferenceUtils.PREF_ENABLE_GOOGLE_ANALYTICS;
import static com.morgan.design.android.util.PreferenceUtils.PREF_OVERVIEW_MODE;
import static com.morgan.design.android.util.PreferenceUtils.PREF_POLLING_SCHEDULE;
import static com.morgan.design.android.util.PreferenceUtils.PREF_REFRESH_ON_USER_PRESENT;
import static com.morgan.design.android.util.PreferenceUtils.PREF_RELOAD_ON_CONNECTIVITY_CHANGED;
import static com.morgan.design.android.util.PreferenceUtils.PREF_REPORT_ERROR_ON_FAILED_LOOKUP;
import static com.morgan.design.android.util.PreferenceUtils.PREF_START_ON_BOOT;
import static com.morgan.design.android.util.PreferenceUtils.PREF_TEMPERATURE_MODE;
import static com.morgan.design.android.util.PreferenceUtils.PREF_WIND_MODE;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import com.morgan.design.Constants;
import com.morgan.design.android.domain.types.OverviewMode;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.PreferenceUtils;
import com.morgan.design.weatherslider.R;

public class UserPreferencesActivity extends PreferenceActivity {

	private static final String LOG_TAG = "UserPreferencesActivity";

	protected boolean hasChanged = false;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	public void onBackPressed() {
		if (this.hasChanged) {
			setResult(RESULT_OK);
		}
		else {
			setResult(RESULT_CANCELED);
		}
		finishActivity(Constants.UPDATED_PREFERENCES);
		super.onBackPressed();
	}

	@Override
	public boolean onPreferenceTreeClick(final PreferenceScreen preferenceScreen, final Preference pref) {
		Logger.d(LOG_TAG, "Finding preferences : key=[" + pref.getKey() + "]");

		if (pref.getKey().equals(PREF_CHANGELOG)) {
			findPreference(PREF_CHANGELOG).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(final Preference arg0, final Object clicked) {
					return PreferenceUtils.setChangelogPref(getApplicationContext(), (Boolean) clicked);
				}
			});
		}

		if (pref.getKey().equals(PREF_POLLING_SCHEDULE)) {
			findPreference(PREF_POLLING_SCHEDULE).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(final Preference arg0, final Object value) {
					hasChanged = true;
					return PreferenceUtils.setPollingSchedule(getApplicationContext(), (String) value);
				}
			});
		}

		if (pref.getKey().equals(PREF_START_ON_BOOT)) {
			findPreference(PREF_START_ON_BOOT).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(final Preference arg0, final Object clicked) {
					return PreferenceUtils.setShouldStartOnBoot(getApplicationContext(), (Boolean) clicked);
				}
			});
		}

		if (pref.getKey().equals(PREF_ENABLE_GOOGLE_ANALYTICS)) {
			findPreference(PREF_ENABLE_GOOGLE_ANALYTICS).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(final Preference arg0, final Object clicked) {
					return PreferenceUtils.setEnableGoogleAalytics(getApplicationContext(), (Boolean) clicked);
				}
			});
		}

		if (pref.getKey().equals(PREF_OVERVIEW_MODE)) {
			findPreference(PREF_OVERVIEW_MODE).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(final Preference arg0, final Object overview) {
					hasChanged = true;
					return PreferenceUtils.setOverviewMode(getApplicationContext(), OverviewMode.fromPref((String) overview));
				}
			});
		}

		if (pref.getKey().equals(PREF_TEMPERATURE_MODE)) {
			findPreference(PREF_TEMPERATURE_MODE).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(final Preference arg0, final Object temperature) {
					hasChanged = true;
					return PreferenceUtils.setTemperatureMode(getApplicationContext(), (String) temperature);
				}
			});
		}

		if (pref.getKey().equals(PREF_WIND_MODE)) {
			findPreference(PREF_WIND_MODE).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(final Preference arg0, final Object speed) {
					hasChanged = true;
					return PreferenceUtils.setWindSpeedMode(getApplicationContext(), (String) speed);
				}
			});
		}

		if (pref.getKey().equals(PREF_RELOAD_ON_CONNECTIVITY_CHANGED)) {
			findPreference(PREF_RELOAD_ON_CONNECTIVITY_CHANGED).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(final Preference arg0, final Object clicked) {
					return PreferenceUtils.setShouldReloadOnConnectivityChanged(getApplicationContext(), (Boolean) clicked);
				}
			});
		}

		if (pref.getKey().equals(PREF_ACRA_SYSTME_LOGS)) {
			findPreference(PREF_ACRA_SYSTME_LOGS).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(final Preference arg0, final Object clicked) {
					return PreferenceUtils.setCollectSystemLogsWithArcaReporting(getApplicationContext(), (Boolean) clicked);
				}
			});
		}

		if (pref.getKey().equals(PREF_REFRESH_ON_USER_PRESENT)) {
			findPreference(PREF_REFRESH_ON_USER_PRESENT).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(final Preference arg0, final Object clicked) {
					return PreferenceUtils.setRefreshOnUserPresent(getApplicationContext(), (Boolean) clicked);
				}
			});
		}

		if (pref.getKey().equals(PREF_ENABLED_NOTIFCATION_TICKER_TEXT)) {
			findPreference(PREF_ENABLED_NOTIFCATION_TICKER_TEXT).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(final Preference arg0, final Object clicked) {
					return PreferenceUtils.setEnableNotificationTickerText(getApplicationContext(), (Boolean) clicked);
				}
			});
		}

		if (pref.getKey().equals(PREF_REPORT_ERROR_ON_FAILED_LOOKUP)) {
			findPreference(PREF_REPORT_ERROR_ON_FAILED_LOOKUP).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(final Preference arg0, final Object clicked) {
					return PreferenceUtils.setReportErrorOnFailedLookup(getApplicationContext(), (Boolean) clicked);
				}
			});
		}

		return super.onPreferenceTreeClick(preferenceScreen, pref);
	}
}
