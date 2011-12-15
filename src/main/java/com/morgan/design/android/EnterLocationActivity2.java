package com.morgan.design.android;

import static com.morgan.design.android.util.ObjectUtils.isNot;
import static com.morgan.design.android.util.ObjectUtils.isNotNull;
import static com.morgan.design.android.util.ObjectUtils.isNull;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.morgan.design.Constants;
import com.morgan.design.WeatherSliderApplication;
import com.morgan.design.android.SimpleGestureFilter.SimpleGestureListener;
import com.morgan.design.android.domain.WOEIDEntry;
import com.morgan.design.android.service.LocationLookupService;
import com.morgan.design.android.service.YahooRequestUtils;
import com.morgan.design.android.util.GoogleAnalyticsService;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.Utils;
import com.weatherslider.morgan.design.R;

public class EnterLocationActivity2 extends Activity implements SimpleGestureListener {

	private static final String LOG_TAG = "EnterLocationActivity";

	private EditText location;

	private ProgressDialog progressDialog;
	private ArrayList<WOEIDEntry> WOIEDlocations;

	private boolean destroyed = false;
	private SimpleGestureFilter detector;

	private GoogleAnalyticsService googleAnalyticsService;

	private BroadcastReceiver locationChangedBroadcastReciever;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.detector = new SimpleGestureFilter(this, this);
		this.detector.setEnabled(true);
		this.location = (EditText) findViewById(R.id.locationText);
		this.googleAnalyticsService = getToLevelApplication().getGoogleAnalyticsService();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.destroyed = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		dismissLoadingProgress();
		if (isNotNull(this.locationChangedBroadcastReciever)) {
			unregisterReceiver(this.locationChangedBroadcastReciever);
			this.locationChangedBroadcastReciever = null;
		}
		stopService(new Intent(Constants.LATEST_WEATHER_QUERY_COMPLETE));
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (isNull(this.locationChangedBroadcastReciever)) {
			this.locationChangedBroadcastReciever = new BroadcastReceiver() {
				@Override
				public void onReceive(final Context context, final Intent intent) {
					dismissLoadingProgress();

					final Bundle extras = intent.getExtras();

					if (null != extras) {
						if (intent.hasExtra(LocationLookupService.PROVIDERS_FOUND)) {
							final boolean providersFound = extras.getBoolean(LocationLookupService.PROVIDERS_FOUND);

							if (providersFound) {
								Logger.d(LOG_TAG, "No location providers found, GPS and MOBILE are disabled");
								Toast.makeText(EnterLocationActivity2.this,
										"No location providers found, please check you are have your GPS or mobile network enabled.",
										Toast.LENGTH_SHORT).show();
							}
							else {
								Logger.d(LOG_TAG, "GPS location not found");
								Toast.makeText(EnterLocationActivity2.this,
										"Unable to locate you, please ensure you are connected to the network.", Toast.LENGTH_SHORT).show();
							}
						}

						if (intent.hasExtra(LocationLookupService.CURRENT_LOCAION)) {
							final Location location = (Location) extras.getParcelable(LocationLookupService.CURRENT_LOCAION);
							Logger.d(LOG_TAG, "Listened to location change lat=[%s], long=[%s]", location.getLatitude(),
									location.getLatitude());
							new DownloadWOIEDDataTaskFromLocation(location).execute();
						}
					}
				}
			};
			registerReceiver(this.locationChangedBroadcastReciever, new IntentFilter(LocationLookupService.LOCATION_CHANGED_BROADCAST));
		}
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case Constants.SELECT_LOCATION:
				if (resultCode == RESULT_OK) {
					setResult(RESULT_OK);
					finish();
				}
				else if (resultCode == RESULT_CANCELED) {
					setResult(RESULT_CANCELED);
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		finishActivity(Constants.ENTER_LOCATION);
		super.finish();
	}

	// ///////////////////////////////////////
	// ///////////// Click Handlers //////////
	// ///////////////////////////////////////

	public void onGetLocation(final View v) {
		final String location = this.location.getText().toString();
		if (null == location || "".equals(location)) {
			Toast.makeText(this, "Please enter a location.", Toast.LENGTH_SHORT).show();
			return;
		}
		this.googleAnalyticsService.trackPageView(this, GoogleAnalyticsService.GET_LOCATION);
		new DownloadWOIEDDataTask(location).execute();
	}

	public void onGetCurrentMyLocation(final View v) {
		this.googleAnalyticsService.trackPageView(this, GoogleAnalyticsService.GET_GPS_LOCATION);

		showProgressDialog("Getting Current Location. Please wait...");

		// Ensure network is active/connected in order to use
		final NetworkInfo activeNetwork = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		final boolean connected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
		if (isNot(connected)) {
			dismissLoadingProgress();
			Toast.makeText(this, "Unable to request network location. You are currently not connected.", Toast.LENGTH_SHORT).show();
			return;
		}

		// Ensure have at-least 15% battery power left
		if (15.00 > Utils.getBatteryLevel(getApplicationContext())) {
			dismissLoadingProgress();
			Toast.makeText(this, "Low battery, unable to use location services.", Toast.LENGTH_SHORT).show();
			return;
		}

		// Allow for overriding default timeout
		final Intent findLocationBroadcast = new Intent(LocationLookupService.GET_CURRENT_LOCATION_LOOKUP);
		findLocationBroadcast.putExtra(LocationLookupService.LOCATION_LOOKUP_TIMEOUT, LocationLookupService.DEFAULT_LOCATION_TIMEOUT);
		startService(findLocationBroadcast);
	}

	// //////////////////////////////////////////
	// ///////////// General / Utility //////////
	// //////////////////////////////////////////

	public void refreshAvailableLocations(final List<WOEIDEntry> locations) {
		this.WOIEDlocations = new ArrayList<WOEIDEntry>(locations);
		Log.d("HelloAndroidActivity", String.format("Found [%s] WOIED locations", this.WOIEDlocations.size()));

		if (null == locations || locations.isEmpty()) {
			Toast.makeText(this, String.format("Unable to find %s, please try again.", this.location.getText()), Toast.LENGTH_SHORT).show();
		}
		else {
			final Bundle extras = new Bundle();
			extras.putSerializable(Constants.WOIED_LOCAITONS, this.WOIEDlocations);

			final Intent intent = new Intent(this, ListLocationsActivity.class);
			intent.putExtras(extras);
			startActivityForResult(intent, Constants.SELECT_LOCATION);
		}
	}

	public void showProgressDialog(final CharSequence message) {
		this.progressDialog = ProgressDialog.show(this, "", message, true);
	}

	public void dismissLoadingProgress() {
		if (this.progressDialog != null && !this.destroyed) {
			this.progressDialog.dismiss();
		}
	}

	protected WeatherSliderApplication getToLevelApplication() {
		return ((WeatherSliderApplication) getApplication());
	}

	// /////////////////////////////////////////////
	// ////////// Swipe Gestures ///////////////////
	// /////////////////////////////////////////////

	@Override
	public boolean dispatchTouchEvent(final MotionEvent me) {
		this.detector.onTouchEvent(me);
		return super.dispatchTouchEvent(me);
	}

	@Override
	public void onSwipe(final int direction) {
		switch (direction) {
			case SimpleGestureFilter.SWIPE_RIGHT:
				onBackPressed();
		}
	}

	@Override
	public void onDoubleTap() {
		// Do nothing at present
	}

	// /////////////////////////////////////////
	// ////////// AsyncTasks ///////////////////
	// /////////////////////////////////////////

	private class DownloadWOIEDDataTask extends AsyncTask<Void, Void, List<WOEIDEntry>> {

		private final String location;

		public DownloadWOIEDDataTask(final String location) {
			this.location = location;
		}

		@Override
		protected void onPreExecute() {
			showProgressDialog("Getting Location. Please wait...");
		}

		@Override
		protected List<WOEIDEntry> doInBackground(final Void... params) {
			final String url = YahooRequestUtils.getInstance().createQuerryGetWoeid(this.location);

			final RestTemplate restTemplate = new RestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

			final String woiedResponse = restTemplate.getForObject(url, String.class);
			return YahooRequestUtils.getInstance().parseWOIEDResults(woiedResponse);
		}

		@Override
		protected void onPostExecute(final List<WOEIDEntry> locations) {
			dismissLoadingProgress();
			refreshAvailableLocations(locations);
		}

	}

	private class DownloadWOIEDDataTaskFromLocation extends AsyncTask<Void, Void, List<WOEIDEntry>> {

		private final Location location;

		public DownloadWOIEDDataTaskFromLocation(final Location location) {
			this.location = location;
		}

		@Override
		protected void onPreExecute() {
			showProgressDialog("Location found, looking up area. Please wait...");
		}

		@Override
		protected List<WOEIDEntry> doInBackground(final Void... params) {
			final String url = YahooRequestUtils.getInstance().createQuerryGetWoeid(this.location);

			final RestTemplate restTemplate = new RestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

			final String woiedResponse = restTemplate.getForObject(url, String.class);
			return YahooRequestUtils.getInstance().parseWOIEDResults(woiedResponse);
		}

		@Override
		protected void onPostExecute(final List<WOEIDEntry> locations) {
			dismissLoadingProgress();
			refreshAvailableLocations(locations);
		}

	}

}
