package com.morgan.design.android;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import com.morgan.design.Constants;
import com.morgan.design.android.domain.types.OverviewMode;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.PreferenceUtils;
import com.morgan.design.android.util.Utils;
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
		Utils.addPreferencesToArcaReport(this);
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

		if (pref.getKey()
			.equals(PreferenceUtils.PREF_CHANGELOG)) {
			findPreference(PreferenceUtils.PREF_CHANGELOG).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(final Preference arg0, final Object clicked) {
					return PreferenceUtils.setChangelogPref(getApplicationContext(), (Boolean) clicked);
				}
			});
		}

		if (pref.getKey()
			.equals(PreferenceUtils.PREF_POLLING_SCHEDULE)) {
			findPreference(PreferenceUtils.PREF_POLLING_SCHEDULE).setOnPreferenceChangeListener(
					new Preference.OnPreferenceChangeListener() {
						@Override
						public boolean onPreferenceChange(final Preference arg0, final Object value) {
							UserPreferencesActivity.this.hasChanged = true;
							return PreferenceUtils.setPollingSchedule(getApplicationContext(), (String) value);
						}
					});
		}

		if (pref.getKey()
			.equals(PreferenceUtils.PREF_START_ON_BOOT)) {
			findPreference(PreferenceUtils.PREF_START_ON_BOOT).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(final Preference arg0, final Object clicked) {
					return PreferenceUtils.setShouldStartOnBoot(getApplicationContext(), (Boolean) clicked);
				}
			});
		}

		if (pref.getKey()
			.equals(PreferenceUtils.PREF_ENABLE_GOOGLE_ANALYTICS)) {
			findPreference(PreferenceUtils.PREF_ENABLE_GOOGLE_ANALYTICS).setOnPreferenceChangeListener(
					new Preference.OnPreferenceChangeListener() {
						@Override
						public boolean onPreferenceChange(final Preference arg0, final Object clicked) {
							return PreferenceUtils.setEnableGoogleAalytics(getApplicationContext(), (Boolean) clicked);
						}
					});
		}

		if (pref.getKey()
			.equals(PreferenceUtils.PREF_OVERVIEW_MODE)) {
			findPreference(PreferenceUtils.PREF_OVERVIEW_MODE).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(final Preference arg0, final Object overview) {
					UserPreferencesActivity.this.hasChanged = true;
					return PreferenceUtils.setOverviewMode(getApplicationContext(), OverviewMode.fromPref((String) overview));
				}
			});
		}

		if (pref.getKey()
			.equals(PreferenceUtils.PREF_TEMPERATURE_MODE)) {
			findPreference(PreferenceUtils.PREF_TEMPERATURE_MODE).setOnPreferenceChangeListener(
					new Preference.OnPreferenceChangeListener() {
						@Override
						public boolean onPreferenceChange(final Preference arg0, final Object temperature) {
							UserPreferencesActivity.this.hasChanged = true;
							return PreferenceUtils.setTemperatureMode(getApplicationContext(), (String) temperature);
						}
					});
		}

		if (pref.getKey()
			.equals(PreferenceUtils.PREF_WIND_MODE)) {
			findPreference(PreferenceUtils.PREF_WIND_MODE).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(final Preference arg0, final Object speed) {
					UserPreferencesActivity.this.hasChanged = true;
					return PreferenceUtils.setWindSpeedMode(getApplicationContext(), (String) speed);
				}
			});
		}

		if (pref.getKey()
			.equals(PreferenceUtils.PREF_RELOAD_ON_CONNECTIVITY_CHANGED)) {
			findPreference(PreferenceUtils.PREF_RELOAD_ON_CONNECTIVITY_CHANGED).setOnPreferenceChangeListener(
					new Preference.OnPreferenceChangeListener() {
						@Override
						public boolean onPreferenceChange(final Preference arg0, final Object clicked) {
							return PreferenceUtils.setShouldReloadOnConnectivityChanged(getApplicationContext(), (Boolean) clicked);
						}
					});
		}

		if (pref.getKey()
			.equals(PreferenceUtils.PREF_ACRA_SYSTME_LOGS)) {
			findPreference(PreferenceUtils.PREF_ACRA_SYSTME_LOGS).setOnPreferenceChangeListener(
					new Preference.OnPreferenceChangeListener() {
						@Override
						public boolean onPreferenceChange(final Preference arg0, final Object clicked) {
							return PreferenceUtils.setCollectSystemLogsWithArcaReporting(getApplicationContext(), (Boolean) clicked);
						}
					});
		}

		return super.onPreferenceTreeClick(preferenceScreen, pref);
	}
}
