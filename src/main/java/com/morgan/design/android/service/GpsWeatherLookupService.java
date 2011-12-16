package com.morgan.design.android.service;

import static com.morgan.design.android.util.ObjectUtils.isNotBlank;
import static com.morgan.design.android.util.ObjectUtils.isNotNull;
import static com.morgan.design.android.util.ObjectUtils.isNull;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;

import com.morgan.design.android.domain.WOEIDEntry;
import com.morgan.design.android.tasks.DownloadWOIEDDataTaskFromLocation;
import com.morgan.design.android.tasks.OnAsyncQueryCallback;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.PreferenceUtils;

public class GpsWeatherLookupService extends AbstractBoundWeatherNotificationService {

	private static final String LOG_TAG = "GpsWeatherLookupService";

	private BroadcastReceiver locationChangedBroadcastReciever;
	private OnAsyncQueryCallback<List<WOEIDEntry>> onWoeidDataCallback;

	private Location currentLocation;

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		super.onStartCommand(intent, flags, startId);
		triggerGetGpsLocation();
		return START_STICKY;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		registerForLocationChangedUpdates();
		this.onWoeidDataCallback = new OnAsyncQueryCallback<List<WOEIDEntry>>() {
			@Override
			public void onPostLookup(final List<WOEIDEntry> locations) {
				// deal with locations
				Logger.d(LOG_TAG, "onPostLookup -> Locations = %s", locations);

				final WOEIDEntry entry = new WOEIDEntry();
				entry.setWoeid("12797150");

				startYahooWeatherService(entry);
			}
		};

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterLocationChangedUpdates();
	}

	private void startYahooWeatherService(final WOEIDEntry entry) {
		if (isNotBlank(entry.getWoeid())) {
			this.woeidId = entry.getWoeid();
			downloadWeatherData(this, entry.getWoeid(), PreferenceUtils.getTemperatureMode(getApplicationContext()));
		}
	}

	/*
	 * Actually got get the current location via GPS 
	 */
	private void triggerGetGpsLocation() {
		Logger.d(LOG_TAG, "Triggering get GPS location");
		final Intent findLocationBroadcast = new Intent(LocationLookupService.GET_CURRENT_LOCATION_LOOKUP);
		findLocationBroadcast.putExtra(LocationLookupService.LOCATION_LOOKUP_TIMEOUT, LocationLookupService.DEFAULT_LOCATION_TIMEOUT);
		startService(findLocationBroadcast);
	}

	/*
	 * Ensure listening to return broadcast containing information about current location
	 */
	private void registerForLocationChangedUpdates() {
		if (isNull(this.locationChangedBroadcastReciever)) {
			this.locationChangedBroadcastReciever = new BroadcastReceiver() {

				@Override
				public void onReceive(final Context context, final Intent intent) {

					final Bundle extras = intent.getExtras();

					if (null != extras) {
						if (intent.hasExtra(LocationLookupService.PROVIDERS_FOUND)) {
							final boolean providersFound = extras.getBoolean(LocationLookupService.PROVIDERS_FOUND);
							if (providersFound) {
								Logger.d(LOG_TAG, "No location providers found, GPS and MOBILE are disabled");
							}
							else {
								Logger.d(LOG_TAG, "GPS location not found");
							}
						}
						if (intent.hasExtra(LocationLookupService.CURRENT_LOCAION)) {
							Logger.d(LOG_TAG, "Current location found, looking up WOEID");

							GpsWeatherLookupService.this.currentLocation =
									(Location) extras.getParcelable(LocationLookupService.CURRENT_LOCAION);
							new DownloadWOIEDDataTaskFromLocation(GpsWeatherLookupService.this.currentLocation,
									GpsWeatherLookupService.this.onWoeidDataCallback).execute();
						}
					}
				}
			};
			registerReceiver(this.locationChangedBroadcastReciever, new IntentFilter(LocationLookupService.LOCATION_CHANGED_BROADCAST));
		}
	}

	private void unregisterLocationChangedUpdates() {
		if (isNotNull(this.locationChangedBroadcastReciever)) {
			unregisterReceiver(this.locationChangedBroadcastReciever);
			this.locationChangedBroadcastReciever = null;
		}
	}

}
