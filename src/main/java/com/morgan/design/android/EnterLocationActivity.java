package com.morgan.design.android;

import static com.morgan.design.Constants.FROM_FRESH_LOOKUP;
import static com.morgan.design.android.util.ObjectUtils.isNot;
import static com.morgan.design.android.util.ObjectUtils.isNotNull;
import static com.morgan.design.android.util.ObjectUtils.isNull;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.morgan.design.Changelog;
import com.morgan.design.Constants;
import com.morgan.design.WeatherSliderApplication;
import com.morgan.design.android.SimpleGestureFilter.SimpleGestureListener;
import com.morgan.design.android.analytics.GoogleAnalyticsService;
import com.morgan.design.android.domain.WOEIDEntry;
import com.morgan.design.android.service.LocationLookupService;
import com.morgan.design.android.service.RoamingLookupService;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.RestTemplateFactory;
import com.morgan.design.android.util.Utils;
import com.morgan.design.android.util.YahooRequestUtils;
import com.morgan.design.weatherslider.R;

public class EnterLocationActivity extends Activity implements SimpleGestureListener {

	private static final String LOG_TAG = "EnterLocationActivity";

	private CheckBox useGps;
	private EditText location;
	private Button getMyLocationButton;
	private Button lookUpLocationButton;
	private Button alwaysUseGpsButton;

	private ProgressDialog progressDialog;
	private ArrayList<WOEIDEntry> WOIEDlocations;

	private boolean destroyed = false;
	private SimpleGestureFilter detector;

	private GoogleAnalyticsService googleAnalyticsService;

	private BroadcastReceiver locationChangedBroadcastReciever;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enter_location);
		Changelog.show(this, false);

		this.detector = new SimpleGestureFilter(this, this);
		this.detector.setEnabled(true);

		this.location = (EditText) findViewById(R.id.locationText);
		this.useGps = (CheckBox) findViewById(R.id.use_gps);
		this.lookUpLocationButton = (Button) findViewById(R.id.lookUpLocationButton);
		this.getMyLocationButton = (Button) findViewById(R.id.getMyLocationButton);
		this.alwaysUseGpsButton = (Button) findViewById(R.id.alwaysUseGpsButton);

		this.googleAnalyticsService = getToLevelApplication().getGoogleAnalyticsService();

		this.location.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					onGetLocation(v);
					return true;
				}
				return false;
			}
		});
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
							createGpsDisabledAlert();
						}
						else if (null != location && providersFound) {
							Logger.d(LOG_TAG, "Listened to location change lat=[%s], long=[%s]", location.getLatitude(),
									location.getLatitude());
							new DownloadWOIEDDataTaskFromLocation(location).execute();
						}
						else {
							Logger.d(LOG_TAG, "GPS location not found");
							Toast.makeText(EnterLocationActivity.this, R.string.toast_unable_to_locate_you, Toast.LENGTH_SHORT)
								.show();
						}
					}
				}
			};
			registerReceiver(this.locationChangedBroadcastReciever,
					new IntentFilter(LocationLookupService.ONE_OFF_LOCATION_FOUND_BROADCAST));
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
		final String location = this.location.getText()
			.toString();
		if (null == location || "".equals(location)) {
			Toast.makeText(this, R.string.toast_please_enter_a_location, Toast.LENGTH_SHORT)
				.show();
			return;
		}
		this.googleAnalyticsService.trackPageView(this, GoogleAnalyticsService.GET_LOCATION);
		new DownloadWOIEDDataTask(location).execute();
	}

	public void onGetCurrentMyLocation(final View v) {
		Logger.d(LOG_TAG, "Getting current location");

		this.googleAnalyticsService.trackPageView(this, GoogleAnalyticsService.GET_GPS_LOCATION);

		showProgressDialog(getString(R.string.service_update_getting_current_location));

		// Ensure network is active/connected in order to use
		final boolean networkConnected = Utils.isConnectedOrConnecting(getApplicationContext());
		final boolean gpsEnabled = Utils.isGpsEnabled(getContentResolver());
		if (isNot(networkConnected) && isNot(gpsEnabled)) {
			dismissLoadingProgress();
			Toast.makeText(this, R.string.toast_unable_to_request_network_location, Toast.LENGTH_SHORT)
				.show();
			createGpsDisabledAlert();
			return;
		}

		// Ensure have at-least 15% battery power left
		if (15.00 > Utils.getBatteryLevel(getApplicationContext())) {
			dismissLoadingProgress();
			Toast.makeText(this, R.string.toast_low_battery_warning, Toast.LENGTH_SHORT)
				.show();
			return;
		}

		// Allow for overriding default timeout
		final Intent findLocationBroadcast = new Intent(LocationLookupService.GET_ONE_OFF_CURRENT_LOCATION);
		findLocationBroadcast.putExtra(LocationLookupService.LOCATION_LOOKUP_TIMEOUT, LocationLookupService.DEFAULT_LOCATION_TIMEOUT);
		startService(findLocationBroadcast);
	}

	public void onEnabledGpsLocation(final View view) {
		final boolean isChecked = this.useGps.isChecked();

		this.location.setEnabled(!isChecked);
		this.location.setClickable(!isChecked);
		this.location.setFocusable(!isChecked);
		this.location.setFocusableInTouchMode(!isChecked);
		this.getMyLocationButton.setEnabled(!isChecked);
		this.getMyLocationButton.setClickable(!isChecked);
		this.lookUpLocationButton.setEnabled(!isChecked);
		this.lookUpLocationButton.setClickable(!isChecked);

		this.alwaysUseGpsButton.setEnabled(isChecked);
		this.alwaysUseGpsButton.setClickable(isChecked);
	}

	public void onAlwaysUseGpsForLocation(final View view) {
		Logger.d(LOG_TAG, "Ticked always use GPS, launching GpsWeatherLookupService");
		this.googleAnalyticsService.trackPageView(this, GoogleAnalyticsService.ALWAYS_USE_GPS_LOCATION);

		Toast.makeText(this, R.string.toast_attempting_to_lookup_your_gps_location, Toast.LENGTH_SHORT)
			.show();

		startService(new Intent(this, RoamingLookupService.class).putExtra(FROM_FRESH_LOOKUP, true));
		setResult(RESULT_OK);
		finish();
	}

	// //////////////////////////////////////////
	// ///////////// General / Utility //////////
	// //////////////////////////////////////////

	public void refreshAvailableLocations(final List<WOEIDEntry> locations) {
		this.WOIEDlocations = new ArrayList<WOEIDEntry>(null == locations
				? new ArrayList<WOEIDEntry>()
				: locations);
		Logger.d(LOG_TAG, String.format("Found [%s] WOIED locations", this.WOIEDlocations.size()));

		if (null == locations || locations.isEmpty()) {
			Toast.makeText(this, String.format(getString(R.string.toast_error_unable_to_find_you), this.location.getText()),
					Toast.LENGTH_SHORT)
				.show();
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
		this.progressDialog = ProgressDialog.show(this, "", message, true, true, new OnCancelListener() {
			@Override
			public void onCancel(final DialogInterface dialog) {
				stopService(new Intent(LocationLookupService.GET_ONE_OFF_CURRENT_LOCATION));
			}
		});
	}

	public void dismissLoadingProgress() {
		if (this.progressDialog != null && !this.destroyed) {
			this.progressDialog.dismiss();
		}
	}

	private void createGpsDisabledAlert() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.alaert_enabled_gps_provider)
			.setCancelable(false)
			.setPositiveButton(R.string.alert_enable_gps, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int id) {
					showGpsOptions();
				}
			});
		builder.setNegativeButton(R.string.alert_do_nothing, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	private void showGpsOptions() {
		final Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(gpsOptionsIntent);
	}

	protected WeatherSliderApplication getToLevelApplication() {
		return ((WeatherSliderApplication) getApplication());
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
			showProgressDialog(getString(R.string.service_update_getting_your_location));
		}

		@Override
		protected List<WOEIDEntry> doInBackground(final Void... params) {
			final String url = YahooRequestUtils.getInstance()
				.createQuerryGetWoeid(this.location);

			return YahooRequestUtils.getInstance()
				.parseWOIEDResults(RestTemplateFactory.createAndQuery(url));
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
			showProgressDialog(getString(R.string.service_update_location_found_looking_up_area));
		}

		@Override
		protected List<WOEIDEntry> doInBackground(final Void... params) {
			final String url = YahooRequestUtils.getInstance()
				.createQuerryGetWoeid(this.location);

			return YahooRequestUtils.getInstance()
				.parseWOIEDResults(RestTemplateFactory.createAndQuery(url));
		}

		@Override
		protected void onPostExecute(final List<WOEIDEntry> locations) {
			dismissLoadingProgress();
			refreshAvailableLocations(locations);
		}

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
}
