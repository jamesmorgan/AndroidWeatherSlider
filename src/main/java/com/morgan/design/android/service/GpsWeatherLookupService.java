package com.morgan.design.android.service;

import static com.morgan.design.android.util.ObjectUtils.isNotBlank;
import static com.morgan.design.android.util.ObjectUtils.isNotNull;
import static com.morgan.design.android.util.ObjectUtils.isNull;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;

import com.morgan.design.android.domain.GeocodeResult;
import com.morgan.design.android.tasks.GeocodeWOIEDDataTaskFromLocation;
import com.morgan.design.android.tasks.OnAsyncQueryCallback;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.PreferenceUtils;

public class GpsWeatherLookupService extends AbstractBoundWeatherNotificationService {

	private static final String LOG_TAG = "GpsWeatherLookupService";

	private BroadcastReceiver locationChangedBroadcastReciever;

	private OnAsyncQueryCallback<GeocodeResult> onGeocodeDataCallback;

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
		this.onGeocodeDataCallback = new OnAsyncQueryCallback<GeocodeResult>() {
			@Override
			public void onPostLookup(final GeocodeResult geocodeResult) {
				Logger.d(LOG_TAG, "onPostLookup -> GeocodeResult = %s", geocodeResult);
				startYahooWeatherService(geocodeResult);
			}
		};

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterLocationChangedUpdates();
	}

	private void startYahooWeatherService(final GeocodeResult entry) {
		if (isNotBlank(entry.getWoeid())) {
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
						boolean providersFound = false;
						Location location = null;
						if (intent.hasExtra(LocationLookupService.PROVIDERS_FOUND)) {
							providersFound = extras.getBoolean(LocationLookupService.PROVIDERS_FOUND);
						}
						if (intent.hasExtra(LocationLookupService.CURRENT_LOCAION)) {
							location = (Location) extras.getParcelable(LocationLookupService.CURRENT_LOCAION);

						}

						if (!providersFound) {
							Logger.d(LOG_TAG, "No location providers found, GPS and MOBILE are disabled");
						}
						else if (null != location && providersFound) {
							GpsWeatherLookupService.this.currentLocation = location;
							Logger.d(LOG_TAG, "Listened to location change lat=[%s], long=[%s]", location.getLatitude(),
									location.getLatitude());
							new GeocodeWOIEDDataTaskFromLocation(location, GpsWeatherLookupService.this.onGeocodeDataCallback).execute();
						}
						else {
							Logger.d(LOG_TAG, "GPS location not found");
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
