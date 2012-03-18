package com.morgan.design;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.morgan.design.android.adaptor.SeparatedListAdapter;
import com.morgan.design.android.analytics.GoogleAnalyticsService;
import com.morgan.design.android.util.BuildUtils;
import com.morgan.design.android.util.Utils;
import com.morgan.design.weatherslider.R;

public class AboutActivity extends Activity {

	private final static String ITEM_TITLE = "title";
	private final static String ITEM_VERSION = "version";
	private final static String ITEM_CAPTION = "caption";
	private final static String SUB_CAPTION = "sub_caption";
	private final static String URL = "complex_url";
	private final static String ITEM_IMAGE = "image";

	private SeparatedListAdapter adapter;
	private ListView list;

	private GoogleAnalyticsService googleAnalyticsService;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.googleAnalyticsService = WeatherSliderApplication.locate(this).getGoogleAnalyticsService();

		// create our list and custom adapter
		this.adapter = new SeparatedListAdapter(this, R.layout.list_header);
		this.list = new ListView(this);

		// ///////////
		// Credits //
		// ///////////
		//@formatter:off
		
		final List<Map<String, ?>> credits = new LinkedList<Map<String, ?>>();
		credits.add(createImageItem("Twitter", "Follow me", Constants.TWITTER_URL, Constants.TWITTER_URL, R.drawable.twitter_logo));
		credits.add(createImageItem("Created By", "James Morgan", Constants.MORGAN_DESIGN, Constants.MORGAN_DESIGN, R.drawable.morgan_design_icon));
		credits.add(createImageItem("Rate WeatherSlider", getResources().getString(R.string.app_name), Constants.ANDROID_MARKET, Constants.MARKET_LINK_URL, R.drawable.rate_me_icon));

		this.adapter.addSection("Credits", new SimpleAdapter(this, credits, R.layout.list_complex_sub_with_image, 
				new String[] {ITEM_TITLE, ITEM_CAPTION, SUB_CAPTION, URL, ITEM_IMAGE }, 
				new int[] { R.id.list_complex_title, R.id.list_complex_caption, R.id.list_complex_sub_caption, R.id.list_complex_url, R.id.list_complex_image }));
		
		// //////////
		// Weather //
		// //////////
		
		final List<Map<String, ?>> weather = new LinkedList<Map<String, ?>>();
		weather.add(createImageItem("Yahoo! Weather", "Weather provided by Yahoo! Weather", Constants.YAHOO_WEATHER_URL, Constants.YAHOO_WEATHER_URL, R.drawable.yahoo_weather_logo));
		weather.add(createImageItem("ARCA", "Application Crash Report for Android", Constants.ARCA_URL, Constants.ARCA_URL, R.drawable.arca_logo));
		weather.add(createImageItem("ORMLite", "ORMLite persistance", Constants.ORMLITE_URL, Constants.ORMLITE_URL, R.drawable.ormlite_256_logo));
		
		this.adapter.addSection("API's", new SimpleAdapter(this, weather, R.layout.list_complex_sub_with_image, 
				new String[] {ITEM_TITLE, ITEM_CAPTION, SUB_CAPTION, URL, ITEM_IMAGE }, 
				new int[] { R.id.list_complex_title, R.id.list_complex_caption, R.id.list_complex_sub_caption, R.id.list_complex_url, R.id.list_complex_image }));
		

		// ////////////
		// Versions //
		// ////////////

		final List<Map<String, ?>> version = new LinkedList<Map<String, ?>>();
		version.add(createSimple("Application Version", BuildUtils.getVersion(this)));
		version.add(createSimple("Database Version", BuildUtils.getDbVersion()));
		version.add(createSimple("SQLLite Version", BuildUtils.getSQLLiteVersion()));
		version.add(createSimple("Android Version", BuildUtils.androidVersion()));
		version.add(createSimple("Phone Model", BuildUtils.phoneModel()));

		this.adapter.addSection("Version", new SimpleAdapter(this, version, R.layout.list_simple,
				new String[] { ITEM_TITLE, ITEM_VERSION }, 
				new int[] { R.id.list_simple_title, R.id.list_simple_version }));

		//@formatter:on
		// //////////////////////////
		// Open URL Click handler //
		// //////////////////////////

		this.list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long duration) {
				@SuppressWarnings("unchecked")
				final HashMap<String, ?> item = (HashMap<String, ?>) AboutActivity.this.adapter.getItem(position);
				if (item.containsKey(URL) && null != item.get(URL)) {
					googleAnalyticsService.trackClickEvent(AboutActivity.this, item.get(URL).toString());
					Utils.openUrl(AboutActivity.this, item.get(URL).toString());
				}
			}
		});

		this.list.setAdapter(this.adapter);
		setContentView(this.list);
	}

	private Map<String, ?> createImageItem(final String title, final String caption, final String subCaption, final String url, final int imageId) {
		final Map<String, Object> item = new HashMap<String, Object>();
		item.put(ITEM_TITLE, title);
		item.put(ITEM_CAPTION, caption);
		item.put(SUB_CAPTION, subCaption);
		item.put(URL, url);
		item.put(ITEM_IMAGE, imageId);
		return item;
	}

	private Map<String, ?> createSimple(final Object title, final Object version) {
		final Map<String, Object> item = new HashMap<String, Object>();
		item.put(ITEM_TITLE, title);
		item.put(ITEM_VERSION, version);
		return item;
	}

}
