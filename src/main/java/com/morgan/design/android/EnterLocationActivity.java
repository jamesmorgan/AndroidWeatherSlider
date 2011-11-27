package com.morgan.design.android;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.morgan.design.R;
import com.morgan.design.android.domain.WOEIDEntry;
import com.morgan.design.android.service.YahooRequestUtils;
import com.morgan.design.android.util.Logger;

public class EnterLocationActivity extends Activity {

	private static final String LOG_TAG = "EnterLocationActivity";

	private EditText location;

	private ProgressDialog progressDialog;
	private ArrayList<WOEIDEntry> WOIEDlocations;

	private boolean destroyed = false;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		this.location = (EditText) findViewById(R.id.locationText);
	}

	public void onGetLocation(final View v) {
		final String location = this.location.getText().toString();
		if (null == location || "".equals(location)) {
			Toast.makeText(this, "Please enter a location.", Toast.LENGTH_SHORT);
			return;
		}
		lookupLocation(location);
	}

	public void refreshAvailableLocations(final List<WOEIDEntry> locations) {
		this.WOIEDlocations = new ArrayList<WOEIDEntry>(locations);
		Log.d("HelloAndroidActivity", String.format("Found [%s] WOIED locations", this.WOIEDlocations.size()));

		if (this.WOIEDlocations.isEmpty()) {
			Toast.makeText(this, String.format("Unable to find %s, please try again.", this.location.getText()), Toast.LENGTH_SHORT);
		}
		else {
			final Bundle extras = new Bundle();
			extras.putSerializable("WOIED_LOCAITONS", this.WOIEDlocations);

			final Intent intent = new Intent(this, ListLocationsActivity.class);
			intent.putExtras(extras);
			startActivityForResult(intent, ManageWeatherChoiceActivity.SELECT_LOCATION);
		}
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case ManageWeatherChoiceActivity.SELECT_LOCATION:
				if (resultCode == RESULT_OK) {
					setResult(RESULT_OK);
					finish();
				}
				else if (resultCode == RESULT_CANCELED) {
					Logger.d(LOG_TAG, "SELECT_LOCATION -> RESULT_CANCELED");
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
		finishActivity(ManageWeatherChoiceActivity.ENTER_LOCATION);
		super.finish();
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.destroyed = true;
	}

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
