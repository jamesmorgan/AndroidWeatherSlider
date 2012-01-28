package com.morgan.design.android;

import static com.morgan.design.Constants.FORCAST_ENTRY;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.morgan.design.android.SimpleGestureFilter.SimpleGestureListener;
import com.morgan.design.android.domain.ForcastEntry;
import com.morgan.design.android.domain.types.IconFactory;
import com.morgan.design.android.domain.types.Temperature;
import com.morgan.design.android.repository.DatabaseHelper;
import com.morgan.design.android.util.DateUtils;
import com.morgan.design.android.util.PreferenceUtils;
import com.morgan.design.android.util.Utils;
import com.weatherslider.morgan.design.R;

public class TwoDayOverviewActivity extends OrmLiteBaseActivity<DatabaseHelper> implements SimpleGestureListener {

	private SimpleGestureFilter detector;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.two_day_overview);

		this.detector = new SimpleGestureFilter(this, this);
		this.detector.setEnabled(true);

		final Intent intent = getIntent();
		if (null != intent) {
			// Today
			if (intent.hasExtra(FORCAST_ENTRY + 0)) {
				final ForcastEntry forcastEntry = (ForcastEntry) intent.getSerializableExtra(FORCAST_ENTRY + 0);
				if (null != forcastEntry) {
					setToday(forcastEntry);
				}
			}

			// Tomo
			if (intent.hasExtra(FORCAST_ENTRY + 1)) {
				final ForcastEntry forcastEntry = (ForcastEntry) intent.getSerializableExtra(FORCAST_ENTRY + 1);
				if (null != forcastEntry) {
					setTomo(forcastEntry);
				}
			}
		}
	}

	private void setToday(ForcastEntry forcastEntry) {
		final String tempUnit = Temperature.withDegree(Utils.abrev(PreferenceUtils.getTemperatureMode(this)));

		final TextView today_day = (TextView) findViewById(R.id.today_day);
		today_day.setText(forcastEntry.getDayOfWeek()
			.full());

		final TextView description = (TextView) findViewById(R.id.today_description);
		description.setText(forcastEntry.getText());

		final ImageView weather_image = (ImageView) findViewById(R.id.today_weather_image);
		weather_image.setImageResource(IconFactory.getImageResourceFromCode(forcastEntry.getCode()));

		final TextView high_temp = (TextView) findViewById(R.id.today_high_temp);
		high_temp.setText(forcastEntry.getHigh() + tempUnit);

		final TextView low_temp = (TextView) findViewById(R.id.today_low_temp);
		low_temp.setText(forcastEntry.getLow() + tempUnit);

		final TextView date = (TextView) findViewById(R.id.today_date);
		date.setText(DateUtils.toSimpleDate(forcastEntry.getDate()));
	}

	private void setTomo(ForcastEntry forcastEntry) {
		final String tempUnit = Temperature.withDegree(Utils.abrev(PreferenceUtils.getTemperatureMode(this)));

		final TextView tomo_day = (TextView) findViewById(R.id.tomo_day);
		tomo_day.setText(forcastEntry.getDayOfWeek()
			.full());

		final TextView description = (TextView) findViewById(R.id.tomo_description);
		description.setText(forcastEntry.getText());

		final ImageView weather_image = (ImageView) findViewById(R.id.tomo_weather_image);
		weather_image.setImageResource(IconFactory.getImageResourceFromCode(forcastEntry.getCode()));

		final TextView high_temp = (TextView) findViewById(R.id.tomo_high_temp);
		high_temp.setText(forcastEntry.getHigh() + tempUnit);

		final TextView low_temp = (TextView) findViewById(R.id.tomo_low_temp);
		low_temp.setText(forcastEntry.getLow() + tempUnit);

		final TextView date = (TextView) findViewById(R.id.tomo_date);
		date.setText(DateUtils.toSimpleDate(forcastEntry.getDate()));
	}

	@Override
	public void onSwipe(final int direction) {
		switch (direction) {
			case SimpleGestureFilter.SWIPE_RIGHT:
				finish();
				break;
		}
	}

	@Override
	public void onDoubleTap() {
		// Do nothing
	}

	@Override
	public boolean dispatchTouchEvent(final MotionEvent me) {
		this.detector.onTouchEvent(me);
		return super.dispatchTouchEvent(me);
	}
}
