package com.morgan.design.android;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import com.morgan.design.Constants;
import com.morgan.design.android.domain.OverviewMode;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.PreferenceUtils;
import com.weatherslider.morgan.design.R;

public class UserPreferencesActivity extends PreferenceActivity {

	// FIXME -> preferences
	// TODO -> Option to start on boot, paid version only?
	// TODO -> Option to show up to 3 notifications, paid version only
	// TODO -> On notification click option, open web or open overview dialog with link, possible option can show sample?
	// http://stackoverflow.com/questions/7313852/android-how-can-i-launch-a-popup-dialog-from-an-notification-or-long-pressed-se
	// TODO -> Option to disable adds?
	// TODO -> Option to pick API, e.g. Google/Yahoo
	// TODO -> Show change log on start

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

		if (pref.getKey().equals(PreferenceUtils.PREF_CHANGELOG)) {
			findPreference(PreferenceUtils.PREF_CHANGELOG).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(final Preference arg0, final Object clicked) {
					UserPreferencesActivity.this.hasChanged = true;
					return PreferenceUtils.setChangelogPref(getApplicationContext(), (Boolean) clicked);
				}
			});
		}

		if (pref.getKey().equals(PreferenceUtils.PREF_POLLING_SCHEDULE)) {
			findPreference(PreferenceUtils.PREF_POLLING_SCHEDULE).setOnPreferenceChangeListener(
					new Preference.OnPreferenceChangeListener() {
						@Override
						public boolean onPreferenceChange(final Preference arg0, final Object value) {
							UserPreferencesActivity.this.hasChanged = true;
							return PreferenceUtils.setPollingSchedule(getApplicationContext(), (String) value);
						}
					});
		}

		if (pref.getKey().equals(PreferenceUtils.PREF_START_ON_BOOT)) {
			findPreference(PreferenceUtils.PREF_START_ON_BOOT).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(final Preference arg0, final Object clicked) {
					UserPreferencesActivity.this.hasChanged = true;
					return PreferenceUtils.setShouldStartOnBoot(getApplicationContext(), (Boolean) clicked);
				}
			});
		}

		if (pref.getKey().equals(PreferenceUtils.PREF_OVERVIEW_MODE)) {
			findPreference(PreferenceUtils.PREF_OVERVIEW_MODE).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(final Preference arg0, final Object overview) {
					UserPreferencesActivity.this.hasChanged = true;
					return PreferenceUtils.setOverviewMode(getApplicationContext(), OverviewMode.fromPref((String) overview));
				}
			});
		}

		if (pref.getKey().equals(PreferenceUtils.PREF_TEMPERATURE_MODE)) {
			findPreference(PreferenceUtils.PREF_TEMPERATURE_MODE).setOnPreferenceChangeListener(
					new Preference.OnPreferenceChangeListener() {
						@Override
						public boolean onPreferenceChange(final Preference arg0, final Object temperature) {
							UserPreferencesActivity.this.hasChanged = true;
							return PreferenceUtils.setTemperatureMode(getApplicationContext(), (String) temperature);
						}
					});
		}
		return super.onPreferenceTreeClick(preferenceScreen, pref);
	}
}
