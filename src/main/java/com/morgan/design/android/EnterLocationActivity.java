package com.morgan.design.android;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.morgan.design.Constants;
import com.morgan.design.android.SimpleGestureFilter.SimpleGestureListener;
import com.morgan.design.android.domain.WOEIDEntry;
import com.morgan.design.android.service.GetMyLocationService;
import com.morgan.design.android.service.GetMyLocationService.LocationResult;
import com.morgan.design.android.service.YahooRequestUtils;
import com.morgan.design.android.util.Logger;
import com.weatherslider.morgan.design.R;

public class EnterLocationActivity extends Activity implements SimpleGestureListener {

	private static final String LOG_TAG = "EnterLocationActivity";

	private EditText location;

	private ProgressDialog progressDialog;
	private ArrayList<WOEIDEntry> WOIEDlocations;

	private boolean destroyed = false;
	private SimpleGestureFilter detector;

	private GetMyLocationService mBoundMyLocationService;
	private boolean mIsLocationServiceBound = false;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.detector = new SimpleGestureFilter(this, this);
		this.detector.setEnabled(true);
		this.location = (EditText) findViewById(R.id.locationText);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.destroyed = true;
		doUnbindService();
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
			Toast.makeText(this, "Please enter a location.", Toast.LENGTH_SHORT);
			return;
		}
		lookupLocation(location);
	}

	public void onGetCurrentMyLocation(final View v) {
		if (null == this.mBoundMyLocationService || !this.mIsLocationServiceBound) {
			doBindService();
		}
		showLoadingProgress();
	}

	// //////////////////////////////////////////
	// ///////////// General / Utility //////////
	// //////////////////////////////////////////

	public void refreshAvailableLocations(final List<WOEIDEntry> locations) {
		this.WOIEDlocations = new ArrayList<WOEIDEntry>(locations);
		Log.d("HelloAndroidActivity", String.format("Found [%s] WOIED locations", this.WOIEDlocations.size()));

		if (null == locations || locations.isEmpty()) {
			Toast.makeText(this, String.format("Unable to find %s, please try again.", this.location.getText()), Toast.LENGTH_SHORT);
		}
		else {
			final Bundle extras = new Bundle();
			extras.putSerializable(Constants.WOIED_LOCAITONS, this.WOIEDlocations);

			final Intent intent = new Intent(this, ListLocationsActivity.class);
			intent.putExtras(extras);
			startActivityForResult(intent, Constants.SELECT_LOCATION);
		}
	}

	protected void lookupLocation(final String location) {
		new DownloadWOIEDDataTask(location).execute();
	}

	public void showLoadingProgress() {
		this.showProgressDialog("Getting Location. Please wait...");
	}

	public void showProgressDialog(final CharSequence message) {
		this.progressDialog = ProgressDialog.show(this, "", message, true);
	}

	public void dismissLoadingProgress() {
		if (this.progressDialog != null && !this.destroyed) {
			this.progressDialog.dismiss();
		}
	}

	// /////////////////////////////////////////////
	// ////////// Service Binder ///////////////////
	// /////////////////////////////////////////////

	private final ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(final ComponentName className, final IBinder service) {
			EnterLocationActivity.this.mBoundMyLocationService = ((GetMyLocationService.LocalBinder) service).getService();
			EnterLocationActivity.this.mBoundMyLocationService.getLocation(EnterLocationActivity.this,
					EnterLocationActivity.this.locationResult);
			Logger.d(LOG_TAG, "Successfully loaded gps service");
		}

		@Override
		public void onServiceDisconnected(final ComponentName className) {
			EnterLocationActivity.this.mBoundMyLocationService = null;
			new AlertDialog.Builder(EnterLocationActivity.this).setMessage("Unable to locate your location.").create().show();
			dismissLoadingProgress();
			Logger.d(LOG_TAG, "Disconnected from gps service");
		}
	};

	public LocationResult locationResult = new LocationResult() {
		@Override
		public void onLocationChanged(final Location location) {
			if (null != location) {
				handleLocationChanged(location);
			}
			else {
				new AlertDialog.Builder(EnterLocationActivity.this).setMessage("Unable to locate your location.").create().show();
			}
			dismissLoadingProgress();
		}
	};

	protected void handleLocationChanged(final Location location) {
		Logger.d(LOG_TAG, "Listened to location change lat=[%s], long=[%s]", location.getLatitude(), location.getLatitude());
		// TODO handle location change event
	}

	void doBindService() {
		bindService(new Intent(this, GetMyLocationService.class), this.mConnection, Context.BIND_AUTO_CREATE);
		this.mIsLocationServiceBound = true;
	}

	void doUnbindService() {
		if (this.mIsLocationServiceBound) {
			unbindService(this.mConnection);
			this.mIsLocationServiceBound = false;
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
			showLoadingProgress();
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
