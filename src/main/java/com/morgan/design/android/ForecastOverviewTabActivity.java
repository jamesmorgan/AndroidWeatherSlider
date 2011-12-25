package com.morgan.design.android;

import static com.morgan.design.android.util.ObjectUtils.isNotNull;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.morgan.design.Constants;
import com.morgan.design.android.domain.ForcastEntry;
import com.morgan.design.android.domain.types.IconFactory;
import com.morgan.design.android.util.DateUtils;
import com.morgan.design.android.util.PreferenceUtils;
import com.morgan.design.android.util.Utils;
import com.weatherslider.morgan.design.R;

public class ForecastOverviewTabActivity extends Activity {

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forcast_overview_tab);

		final Bundle extras = getIntent().getExtras();
		if (isNotNull(extras)) {

			final ForcastEntry forcastEntry = (ForcastEntry) extras.getSerializable(Constants.FORCAST_ENTRY);
			final String tempUnit = Utils.abrev(PreferenceUtils.getTemperatureMode(this));

			final TextView description = (TextView) findViewById(R.id.description);
			description.setText(forcastEntry.getText());

			final ImageView weather_image = (ImageView) findViewById(R.id.weather_image);
			weather_image.setImageResource(IconFactory.getImageResourceFromCode(forcastEntry.getCode()));

			final TextView high_temp = (TextView) findViewById(R.id.high_temp);
			high_temp.setText("Highs of " + forcastEntry.getHigh() + tempUnit);

			final TextView low_temp = (TextView) findViewById(R.id.low_temp);
			low_temp.setText("Lows of " + forcastEntry.getLow() + tempUnit);

			final TextView date = (TextView) findViewById(R.id.date);
			date.setText(DateUtils.toSimpleDate(forcastEntry.getDate()));
		}
	}

}
