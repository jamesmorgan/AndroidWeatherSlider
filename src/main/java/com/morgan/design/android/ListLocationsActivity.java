package com.morgan.design.android;

import static com.morgan.design.Constants.FROM_INACTIVE_LOCATION;
import static com.morgan.design.Constants.WEATHER_ID;
import static com.morgan.design.android.util.ObjectUtils.stringHasValue;
import static com.morgan.design.android.util.ObjectUtils.valueOrDefault;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.morgan.design.Constants;
import com.morgan.design.android.SimpleGestureFilter.SimpleGestureListener;
import com.morgan.design.android.adaptor.WOIEDAdaptor;
import com.morgan.design.android.dao.WeatherChoiceDao;
import com.morgan.design.android.dao.orm.WeatherChoice;
import com.morgan.design.android.domain.WOEIDEntry;
import com.morgan.design.android.factory.FlagIconFactory;
import com.morgan.design.android.repository.DatabaseHelper;
import com.morgan.design.android.service.StaticLookupService;
import com.morgan.design.android.util.Utils;
import com.morgan.design.weatherslider.R;

public class ListLocationsActivity extends OrmLiteBaseListActivity<DatabaseHelper> implements SimpleGestureListener {

	private WOIEDAdaptor adaptor;

	private List<WOEIDEntry> WOIEDlocations;
	private SimpleGestureFilter detector;
	protected WeatherChoiceDao weatherDao;

	@Override
	@SuppressWarnings("unchecked")
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.woied_list);
		this.detector = new SimpleGestureFilter(this, this);
		this.detector.setEnabled(true);
		this.weatherDao = new WeatherChoiceDao(getHelper());

		final Bundle extras = getIntent().getExtras();
		if (null != extras) {
			final Serializable serializable = extras.getSerializable(Constants.WOIED_LOCAITONS);
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

		Drawable flag = null;
		if (null != entry.getCountryCode()) {
			Integer flagCode = FlagIconFactory.getFlag(entry.getCountryCode());
			if (null != flagCode) {
				flag = getResources().getDrawable(flagCode);
			}
		}

		//@formatter:off
		new AlertDialog.Builder(this)
				.setIcon(flag)
				.setTitle(getString(R.string.alert_on_location_click_is_this_correct))
				.setMessage(createConfirmationText(entry))
				.setCancelable(true)
				.setPositiveButton(R.string.alert_yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int id) {
						loadWeatherDataForEntry(entry);
					}
				}).setNegativeButton(R.string.alert_no, Utils.cancel).create().show();
		//@formatter:on
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		super.finish();
	}

	private String createConfirmationText(final WOEIDEntry entry) {
		String admin = null;
		if ("" != entry.getAdmin1()) {
			admin = entry.getAdmin1();
		}
		else if ("" != entry.getAdmin2()) {
			admin = entry.getAdmin2();
		}

		String locality = null;
		if ("" != entry.getLocality1()) {
			locality = entry.getLocality1();

		}
		else if ("" != entry.getLocality2()) {
			locality = entry.getLocality2();
		}

		//@formatter:off
		// Name and place type
		// Country
		// Admin OR Locality
		return entry.getName() + " (" + entry.getPlaceTypeName() + ") \n"
				+ (entry.getCountry() + "\n") +
				(null != admin 
						? admin
						: null != locality 
								? locality : "");
		//@formatter:on
	}

	protected void loadWeatherDataForEntry(final WOEIDEntry entry) {

		final WeatherChoice choice = new WeatherChoice();
		choice.setWoeid(entry.getWoeid());
		choice.setCreatedDateTime(new Date());
		choice.setCurrentLocationText(getSimpleLocation(entry));
		choice.setCurrentWeatherText(getString(R.string.not_available));

		this.weatherDao.create(choice);

		final Bundle bundle = new Bundle();
		bundle.putSerializable(WEATHER_ID, choice.getId());

		startService(new Intent(this, StaticLookupService.class).putExtra(FROM_INACTIVE_LOCATION, true).putExtras(bundle));

		Intent intent = new Intent();
		intent.putExtra(WEATHER_ID, choice.getId());

		setResult(RESULT_OK, intent);
		finish();
	}

	private String getSimpleLocation(final WOEIDEntry entry) {
		final String location = valueOrDefault(entry.getName(), "");
		return stringHasValue(location) ? location + ", " + valueOrDefault(entry.getCountry(), "") : valueOrDefault(entry.getCountry(), "");
	}
}
