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
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.morgan.design.Constants;
import com.morgan.design.android.SimpleGestureFilter.SimpleGestureListener;
import com.morgan.design.android.adaptor.WOIEDAdaptor;
import com.morgan.design.android.dao.WeatherChoiceDao;
import com.morgan.design.android.domain.WOEIDEntry;
import com.morgan.design.android.domain.orm.WeatherChoice;
import com.morgan.design.android.repository.DatabaseHelper;
import com.morgan.design.android.service.StaticLookupService;
import com.weatherslider.morgan.design.R;

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

		final WeatherChoice choice = new WeatherChoice();
		choice.setWoeid(entry.getWoeid());
		choice.setCreatedDateTime(new Date());
		choice.setActive(true);
		choice.setCurrentLocationText(getSimpleLocation(entry));
		choice.setCurrentWeatherText("N/A");

		this.weatherDao.create(choice);

		final Bundle bundle = new Bundle();
		bundle.putSerializable(WEATHER_ID, choice.getId());

		startService(new Intent(this, StaticLookupService.class).putExtra(FROM_INACTIVE_LOCATION, true).putExtras(bundle));

		setResult(RESULT_OK);
		finish();
	}

	private String getSimpleLocation(final WOEIDEntry entry) {
		final String location = valueOrDefault(entry.getName(), "");
		return stringHasValue(location)
				? location + ", " + valueOrDefault(entry.getCountry(), "")
				: location;
	}
}
