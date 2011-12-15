package com.morgan.design.android.tasks;

import java.util.List;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import android.location.Location;
import android.os.AsyncTask;

import com.morgan.design.android.domain.WOEIDEntry;
import com.morgan.design.android.util.YahooRequestUtils;

public class DownloadWOIEDDataTaskFromLocation extends AsyncTask<Void, Void, List<WOEIDEntry>> {

	private final Location location;
	private final OnLookupWoeidCallback onLookupWoeidCallback;

	public DownloadWOIEDDataTaskFromLocation(final Location location, final OnLookupWoeidCallback onLookupWoeidCallback) {
		this.location = location;
		this.onLookupWoeidCallback = onLookupWoeidCallback;
	}

	@Override
	protected void onPreExecute() {
		this.onLookupWoeidCallback.onPreLookup();
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
		this.onLookupWoeidCallback.onPostLookup(locations);
	}

	public static abstract class OnLookupWoeidCallback {
		public abstract void onPreLookup();

		public abstract void onPostLookup(final List<WOEIDEntry> locations);
	}
}
