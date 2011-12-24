package com.morgan.design.android.tasks;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import android.location.Location;
import android.os.AsyncTask;

import com.morgan.design.android.domain.GeocodeResult;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.YahooRequestUtils;

public class GeocodeWOIEDDataTaskFromLocation extends AsyncTask<Void, Void, GeocodeResult> {

	private static final String LOG_TAG = "GeocodeWOIEDDataTaskFromLocation";

	private final Location location;
	private final OnAsyncCallback<GeocodeResult> onAsyncQueryCallBack;

	public GeocodeWOIEDDataTaskFromLocation(final Location location, final OnAsyncCallback<GeocodeResult> onAsyncQueryCallBack) {
		this.location = location;
		this.onAsyncQueryCallBack = onAsyncQueryCallBack;
	}

	@Override
	protected void onPreExecute() {
		this.onAsyncQueryCallBack.onPreLookup();
	}

	@Override
	protected GeocodeResult doInBackground(final Void... params) {
		this.onAsyncQueryCallBack.onInitiateExecution();
		Logger.d(LOG_TAG, "Looking WOEID fro lat=[%s], long=[%s]", this.location.getLatitude(), this.location.getLongitude());

		final String url = YahooRequestUtils.getInstance().createGeocodeWoeidQuery(this.location);

		final RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

		final String woiedResponse = restTemplate.getForObject(url, String.class);
		return YahooRequestUtils.getInstance().parseGeocodeWOIEDResult(woiedResponse);
	}

	@Override
	protected void onPostExecute(final GeocodeResult location) {
		this.onAsyncQueryCallBack.onPostLookup(location);
	}

}
