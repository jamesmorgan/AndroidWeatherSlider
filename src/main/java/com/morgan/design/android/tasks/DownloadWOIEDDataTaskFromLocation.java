package com.morgan.design.android.tasks;

import java.util.List;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import android.location.Location;
import android.os.AsyncTask;

import com.morgan.design.android.domain.WOEIDEntry;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.YahooRequestUtils;

public class DownloadWOIEDDataTaskFromLocation extends AsyncTask<Void, Void, List<WOEIDEntry>> {

	private static final String LOG_TAG = "DownloadWOIEDDataTaskFromLocation";

	private final Location location;
	private final OnAsyncQueryCallback<List<WOEIDEntry>> onAsyncQueryCallBack;

	public DownloadWOIEDDataTaskFromLocation(final Location location, final OnAsyncQueryCallback<List<WOEIDEntry>> onAsyncQueryCallBack) {
		this.location = location;
		this.onAsyncQueryCallBack = onAsyncQueryCallBack;
	}

	@Override
	protected void onPreExecute() {
		this.onAsyncQueryCallBack.onPreLookup();
	}

	@Override
	protected List<WOEIDEntry> doInBackground(final Void... params) {
		Logger.d(LOG_TAG, "Looking WOEID fro lat=[%s], long=[%s]", this.location.getLatitude(), this.location.getLongitude());

		final String url = YahooRequestUtils.getInstance().createQuerryGetWoeid(this.location);

		final RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

		final String woiedResponse = restTemplate.getForObject(url, String.class);
		return YahooRequestUtils.getInstance().parseWOIEDResults(woiedResponse);
	}

	@Override
	protected void onPostExecute(final List<WOEIDEntry> locations) {
		this.onAsyncQueryCallBack.onPostLookup(locations);
	}

}
