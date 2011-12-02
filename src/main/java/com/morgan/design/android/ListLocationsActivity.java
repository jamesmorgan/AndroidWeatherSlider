package com.morgan.design.android;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.morgan.design.android.SimpleGestureFilter.SimpleGestureListener;
import com.morgan.design.android.adaptor.WOIEDAdaptor;
import com.morgan.design.android.domain.WOEIDEntry;
import com.morgan.design.android.service.YahooWeatherLoaderService;
import com.weatherslider.morgan.design.R;

public class ListLocationsActivity extends ListActivity implements SimpleGestureListener {

	private WOIEDAdaptor adaptor;

	private List<WOEIDEntry> WOIEDlocations;
	private SimpleGestureFilter detector;

	@Override
	@SuppressWarnings("unchecked")
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.woied_list);
		this.detector = new SimpleGestureFilter(this, this);
		this.detector.setEnabled(true);

		final Bundle extras = getIntent().getExtras();
		if (null != extras) {
			final Serializable serializable = extras.getSerializable("WOIED_LOCAITONS");
			if (null != serializable) {
				setLocations((ArrayList<WOEIDEntry>) serializable);
			}
		}
	}

	private void setLocations(final List<WOEIDEntry> locations) {
		this.WOIEDlocations = locations;
		this.adaptor = new WOIEDAdaptor(this, this.WOIEDlocations);
		setListAdapter(this.adaptor);
	}

	@Override
	public void onSwipe(final int direction) {
		switch (direction) {
			case SimpleGestureFilter.SWIPE_RIGHT:
				onBackPressed();
				break;
		}
	}

	@Override
	public boolean dispatchTouchEvent(final MotionEvent me) {
		this.detector.onTouchEvent(me);
		return super.dispatchTouchEvent(me);
	}

	@Override
	public void onDoubleTap() {
		// Do nothing at present
	}

	@Override
	protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
		super.onListItemClick(l, v, position, id);
		final WOEIDEntry entry = this.WOIEDlocations.get(position);
		new AlertDialog.Builder(this).setMessage(createConfirmationText(entry))
			.setCancelable(false)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int id) {
					loadWeatherDataForEntry(entry);
				}
			})
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int id) {
					dialog.cancel();
				}
			})
			.create()
			.show();
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		super.finish();
	}

	private String createConfirmationText(final WOEIDEntry entry) {

		final StringBuilder stringBuilder =
				new StringBuilder().append("Is this correct: ")
					.append(" \n")
					.append(entry.getPlaceTypeName())
					.append(": ")
					.append(entry.getName());

		// Either admin 1 or 2
		if ("" != entry.getAdmin1()) {
			stringBuilder.append(" \n").append(entry.getAdmin1());
		}
		else if ("" != entry.getAdmin2()) {
			stringBuilder.append(" \n").append(entry.getAdmin2());
		}

		// Either locality 1 or 2
		if ("" != entry.getLocality1()) {
			stringBuilder.append(" \n").append(entry.getLocality1());
		}
		else if ("" != entry.getLocality2()) {
			stringBuilder.append(" \n").append(entry.getLocality2());
		}

		// Always try country
		if ("" != entry.getCountry()) {
			stringBuilder.append(" \n").append(entry.getCountry());
		}

		return stringBuilder.toString();
	}

	protected void loadWeatherDataForEntry(final WOEIDEntry entry) {
		final Bundle bundle = new Bundle();
		bundle.putSerializable(YahooWeatherLoaderService.CURRENT_WEATHER_WOEID, entry.getWoeid());

		final Intent intent = new Intent(this, YahooWeatherLoaderService.class);
		intent.putExtras(bundle);

		startService(intent);

		setResult(RESULT_OK);
		finish();
	}

	// private YahooWeatherNotifcationService mBoundService;
	// private final ServiceConnection mConnection = new ServiceConnection() {
	// @Override
	// public void onServiceConnected(final ComponentName className, final IBinder service) {
	// ListLocationsActivity.this.mBoundService = ((YahooWeatherNotifcationService.LocalBinder) service).getService();
	// setUpdateWeatherInfoForService();
	// Toast.makeText(ListLocationsActivity.this, "Successfully loaded weather details", Toast.LENGTH_SHORT).show();
	// }
	//
	// @Override
	// public void onServiceDisconnected(final ComponentName className) {
	// ListLocationsActivity.this.mBoundService = null;
	// Toast.makeText(ListLocationsActivity.this, "Disconnected from weather service", Toast.LENGTH_SHORT).show();
	// }
	// };
	//
	// private boolean mIsBound;
	//
	// void doBindService() {
	// bindService(new Intent(ListLocationsActivity.this, YahooWeatherNotifcationService.class), this.mConnection,
	// Context.BIND_AUTO_CREATE);
	// this.mIsBound = true;
	// }
	// protected void setUpdateWeatherInfoForService() {
	// this.mBoundService.setWeatherInformation(this.currentWeather);
	// }
	//
	// void doUnbindService() {
	// if (this.mIsBound) {
	// unbindService(this.mConnection);
	// this.mIsBound = false;
	// }
	// }
	// @Override
	// protected void onDestroy() {
	// super.onDestroy();
	// doUnbindService();
	// }
	// private YahooWeatherInfo currentWeather;
	//
	// public void loadWeatherInfomation(final YahooWeatherInfo weatherInfo) {
	// this.currentWeather = weatherInfo;
	//
	// // Only start/bind if not null
	// if (null != this.currentWeather) {
	//
	// // Bind if not done before
	// if (null == this.mConnection || !this.mIsBound) {
	// doBindService();
	// }
	// // If bind reset options
	// else if (this.mIsBound) {
	// setUpdateWeatherInfoForService();
	// }
	// }
	// }
	// private ProgressDialog progressDialog;
	// private final boolean destroyed = false;
	// public void showLoadingProgress() {
	// this.showProgressDialog("Gathering Weather Information. Please wait...");
	// }
	//
	// public void showProgressDialog(final CharSequence message) {
	// this.progressDialog = ProgressDialog.show(this, "", message, true);
	// }
	//
	// public void dismissLoadingProgress() {
	// if (this.progressDialog != null && !this.destroyed) {
	// this.progressDialog.dismiss();
	// }
	// }
	// private class DownloadWeatherInfoDataTask extends AsyncTask<Void, Void, YahooWeatherInfo> {
	//
	// private final WOEIDEntry entry;
	//
	// public DownloadWeatherInfoDataTask(final WOEIDEntry entry) {
	// this.entry = entry;
	// }
	//
	// @Override
	// protected void onPreExecute() {
	// showLoadingProgress();
	// }
	//
	// @Override
	// protected YahooWeatherInfo doInBackground(final Void... params) {
	// final String url = YahooRequestLoader.createWeatherQuery(this.entry);
	//
	// final RestTemplate restTemplate = new RestTemplate();
	// restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
	//
	// final String weatherResponse = restTemplate.getForObject(url, String.class);
	// return YahooRequestLoader.getWeatherInfo(weatherResponse);
	// }
	//
	// @Override
	// protected void onPostExecute(final YahooWeatherInfo weatherInfo) {
	// dismissLoadingProgress();
	// loadWeatherInfomation(weatherInfo);
	// }
	// }

}
