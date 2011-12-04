package com.morgan.design.android.service;

import java.util.List;

import android.location.Location;

import com.morgan.design.android.domain.WOEIDEntry;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.parser.WOIEDParser;
import com.morgan.design.android.parser.YahooWeatherInfoParser;

public class YahooRequestUtils {

	private static final String URL_YAHOO_API_WEATHER = "http://weather.yahooapis.com/forecastrss?w=%s&u=c";

	private static final String GET_LOCATION_WOEID_FOR_TEXT =
			"http://query.yahooapis.com/v1/public/yql?q=select * from geo.places where text='%s' | SORT(field='placeTypeName.code')&format=xml";

	private static final String GET_LOCATION_WOEID_FOR_LOCATION =
			"http://query.yahooapis.com/v1/public/yql?q=select * from geo.places where text='%s, %s' | SORT(field='placeTypeName.code')&format=xml";

	private static final String LOG_TAG = "YahooRequestLoader";

	private YahooRequestUtils() {
		super();
	}

	private static YahooRequestUtils instance;

	public synchronized static YahooRequestUtils getInstance() {
		if (instance == null) {
			instance = new YahooRequestUtils();
		}
		return instance;
	}

	public YahooWeatherInfo getWeatherInfo(final String result) {
		// Logger.i(LOG_TAG, result);
		return YahooWeatherInfoParser.getInstance().parse(result);
	}

	public List<WOEIDEntry> parseWOIEDResults(final String results) {
		// Logger.i(LOG_TAG, results);
		return WOIEDParser.getInstance().parse(results);
	}

	public String createQuerryGetWoeid(final String strQuerry) {
		if (strQuerry == null) {
			return null;
		}
		return String.format(GET_LOCATION_WOEID_FOR_TEXT, strQuerry);
	}

	// http://stackoverflow.com/questions/2502912/using-latitude-longitude-to-get-a-place-with-yahoo
	public String createQuerryGetWoeid(final Location location) {
		if (location == null) {
			return null;
		}
		return String.format(GET_LOCATION_WOEID_FOR_LOCATION, location.getLatitude(), location.getLongitude());
	}

	public String createWeatherQuery(final WOEIDEntry woiedEntry) {
		if (woiedEntry == null) {
			return null;
		}
		return String.format(URL_YAHOO_API_WEATHER, woiedEntry.getWoeid());
	}

	public String createWeatherQuery(final String woeidId) {
		if (woeidId == null) {
			return null;
		}
		return String.format(URL_YAHOO_API_WEATHER, woeidId);
	}
}
