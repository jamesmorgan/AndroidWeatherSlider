package com.morgan.design.android.util;

import java.util.List;

import android.location.Location;

import com.morgan.design.Constants;
import com.morgan.design.android.domain.GeocodeResult;
import com.morgan.design.android.domain.WOEIDEntry;
import com.morgan.design.android.domain.Woeid;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.domain.types.Temperature;
import com.morgan.design.android.parser.WOIEDParser;
import com.morgan.design.android.parser.YahooGeocodeParser;
import com.morgan.design.android.parser.YahooWeatherInfoParser;

public class YahooRequestUtils {

	private static final String URL_YAHOO_API_WEATHER = "http://weather.yahooapis.com/forecastrss?w=%s&u=%s";

	private static final String GET_LOCATION_WOEID_FOR_TEXT =
			"http://query.yahooapis.com/v1/public/yql?q=select * from geo.places where text='%s' | SORT(field='placeTypeName.code')&format=xml";

	private static final String GET_LOCATION_WOEID_FOR_LOCATION =
			"http://query.yahooapis.com/v1/public/yql?q=select * from geo.places where text='%s, %s' | SORT(field='placeTypeName.code')&format=xml";

	private static final String GEOCODE_WOEID_FOR_LOCATION = "http://where.yahooapis.com/geocode?q=%s,%s&count=1&gflags=R&appid="
		+ Constants.YAHOO_API_KEY;

	// private static final String LOG_TAG = "YahooRequestLoader";

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
		return YahooWeatherInfoParser.getInstance().parse(result);
	}

	public List<WOEIDEntry> parseWOIEDResults(final String results) {
		return WOIEDParser.getInstance().parse(results);
	}

	public GeocodeResult parseGeocodeWOIEDResult(final String result) {
		return YahooGeocodeParser.getInstance().parse(result);
	}

	public String createQuerryGetWoeid(final String strQuerry) {
		if (strQuerry == null) {
			return null;
		}
		return String.format(GET_LOCATION_WOEID_FOR_TEXT, strQuerry);
	}

	public String createQuerryGetWoeid(final Location location) {
		if (location == null) {
			return null;
		}
		return String.format(GET_LOCATION_WOEID_FOR_LOCATION, location.getLatitude(), location.getLongitude());
	}

	public String createGeocodeWoeidQuery(final Location location) {
		if (location == null) {
			return null;
		}
		return String.format(GEOCODE_WOEID_FOR_LOCATION, location.getLatitude(), location.getLongitude());
	}

	public String createWeatherQuery(final Woeid woied, final Temperature temperature) {
		if (woied == null) {
			return null;
		}
		return String.format(URL_YAHOO_API_WEATHER, woied.getWoeid(), temperature.abrev());
	}

}
